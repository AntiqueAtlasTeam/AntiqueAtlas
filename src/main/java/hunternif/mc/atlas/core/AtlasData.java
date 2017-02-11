package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MapDataPacket;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.ShortVec2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AtlasData extends WorldSavedData {
	private static final int VERSION = 2;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
	private static final String TAG_DIMENSION_ID = "qDimensionID";
	private static final String TAG_VISITED_CHUNKS = "qVisitedChunks";
	
	// Navigation
	private static final String TAG_BROWSING_X = "qBrowseX";
	private static final String TAG_BROWSING_Y = "qBrowseY";
	private static final String TAG_BROWSING_ZOOM = "qBrowseZoom";

	/** Maps dimension ID to biomeAnalyzer. */
	private final Map<Integer, IBiomeDetector> biomeAnalyzers = new HashMap<>();
	private final BiomeDetectorBase biomeDetectorOverworld = new BiomeDetectorBase();
	private final BiomeDetectorNether biomeDetectorNether = new BiomeDetectorNether();
	private final BiomeDetectorEnd biomeDetectorEnd = new BiomeDetectorEnd();

	/** This map contains, for each dimension, a map of chunks the player
	 * has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private final Map<Integer /*dimension ID*/, DimensionData> dimensionMap =
			new ConcurrentHashMap<>(2, 0.75f, 2);
	
	/** Set of players this Atlas data has been sent to. */
	private final Set<EntityPlayer> playersSentTo = new HashSet<>();
	
	private NBTTagCompound nbt;

	AtlasData(String key) {
		super(key);

		biomeDetectorOverworld.setScanPonds(AntiqueAtlasMod.settings.doScanPonds);
		setBiomeDetectorForDimension(0, biomeDetectorOverworld);
		setBiomeDetectorForDimension(-1, biomeDetectorNether);
		setBiomeDetectorForDimension(1, biomeDetectorEnd);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.nbt = compound;
		int version = compound.getInteger(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}

		NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.tagCount(); d++) {
			NBTTagCompound dimTag = dimensionMapList.getCompoundTagAt(d);
			int dimensionID = dimTag.getInteger(TAG_DIMENSION_ID);
			int[] intArray = dimTag.getIntArray(TAG_VISITED_CHUNKS);
			DimensionData dimData = getDimensionData(dimensionID);
			for (int i = 0; i < intArray.length; i += 3) {
				dimData.setTile(intArray[i], intArray[i+1], new Tile(intArray[i+2]));
            }

            double zoom = (double)dimTag.getInteger(TAG_BROWSING_ZOOM) / BrowsingPositionPacket.ZOOM_SCALE_FACTOR;
			if (zoom == 0) {
			    zoom = 0.5;
            }

			dimData.setBrowsingPosition(dimTag.getInteger(TAG_BROWSING_X),
					dimTag.getInteger(TAG_BROWSING_Y), zoom);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {		
		compound.setInteger(TAG_VERSION, VERSION);
		NBTTagList dimensionMapList = new NBTTagList();
		for (Entry<Integer, DimensionData> dimensionEntry : dimensionMap.entrySet()) {
			NBTTagCompound dimTag = new NBTTagCompound();
			dimTag.setInteger(TAG_DIMENSION_ID, dimensionEntry.getKey());
			DimensionData dimData = dimensionEntry.getValue();
			Map<ShortVec2, Tile> seenChunks = dimData.getSeenChunks();
			int[] intArray = new int[seenChunks.size()*3];
			int i = 0;
			for (Entry<ShortVec2, Tile> entry : seenChunks.entrySet()) {
				intArray[i++] = entry.getKey().x;
				intArray[i++] = entry.getKey().y;
				intArray[i++] = entry.getValue().biomeID;
			}
			dimTag.setIntArray(TAG_VISITED_CHUNKS, intArray);
			dimTag.setInteger(TAG_BROWSING_X, dimData.getBrowsingX());
			dimTag.setInteger(TAG_BROWSING_Y, dimData.getBrowsingY());
			dimTag.setInteger(TAG_BROWSING_ZOOM, (int)Math.round(dimData.getBrowsingZoom() * BrowsingPositionPacket.ZOOM_SCALE_FACTOR));
			dimensionMapList.appendTag(dimTag);
		}
		compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
		
		return compound;
	}

	private void setBiomeDetectorForDimension(int dimension, IBiomeDetector biomeAnalyzer) {
		biomeAnalyzers.put(dimension, biomeAnalyzer);
	}

	/** If not found, returns the analyzer for overworld. */
	private IBiomeDetector getBiomeDetectorForDimension(int dimension) {
		IBiomeDetector biomeAnalyzer = biomeAnalyzers.get(dimension);
		return biomeAnalyzer == null ? biomeDetectorOverworld : biomeAnalyzer;
	}

	public void updateMapAroundPlayer(EntityPlayer player) {
		// Update the actual map only so often:
		int newScanInterval = Math.round(AntiqueAtlasMod.settings.newScanInterval * 20);
		int rescanInterval = newScanInterval * AntiqueAtlasMod.settings.rescanRate;
		if (player.ticksExisted % newScanInterval != 0) {
			return;
		}

		int playerX = MathHelper.floor(player.posX) >> 4;
		int playerZ = MathHelper.floor(player.posZ) >> 4;
		ITileStorage seenChunks = this.getDimensionData(player.dimension);
		IBiomeDetector biomeDetector = getBiomeDetectorForDimension(player.dimension);
		int scanRadius = AntiqueAtlasMod.settings.scanRadius;

		// Look at chunks around in a circular area:
		for (double dx = -scanRadius; dx <= scanRadius; dx++) {
			for (double dz = -scanRadius; dz <= scanRadius; dz++) {
				if (dx*dx + dz*dz > scanRadius*scanRadius) {
					continue; // Outside the circle
				}
				int x = (int)(playerX + dx);
				int z = (int)(playerZ + dz);
				Tile oldTile = seenChunks.getTile(x, z);

				// Check if there's a custom tile at the location:
				int biomeId = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(player.dimension, x, z);
				// Custom tiles overwrite even the chunks already seen.

				// If there's no custom tile, check the actual chunk:
				if (biomeId == -1) {
					Chunk chunk = player.getEntityWorld().getChunkFromChunkCoords(x, z);
					// Force loading of chunk, if required:
					if (AntiqueAtlasMod.settings.forceChunkLoading && !chunk.isLoaded()) {
						player.getEntityWorld().getChunkProvider().provideChunk(x << 4, z << 4);
					}
					// Skip chunk if it hasn't loaded yet:
					if (!chunk.isLoaded()) {
						continue;
					}

					if (oldTile != null) {
						// If the chunk has been scanned previously, only re-scan it so often:
						if (!AntiqueAtlasMod.settings.doRescan || player.ticksExisted % rescanInterval != 0) {
							continue;
						}
						biomeId = biomeDetector.getBiomeID(chunk);
						if (biomeId == IBiomeDetector.NOT_FOUND) {
							// If the new tile is empty, remove the old one:
							this.removeTile(player.dimension, x, z);
						} else if (oldTile.biomeID != biomeId) {
							// Only update if the old tile's biome ID doesn't match the new one:
							this.setTile(player.dimension, x, z, new Tile(biomeId));
						}
					} else {
						// Scanning new chunk:
						biomeId = biomeDetector.getBiomeID(chunk);
						if (biomeId != IBiomeDetector.NOT_FOUND) {
							this.setTile(player.dimension, x, z, new Tile(biomeId));
						}
					}
				} else {
					// Only update the custom tile if it doesn't rewrite itself:
					if (oldTile == null || oldTile.biomeID != biomeId) {
						this.setTile(player.dimension, x, z, new Tile(biomeId));
						this.markDirty();
					}
				}

			}
		}
	}
	
	/** Puts a given tile into given map at specified coordinates and,
	 * if tileStitcher is present, sets appropriate sectors on adjacent tiles. */
	public void setTile(int dimension, int x, int y, Tile tile) {
		DimensionData dimData = getDimensionData(dimension);
		dimData.setTile(x, y, tile);
	}
	
	/** Returns the Tile previously set at given coordinates. */
    private Tile removeTile(int dimension, int x, int y) {
		DimensionData dimData = getDimensionData(dimension);
		return dimData.removeTile(x, y);
	}
	
	public Set<Integer> getVisitedDimensions() {
		return dimensionMap.keySet();
	}
	/** If this dimension is not yet visited, empty DimensionData will be created. */
	public DimensionData getDimensionData(int dimension) {
		return dimensionMap.computeIfAbsent(dimension, k -> new DimensionData(this, dimension));
	}

	public Map<ShortVec2, Tile> getSeenChunksInDimension(int dimension) {
		return getDimensionData(dimension).getSeenChunks();
	}
	
	/** The set of players this AtlasData has already been sent to. */
	public Collection<EntityPlayer> getSyncedPlayers() {
		return Collections.unmodifiableCollection(playersSentTo);
	}

	/** Whether this AtlasData has already been sent to the specified player. */
	public boolean isSyncedOnPlayer(EntityPlayer player) {
		return playersSentTo.contains(player);
	}

	/** Send all data to the player in several zipped packets. Called once
	 * during the first run of ItemAtals.onUpdate(). */
	public void syncOnPlayer(int atlasID, EntityPlayer player) {
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}
		// Before syncing make sure the changes are written to the nbt:
		writeToNBT(nbt);
		PacketDispatcher.sendTo(new MapDataPacket(atlasID, nbt), (EntityPlayerMP) player);
		Log.info("Sent Atlas #%d data to player %s", atlasID, player.getName());
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return dimensionMap.isEmpty();
	}
}
