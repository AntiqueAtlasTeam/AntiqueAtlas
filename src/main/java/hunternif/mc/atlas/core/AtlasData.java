package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MapDataPacket;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AtlasData extends WorldSavedData {
	public static final int VERSION = 3;
	public static final String TAG_VERSION = "aaVersion";
	public static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
	public static final String TAG_DIMENSION_ID = "qDimensionID";
	public static final String TAG_VISITED_CHUNKS = "qVisitedChunks";

	// Navigation
	public static final String TAG_BROWSING_X = "qBrowseX";
	public static final String TAG_BROWSING_Y = "qBrowseY";
	public static final String TAG_BROWSING_ZOOM = "qBrowseZoom";

	/** Maps dimension ID to biomeAnalyzer. */
	private final Map<DimensionType, IBiomeDetector> biomeAnalyzers = new HashMap<>();
	private final BiomeDetectorBase biomeDetectorOverworld = new BiomeDetectorBase();
	private final BiomeDetectorNether biomeDetectorNether = new BiomeDetectorNether();
	private final BiomeDetectorEnd biomeDetectorEnd = new BiomeDetectorEnd();

	/** This map contains, for each dimension, a map of chunks the player
	 * has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private final Map<DimensionType /*dimension ID*/, DimensionData> dimensionMap =
			new ConcurrentHashMap<>(2, 0.75f, 2);

	/** Set of players this Atlas data has been sent to. */
	private final Set<PlayerEntity> playersSentTo = new HashSet<>();

	private CompoundNBT nbt;

	public AtlasData(String key) {
		super(key);

		biomeDetectorOverworld.setScanPonds(SettingsConfig.doScanPonds);
		biomeDetectorOverworld.setScanRavines(SettingsConfig.doScanRavines);
		setBiomeDetectorForDimension(DimensionType.OVERWORLD, biomeDetectorOverworld);
		setBiomeDetectorForDimension(DimensionType.THE_NETHER, biomeDetectorNether);
		setBiomeDetectorForDimension(DimensionType.THE_END, biomeDetectorEnd);
	}

	@Override
	public void read(CompoundNBT compound) {
		this.nbt = compound;
		int version = compound.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d. Updating.", version, VERSION);
			readFromNBT2(compound);
			return;
		}

		ListNBT dimensionMapList = compound.getList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.size(); d++) {
			CompoundNBT dimTag = dimensionMapList.getCompound(d);
			DimensionType dimensionID;
			if (dimTag.contains(TAG_DIMENSION_ID, Constants.NBT.TAG_ANY_NUMERIC)) {
				dimensionID = Registry.DIMENSION_TYPE.getByValue(dimTag.getInt(TAG_DIMENSION_ID));
			} else {
				dimensionID = Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(dimTag.getString(TAG_DIMENSION_ID)));
			}
			ListNBT dimensionTag = (ListNBT) dimTag.get(TAG_VISITED_CHUNKS);
			DimensionData dimData = getDimensionData(dimensionID);
			dimData.readFromNBT(dimensionTag);
			double zoom = (double)dimTag.getInt(TAG_BROWSING_ZOOM) / BrowsingPositionPacket.ZOOM_SCALE_FACTOR;
			if (zoom == 0) zoom = 0.5;
			dimData.setBrowsingPosition(dimTag.getInt(TAG_BROWSING_X),
					dimTag.getInt(TAG_BROWSING_Y), zoom);
		}
	}

	/**Reads from NBT version 2. This is designed to allow easy upgrading to version 3.*/
	public void readFromNBT2(CompoundNBT compound) {
		this.nbt = compound;
		int version = compound.getInt(TAG_VERSION);
		if (version < 2) {
			Log.warn("Loading map with version 2 failed");
			this.markDirty();
			return;
		}
		ListNBT dimensionMapList = compound.getList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.size(); d++) {
			CompoundNBT dimTag = dimensionMapList.getCompound(d);
			DimensionType dimensionID;
			if (dimTag.contains(TAG_DIMENSION_ID, Constants.NBT.TAG_ANY_NUMERIC)) {
				dimensionID = Registry.DIMENSION_TYPE.getByValue(dimTag.getInt(TAG_DIMENSION_ID));
			} else {
				dimensionID = Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(dimTag.getString(TAG_DIMENSION_ID)));
			}
			int[] intArray = dimTag.getIntArray(TAG_VISITED_CHUNKS);
			DimensionData dimData = getDimensionData(dimensionID);
			for (int i = 0; i < intArray.length; i += 3) {
				if (dimData.getTile(intArray[i], intArray[i+1]) != null){
					Log.warn("Duplicate tile at "+ intArray[i] + ", " + intArray[i]);
				}
				// TODO FABRIC remove int
				dimData.setTile(intArray[i], intArray[i+1], TileKindFactory.get(intArray[i + 2]));
			}
			Log.info("Updated " + intArray.length/3 + " chunks");
			double zoom = (double)dimTag.getInt(TAG_BROWSING_ZOOM) / BrowsingPositionPacket.ZOOM_SCALE_FACTOR;
			if (zoom == 0) zoom = 0.5;
			dimData.setBrowsingPosition(dimTag.getInt(TAG_BROWSING_X),
					dimTag.getInt(TAG_BROWSING_Y), zoom);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		return writeToNBT(compound, true);
	}

	public CompoundNBT writeToNBT(CompoundNBT compound, boolean includeTileData) {
		ListNBT dimensionMapList = new ListNBT();
		compound.putInt(TAG_VERSION, VERSION);
		for (Entry<DimensionType, DimensionData> dimensionEntry : dimensionMap.entrySet()) {
			CompoundNBT dimTag = new CompoundNBT();
			dimTag.putString(TAG_DIMENSION_ID, Registry.DIMENSION_TYPE.getKey(dimensionEntry.getKey()).toString());
			DimensionData dimData = dimensionEntry.getValue();
			if (includeTileData){
				dimTag.put(TAG_VISITED_CHUNKS, dimData.writeToNBT());
			}
			dimTag.putInt(TAG_BROWSING_X, dimData.getBrowsingX());
			dimTag.putInt(TAG_BROWSING_Y, dimData.getBrowsingY());
			dimTag.putInt(TAG_BROWSING_ZOOM, (int)Math.round(dimData.getBrowsingZoom() * BrowsingPositionPacket.ZOOM_SCALE_FACTOR));
			dimensionMapList.add(dimTag);
		}
		compound.put(TAG_DIMENSION_MAP_LIST, dimensionMapList);

		return compound;
	}

	private void setBiomeDetectorForDimension(DimensionType dimension, IBiomeDetector biomeAnalyzer) {
		biomeAnalyzers.put(dimension, biomeAnalyzer);
	}

	/** If not found, returns the analyzer for overworld. */
	private IBiomeDetector getBiomeDetectorForDimension(DimensionType dimension) {
		IBiomeDetector biomeAnalyzer = biomeAnalyzers.get(dimension);

		return biomeAnalyzer == null ? biomeDetectorOverworld : biomeAnalyzer;
	}

	/**Updates map data around player
	 *
	 * @return A set of the new tiles, mostly so the server can synch those with relevant clients.*/
	public Collection<TileInfo> updateMapAroundPlayer(PlayerEntity player) {
		// Update the actual map only so often:
		int newScanInterval = (int) Math.round(SettingsConfig.newScanInterval * 20);
		int rescanInterval = newScanInterval * SettingsConfig.rescanRate;

		if (player.getEntityWorld().getGameTime() % newScanInterval != 0) {
			return Collections.emptyList(); //no new tiles
		}

		ArrayList<TileInfo> updatedTiles = new ArrayList<TileInfo>();

		int playerX = MathHelper.floor(player.getPosX()) >> 4;
		int playerZ = MathHelper.floor(player.getPosZ()) >> 4;
		ITileStorage seenChunks = this.getDimensionData(player.dimension);
		IBiomeDetector biomeDetector = getBiomeDetectorForDimension(player.dimension);
		int scanRadius = SettingsConfig.scanRadius;

		final boolean rescanRequired = SettingsConfig.doRescan && player.getEntityWorld().getGameTime() % rescanInterval == 0;
		int scanRadiusSq = scanRadius*scanRadius;

		// Look at chunks around in a circular area:
		for (double dx = -scanRadius; dx <= scanRadius; dx++) {
			for (double dz = -scanRadius; dz <= scanRadius; dz++) {
				if (dx*dx + dz*dz > scanRadiusSq) {
					continue; // Outside the circle
				}

				int x = (int)(playerX + dx);
				int z = (int)(playerZ + dz);
				TileKind oldTile = seenChunks.getTile(x, z);
				TileKind tile = null;

				// Check if there's a custom tile at the location:
				// Custom tiles overwrite even the chunks already seen.
				int customTileId = AntiqueAtlasMod.extBiomeData.getData().getBiomeAt(player.dimension, x, z);
				if (customTileId != -1) {
					tile = TileKindFactory.get(customTileId);
				}

				// If there's no custom tile, check the actual chunk:
				if (tile == null) {
					// If the chunk has been scanned previously, only re-scan it so often:
					if (oldTile != null && !rescanRequired) {
						continue;
					}

					// TODO FABRIC: forceChunkLoading crashes here
					IChunk chunk = player.getEntityWorld().getChunk(x, z, ChunkStatus.FULL, SettingsConfig.forceChunkLoading);

					// Skip chunk if it hasn't loaded yet:
					if (chunk == null) {
						continue;
					}

					if (oldTile != null) {
						tile = biomeDetector.getBiomeID(player.getEntityWorld(), chunk);

						if (tile == null) {
							// If the new tile is empty, remove the old one:
							this.removeTile(player.dimension, x, z);
						} else if (oldTile != tile) {
							// Only update if the old tile's biome ID doesn't match the new one:
							this.setTile(player.dimension, x, z, tile);
							updatedTiles.add(new TileInfo(x, z, tile));
						}
					} else {
						// Scanning new chunk:
						tile = biomeDetector.getBiomeID(player.getEntityWorld(), chunk);
						if (tile != null) {
							this.setTile(player.dimension, x, z, tile);
							updatedTiles.add(new TileInfo(x, z, tile));
						}
					}
				} else {
					// Only update the custom tile if it doesn't rewrite itself:
					if (oldTile == null || oldTile != tile) {
						this.setTile(player.dimension, x, z, tile);
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
	public void setTile(DimensionType dimension, int x, int y, TileKind tile) {
		DimensionData dimData = getDimensionData(dimension);
		dimData.setTile(x, y, tile);
	}

	/** Returns the Tile previously set at given coordinates. */
    private TileKind removeTile(DimensionType dimension, int x, int y) {
		DimensionData dimData = getDimensionData(dimension);
		return dimData.removeTile(x, y);
	}

	public Set<DimensionType> getVisitedDimensions() {
		return dimensionMap.keySet();
	}

	/* TODO: Packet Rework
	 *   Dimension data should check the server for updates*/
	/** If this dimension is not yet visited, empty DimensionData will be created. */
	public DimensionData getDimensionData(DimensionType dimension) {
		return dimensionMap.computeIfAbsent(dimension, k -> new DimensionData(this, dimension));
	}

	public Map<ShortVec2, TileKind> getSeenChunksInDimension(DimensionType dimension) {
		return getDimensionData(dimension).getSeenChunks();
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
			nbt = new CompoundNBT();
		}
		// Before syncing make sure the changes are written to the nbt.
		// Do not include dimension tile data.  This will happen later.
		writeToNBT(nbt, false);
		PacketDispatcher.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MapDataPacket(atlasID, nbt));

		for (DimensionType i : dimensionMap.keySet()){
			dimensionMap.get(i).syncOnPlayer(atlasID, player);
		}

		Log.info("Sent Atlas #%d data to player %s", atlasID, player.getCommandSource().getName());
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return dimensionMap.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AtlasData)) return false;
		AtlasData other = (AtlasData) obj;
		// TODO: This doesn't handle disjoint DimensionType keysets of equal size
		if (other.dimensionMap.size()!=dimensionMap.size()) return false;
		for (DimensionType key : dimensionMap.keySet()){
			if (!dimensionMap.get(key).equals(other.dimensionMap.get(key))) return false;
		}
		return true;
	}
}
