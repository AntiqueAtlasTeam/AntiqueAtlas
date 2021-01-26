package hunternif.mc.impl.atlas.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.forge.NbtType;
import hunternif.mc.impl.atlas.network.packet.s2c.play.MapDataS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.ShortVec2;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.storage.WorldSavedData;

public class AtlasData extends WorldSavedData {
	public static final int VERSION = 4;
	public static final String TAG_VERSION = "aaVersion";
	public static final String TAG_WORLD_MAP_LIST = "qWorldMap";
	public static final String TAG_WORLD_ID = "qWorldID";
	public static final String TAG_VISITED_CHUNKS = "qVisitedChunks";

	// Navigation
	public static final String TAG_BROWSING_X = "qBrowseX";
	public static final String TAG_BROWSING_Y = "qBrowseY";
	public static final String TAG_BROWSING_ZOOM = "qBrowseZoom";

	/** Maps dimension ID to biomeAnalyzer. */
	private final Map<RegistryKey<World>, IBiomeDetector> biomeAnalyzers = new HashMap<>();
	private final BiomeDetectorBase biomeDetectorOverworld = new BiomeDetectorBase();
	private final BiomeDetectorNether biomeDetectorNether = new BiomeDetectorNether();
	private final BiomeDetectorEnd biomeDetectorEnd = new BiomeDetectorEnd();

	/** This map contains, for each dimension, a map of chunks the player
	 * has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private final Map<RegistryKey<World>, WorldData> worldMap =
			new ConcurrentHashMap<>(2, 0.75f, 2);

	/** Set of players this Atlas data has been sent to. */
	private final Set<PlayerEntity> playersSentTo = new HashSet<>();

	private CompoundNBT nbt;

	public AtlasData(String key) {
		super(key);

		biomeDetectorOverworld.setScanPonds(AntiqueAtlasConfig.doScanPonds.get());
		biomeDetectorOverworld.setScanRavines(AntiqueAtlasConfig.doScanRavines.get());
		setBiomeDetectorForWorld(World.OVERWORLD, biomeDetectorOverworld);
		setBiomeDetectorForWorld(World.THE_NETHER, biomeDetectorNether);
		setBiomeDetectorForWorld(World.THE_END, biomeDetectorEnd);
	}

	@Override
	public void read(CompoundNBT compound) {
		this.nbt = compound;
		int version = compound.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d.", version, VERSION);
			return;
		}

		ListNBT worldMapList = compound.getList(TAG_WORLD_MAP_LIST, NbtType.CompoundNBT);
		for (int d = 0; d < worldMapList.size(); d++) {
			CompoundNBT worldTag = worldMapList.getCompound(d);
			RegistryKey<World> worldID;
			worldID = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(worldTag.getString(TAG_WORLD_ID)));
			ListNBT dimensionTag = (ListNBT) worldTag.get(TAG_VISITED_CHUNKS);
			WorldData dimData = getWorldData(worldID);
			dimData.readFromNBT(dimensionTag);
			double zoom = worldTag.getDouble(TAG_BROWSING_ZOOM);
			if (zoom == 0) zoom = 0.5;
			dimData.setBrowsingPosition(worldTag.getInt(TAG_BROWSING_X),
					worldTag.getInt(TAG_BROWSING_Y), zoom);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		return writeToNBT(compound, true);
	}

	public CompoundNBT writeToNBT(CompoundNBT compound, boolean includeTileData) {
		ListNBT dimensionMapList = new ListNBT();
		compound.putInt(TAG_VERSION, VERSION);
		for (Entry<RegistryKey<World>, WorldData> dimensionEntry : worldMap.entrySet()) {
			CompoundNBT dimTag = new CompoundNBT();
			dimTag.putString(TAG_WORLD_ID, dimensionEntry.getKey().getLocation().toString());
			WorldData dimData = dimensionEntry.getValue();
			if (includeTileData){
				dimTag.put(TAG_VISITED_CHUNKS, dimData.writeToNBT());
			}
			dimTag.putInt(TAG_BROWSING_X, dimData.getBrowsingX());
			dimTag.putInt(TAG_BROWSING_Y, dimData.getBrowsingY());
			dimTag.putDouble(TAG_BROWSING_ZOOM,dimData.getBrowsingZoom());
			dimensionMapList.add(dimTag);
		}
		compound.put(TAG_WORLD_MAP_LIST, dimensionMapList);

		return compound;
	}

	private void setBiomeDetectorForWorld(RegistryKey<World> world, IBiomeDetector biomeAnalyzer) {
		biomeAnalyzers.put(world, biomeAnalyzer);
	}

	/** If not found, returns the analyzer for overworld. */
	private IBiomeDetector getBiomeDetectorForWorld(RegistryKey<World> dimension) {
		IBiomeDetector biomeAnalyzer = biomeAnalyzers.get(dimension);

		return biomeAnalyzer == null ? biomeDetectorOverworld : biomeAnalyzer;
	}

	/**Updates map data around player
	 *
	 * @return A set of the new tiles, mostly so the server can sync those with relevant clients.*/
	public Collection<TileInfo> updateMapAroundPlayer(PlayerEntity player) {
		// Update the actual map only so often:
		int newScanInterval = Math.round(AntiqueAtlasConfig.newScanInterval.get().floatValue() * 20);

		if (player.getEntityWorld().getGameTime() % newScanInterval != 0) {
			return Collections.emptyList(); //no new tiles
		}

		ArrayList<TileInfo> updatedTiles = new ArrayList<>();

		int rescanInterval = newScanInterval * AntiqueAtlasConfig.rescanRate.get();
		boolean rescanRequired = AntiqueAtlasConfig.doRescan.get() && player.getEntityWorld().getGameTime() % rescanInterval == 0;

		int scanRadius = AntiqueAtlasConfig.scanRadius.get();

		// Look at chunks around in a circular area:
		for (int dx = -scanRadius; dx <= scanRadius; dx++) {
			for (int dz = -scanRadius; dz <= scanRadius; dz++) {
				if (dx*dx + dz*dz > scanRadius*scanRadius) {
					continue; // Outside the circle
				}

				int chunkX = player.chunkCoordX + dx;
				int chunkZ = player.chunkCoordZ + dz;

				TileInfo update = updateMapChunk(player.getEntityWorld(), chunkX, chunkZ, rescanRequired);
				if(update != null) {
					updatedTiles.add(update);
				}
			}
		}
		return updatedTiles;
	}

	private TileInfo updateMapChunk(World world, int x, int z, boolean rescanRequired)
	{
		ITileStorage seenChunks = this.getWorldData(world.getDimensionKey());

		ResourceLocation oldTile = seenChunks.getTile(x, z);
		ResourceLocation tile = null;

		// Check if there's a custom tile at the location:
		// Custom tiles overwrite even the chunks already seen.
		tile = AntiqueAtlasMod.tileData.getData(world).getTile(x, z);

		// If there's no custom tile, check the actual chunk:
		if (tile == null) {
			// If the chunk has been scanned previously, only re-scan it so often:
			if (oldTile != null && !rescanRequired) {
				return null;
			}

			// TODO FABRIC: forceChunkLoading crashes here
			IChunk chunk = world.getChunk(x, z, ChunkStatus.FULL, AntiqueAtlasConfig.forceChunkLoading.get());

			// Skip chunk if it hasn't loaded yet:
			if (chunk == null) {
				return null;
			}

			IBiomeDetector biomeDetector = getBiomeDetectorForWorld(world.getDimensionKey());
			tile = biomeDetector.getBiomeID(world, chunk);

			if (oldTile != null) {
				if (tile == null) {
					// If the new tile is empty, remove the old one:
					this.removeTile(world.getDimensionKey(), x, z);
					// TODO should this also return a TileInfo?
				} else if (!oldTile.equals(tile)) {
					// Only update if the old tile's biome ID doesn't match the new one:
					this.setTile(world.getDimensionKey(), x, z, tile);
					return new TileInfo(x, z, tile);
				}
			} else {
				// Scanning new chunk:
				if (tile != null) {
					this.setTile(world.getDimensionKey(), x, z, tile);
					return new TileInfo(x, z, tile);
				}
			}
		} else {
			// Only update the custom tile if it doesn't rewrite itself:
			if (oldTile == null || !oldTile.equals(tile)) {
				this.setTile(world.getDimensionKey(), x, z, tile);
				this.markDirty();
				return new TileInfo(x, z, tile);
			}
		}

		return null;
	}

	/** Puts a given tile into given map at specified coordinates and,
	 * if tileStitcher is present, sets appropriate sectors on adjacent tiles. */
	public void setTile(RegistryKey<World> world, int x, int y, ResourceLocation tile) {
		WorldData worldData = getWorldData(world);
		worldData.setTile(x, y, tile);
	}

	/** Returns the Tile previously set at given coordinates. */
	private ResourceLocation removeTile(RegistryKey<World> world, int x, int y) {
		WorldData dimData = getWorldData(world);
		return dimData.removeTile(x, y);
	}

	public Set<RegistryKey<World>> getVisitedWorlds() {
		return worldMap.keySet();
	}

	/* TODO: Packet Rework
	 *   Dimension data should check the server for updates*/
	/** If this dimension is not yet visited, empty DimensionData will be created. */
	public WorldData getWorldData(RegistryKey<World> world) {
		return worldMap.computeIfAbsent(world, k -> new WorldData(this, world));
	}

	public Map<ShortVec2, ResourceLocation> getSeenChunksInDimension(RegistryKey<World> world) {
		return getWorldData(world).getSeenChunks();
	}

	/** The set of players this AtlasData has already been sent to. */
	public Collection<PlayerEntity> getSyncedPlayers() {
		return Collections.unmodifiableCollection(playersSentTo);
	}

	/** Whether this AtlasData has already been sent to the specified player. */
	public boolean isSyncedOnPlayer(PlayerEntity player) {
		return playersSentTo.contains(player);
	}

	/** Send all data to the player in several zipped packets. Called once
	 * during the first run of ItemAtlas.onUpdate(). */
	public void syncOnPlayer(int atlasID, PlayerEntity player) {
		if (nbt == null) {
			nbt = new CompoundNBT();
		}
		// Before syncing make sure the changes are written to the nbt.
		// Do not include dimension tile data.  This will happen later.
		writeToNBT(nbt, false);
		new MapDataS2CPacket(atlasID, nbt).send((ServerPlayerEntity) player);

		for (RegistryKey<World> world : worldMap.keySet()){
			worldMap.get(world).syncOnPlayer(atlasID, player);
		}

		Log.info("Sent Atlas #%d data to player %s", atlasID, player.getCommandSource().getName());
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return worldMap.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AtlasData)) return false;
		AtlasData other = (AtlasData) obj;
		// TODO: This doesn't handle disjoint DimensionType keysets of equal size
		if (other.worldMap.size()!= worldMap.size()) return false;
		for (RegistryKey<World> key : worldMap.keySet()){
			if (!worldMap.get(key).equals(other.worldMap.get(key))) return false;
		}
		return true;
	}
}
