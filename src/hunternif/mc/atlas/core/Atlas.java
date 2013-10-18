package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer.BiomeFlag;
import hunternif.mc.atlas.gui.MapTile;
import hunternif.mc.atlas.gui.MapTileStitcher;
import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.util.ShortVec2;
import hunternif.mc.atlas.util.ShortVec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class Atlas {
	private static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
	private static final String TAG_DIMENSION_ID = "qDimensionID";
	private static final String TAG_VISITED_CHUNKS = "qVisitedChunks";

	/** In [chunks] */
	public static double LOOK_RADIUS = 8;
	/** In [ticks] */
	public static int UPDATE_INTERVAL = 20;
	
	/** This map contains, for each dimension, a map of chunks the info.getPlayer() has seen.
	 * CAREFUL! Don't chunk coordinates that are already put in the map! */
	private Map<Integer /*dimension ID*/, Map<ShortVec2, MapTile>> dimensionMap =
			new ConcurrentHashMap<Integer, Map<ShortVec2, MapTile>>();
	/** All chunks that the info.getPlayer() has actually visited. This set is not saved
	 * to NBT to force updating all seen chunks when the world is reloaded.
	 * The 'y' in IntVec3 represents dimension id.
	 * CAREFUL! Don't change coordinates that are already put in the map!*/
	private transient Set<ShortVec3> visitedChunks = new HashSet<ShortVec3>();

	private MapTileStitcher tileStitcher;
	private ChunkBiomeAnalyzer biomeAnalyzer;

	public PlayerInfo info;

	public Atlas(PlayerInfo info, ChunkBiomeAnalyzer biomeAnalyzer, MapTileStitcher tileStitcher) {
		this.info = info;
		this.biomeAnalyzer = biomeAnalyzer;
		this.tileStitcher = tileStitcher;
	}

	public void saveToNBT(NBTTagCompound compound) {
		// Write visited chunks' coordinates:
		NBTTagList dimensionMapList = new NBTTagList();
		for (Entry<Integer, Map<ShortVec2, MapTile>> dimensionEntry : dimensionMap.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(TAG_DIMENSION_ID, dimensionEntry.getKey().intValue());
			Map<ShortVec2, MapTile> seenChunks = dimensionEntry.getValue();
			int[] intArray = new int[seenChunks.size()*3];
			int i = 0;
			for (Entry<ShortVec2, MapTile> entry : seenChunks.entrySet()) {
				intArray[i++] = entry.getKey().x;
				intArray[i++] = entry.getKey().y;
				intArray[i++] = entry.getValue().biomeID;
			}
			tag.setIntArray(TAG_VISITED_CHUNKS, intArray);
			dimensionMapList.appendTag(tag);
		}
		compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
	}

	public void loadFromNBT(NBTTagCompound compound) {
		// Read visited chunks' coordinates:
		NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST);
		for (int d = 0; d < dimensionMapList.tagCount(); d++) {
			NBTTagCompound tag = (NBTTagCompound) dimensionMapList.tagAt(d);
			int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
			Map<ShortVec2, MapTile> seenChunks = new ConcurrentHashMap<ShortVec2, MapTile>();
			int[] intArray = tag.getIntArray(TAG_VISITED_CHUNKS);
			for (int i = 0; i < intArray.length; i += 3) {
				ShortVec2 coords = new ShortVec2(intArray[i], intArray[i+1]);
				BiomeGenBase biome = BiomeGenBase.biomeList[intArray[i+2]];
				putTile(seenChunks, coords, new MapTile(biome));
			}
			dimensionMap.put(Integer.valueOf(dimensionID), seenChunks);
		}
	}

	public Set<Integer> getVisitedDimensions() {
		return dimensionMap.keySet();
	}
	public Map<ShortVec2, MapTile> getSeenChunksInCurrentDimension() {
		return getSeenChunksInDimension(info.getPlayer().dimension);
	}
	public Map<ShortVec2, MapTile> getSeenChunksInDimension(int dimension) {
		Map<ShortVec2, MapTile> seenChunks = dimensionMap.get(Integer.valueOf(dimension));
		if (seenChunks == null) {
			seenChunks = new ConcurrentHashMap<ShortVec2, MapTile>();
			dimensionMap.put(Integer.valueOf(dimension), seenChunks);
		}
		return seenChunks;
	}

	public void updateMap() {
		if (info.getPlayer().ticksExisted % UPDATE_INTERVAL != 0) {
			return;
		}
		int playerX = MathHelper.floor_double(info.getPlayer().posX) >> 4;
		int playerZ = MathHelper.floor_double(info.getPlayer().posZ) >> 4;
		ShortVec3 coords3 = new ShortVec3(playerX, info.getPlayer().dimension, playerZ);
		// Don't look around if the we've already *visited* (not just seen) this chunk
		if (visitedChunks.contains(coords3)) {
			return;
		} else {
			visitedChunks.add(coords3);
		}
		Map<ShortVec2, MapTile> seenChunks = getSeenChunksInDimension(info.getPlayer().dimension);
		// Look around in a circular area
		for (double dx = -LOOK_RADIUS; dx <= LOOK_RADIUS; dx++) {
			for (double dz = -LOOK_RADIUS; dz <= LOOK_RADIUS; dz++) {
				if (dx*dx + dz*dz > LOOK_RADIUS*LOOK_RADIUS) {
					continue; // Out of the circle of view
				}
				ShortVec2 coords = new ShortVec2(playerX + dx, playerZ + dz);
				if (seenChunks.containsKey(coords)) {
					continue;
				}
				Chunk chunk = info.getPlayer().worldObj.getChunkFromChunkCoords(coords.x, coords.y);
				int meanBiomeId = biomeAnalyzer.getChunkBiomeID(chunk);
				if (meanBiomeId != BiomeFlag.NONE) {
					putTile(seenChunks, coords, new MapTile(meanBiomeId));
				}
			}
		}
	}
	
	/** Puts a given tile into given map at specified coordinates and,
	 * on the client, sets appropriate sectors on adjacent tiles. */
	public void putTile(Map<ShortVec2, MapTile> tiles, ShortVec2 tileCoords, MapTile tile) {
		tiles.put(tileCoords, tile);
		// Only interested in texture details on the client:
		if (tileStitcher != null && info.getPlayer().worldObj.isRemote) {
			tileStitcher.stitchAdjacentTiles(tiles, new ShortVec2(tileCoords), tile);
		}
	}
	
	/** Sends ALL map data to client in several pieces. */
	public void syncOnClinet() {
		int pieces = 0;
		int dataSizeBytes = 0;
		Map<ShortVec2, MapTile> data = new HashMap<ShortVec2, MapTile>();
		for (Integer dimension : getVisitedDimensions()) {
			Map<ShortVec2, MapTile> seenChunks = getSeenChunksInDimension(dimension.intValue());
			for (Entry<ShortVec2, MapTile> entry : seenChunks.entrySet()) {
				data.put(entry.getKey(), entry.getValue());
				dataSizeBytes += MapDataPacket.ENTRY_SIZE_BYTES;
				if (dataSizeBytes >= MapDataPacket.MAX_SIZE_BYTES) {
					MapDataPacket packet = new MapDataPacket(dimension.intValue(), data);
					PacketDispatcher.sendPacketToPlayer(packet.makePacket(), (Player)info.getPlayer());
					pieces++;
					dataSizeBytes = 0;
					data.clear();
				}
			}
			if (data.size() > 0) {
				MapDataPacket packet = new MapDataPacket(dimension.intValue(), data);
				PacketDispatcher.sendPacketToPlayer(packet.makePacket(), (Player)info.getPlayer());
				pieces++;
				dataSizeBytes = 0;
				data.clear();
			}
		}
		AntiqueAtlasMod.logger.info("Sent Map data in " + pieces + " pieces.");
	}
}
