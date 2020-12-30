package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.s2c.play.MapDataS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.ShortVec2;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AtlasData extends PersistentState {
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

	private CompoundTag nbt;

	public AtlasData(String key) {
		super(key);

		biomeDetectorOverworld.setScanPonds(AntiqueAtlasMod.CONFIG.doScanPonds);
		biomeDetectorOverworld.setScanRavines(AntiqueAtlasMod.CONFIG.doScanRavines);
		setBiomeDetectorForDimension(World.OVERWORLD, biomeDetectorOverworld);
		setBiomeDetectorForDimension(World.NETHER, biomeDetectorNether);
		setBiomeDetectorForDimension(World.END, biomeDetectorEnd);
	}

	@Override
	public void fromTag(CompoundTag compound) {
		this.nbt = compound;
		int version = compound.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d.", version, VERSION);
			return;
		}

		ListTag worldMapList = compound.getList(TAG_WORLD_MAP_LIST, NbtType.COMPOUND);
		for (int d = 0; d < worldMapList.size(); d++) {
			CompoundTag worldTag = worldMapList.getCompound(d);
			RegistryKey<World> worldID;
			worldID = RegistryKey.of(Registry.DIMENSION, new Identifier(worldTag.getString(TAG_WORLD_ID)));
			ListTag dimensionTag = (ListTag) worldTag.get(TAG_VISITED_CHUNKS);
			WorldData dimData = getWorldData(worldID);
			dimData.readFromNBT(dimensionTag);
			double zoom = worldTag.getDouble(TAG_BROWSING_ZOOM);
			if (zoom == 0) zoom = 0.5;
			dimData.setBrowsingPosition(worldTag.getInt(TAG_BROWSING_X),
					worldTag.getInt(TAG_BROWSING_Y), zoom);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		return writeToNBT(compound, true);
	}

	public CompoundTag writeToNBT(CompoundTag compound, boolean includeTileData) {
		ListTag dimensionMapList = new ListTag();
		compound.putInt(TAG_VERSION, VERSION);
		for (Entry<RegistryKey<World>, WorldData> dimensionEntry : worldMap.entrySet()) {
			CompoundTag dimTag = new CompoundTag();
			dimTag.putString(TAG_WORLD_ID, dimensionEntry.getKey().getValue().toString());
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

	private void setBiomeDetectorForDimension(RegistryKey<World> dimension, IBiomeDetector biomeAnalyzer) {
		biomeAnalyzers.put(dimension, biomeAnalyzer);
	}

	/** If not found, returns the analyzer for overworld. */
	private IBiomeDetector getBiomeDetectorForDimension(RegistryKey<World> dimension) {
		IBiomeDetector biomeAnalyzer = biomeAnalyzers.get(dimension);

		return biomeAnalyzer == null ? biomeDetectorOverworld : biomeAnalyzer;
	}

	/**Updates map data around player
	 *
	 * @return A set of the new tiles, mostly so the server can sync those with relevant clients.*/
	public Collection<TileInfo> updateMapAroundPlayer(PlayerEntity player) {
		// Update the actual map only so often:
		int newScanInterval = Math.round(AntiqueAtlasMod.CONFIG.newScanInterval * 20);
		int rescanInterval = newScanInterval * AntiqueAtlasMod.CONFIG.rescanRate;

		if (player.getEntityWorld().getTime() % newScanInterval != 0) {
			return Collections.emptyList(); //no new tiles
		}

		ArrayList<TileInfo> updatedTiles = new ArrayList<>();

		int playerX = MathHelper.floor(player.getX()) >> 4;
		int playerZ = MathHelper.floor(player.getZ()) >> 4;
		ITileStorage seenChunks = this.getWorldData(player.getEntityWorld().getRegistryKey());
		IBiomeDetector biomeDetector = getBiomeDetectorForDimension(player.getEntityWorld().getRegistryKey());
		int scanRadius = AntiqueAtlasMod.CONFIG.scanRadius;

		final boolean rescanRequired = AntiqueAtlasMod.CONFIG.doRescan && player.getEntityWorld().getTime() % rescanInterval == 0;
		int scanRadiusSq = scanRadius*scanRadius;

		// Look at chunks around in a circular area:
		for (double dx = -scanRadius; dx <= scanRadius; dx++) {
			for (double dz = -scanRadius; dz <= scanRadius; dz++) {
				if (dx*dx + dz*dz > scanRadiusSq) {
					continue; // Outside the circle
				}

				int x = (int)(playerX + dx);
				int z = (int)(playerZ + dz);
				Identifier oldTile = seenChunks.getTile(x, z);
				Identifier tile = null;

				// Check if there's a custom tile at the location:
				// Custom tiles overwrite even the chunks already seen.
				tile = AntiqueAtlasMod.tileData.getData().getTile(player.getEntityWorld().getRegistryKey(), x, z);

				// If there's no custom tile, check the actual chunk:
				if (tile == null) {
					// If the chunk has been scanned previously, only re-scan it so often:
					if (oldTile != null && !rescanRequired) {
						continue;
					}

					// TODO FABRIC: forceChunkLoading crashes here
					Chunk chunk = player.getEntityWorld().getChunk(x, z, ChunkStatus.FULL, AntiqueAtlasMod.CONFIG.forceChunkLoading);

					// Skip chunk if it hasn't loaded yet:
					if (chunk == null) {
						continue;
					}

					if (oldTile != null) {
						tile = biomeDetector.getBiomeID(player.getEntityWorld(), chunk);

						if (tile == null) {
							// If the new tile is empty, remove the old one:
							this.removeTile(player.getEntityWorld().getRegistryKey(), x, z);
						} else if (oldTile != tile) {
							// Only update if the old tile's biome ID doesn't match the new one:
							this.setTile(player.getEntityWorld().getRegistryKey(), x, z, tile);
							updatedTiles.add(new TileInfo(x, z, tile));
						}
					} else {
						// Scanning new chunk:
						tile = biomeDetector.getBiomeID(player.getEntityWorld(), chunk);
						if (tile != null) {
							this.setTile(player.getEntityWorld().getRegistryKey(), x, z, tile);
							updatedTiles.add(new TileInfo(x, z, tile));
						}
					}
				} else {
					// Only update the custom tile if it doesn't rewrite itself:
					if (oldTile == null || oldTile != tile) {
						this.setTile(player.getEntityWorld().getRegistryKey(), x, z, tile);
						updatedTiles.add(new TileInfo(x, z, tile));
						this.markDirty();
					}
				}

			}
		}
		return updatedTiles;
	}

	/** Puts a given tile into given map at specified coordinates and,
	 * if tileStitcher is present, sets appropriate sectors on adjacent tiles. */
	public void setTile(RegistryKey<World> world, int x, int y, Identifier tile) {
		WorldData worldData = getWorldData(world);
		worldData.setTile(x, y, tile);
	}

	/** Returns the Tile previously set at given coordinates. */
	private Identifier removeTile(RegistryKey<World> world, int x, int y) {
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

	public Map<ShortVec2, Identifier> getSeenChunksInDimension(RegistryKey<World> world) {
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
	 * during the first run of ItemAtals.onUpdate(). */
	public void syncOnPlayer(int atlasID, PlayerEntity player) {
		if (nbt == null) {
			nbt = new CompoundTag();
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
