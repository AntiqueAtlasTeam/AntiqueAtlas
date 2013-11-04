package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.MapTileStitcher;
import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class AtlasData extends WorldSavedData {
	private static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
	private static final String TAG_DIMENSION_ID = "qDimensionID";
	private static final String TAG_VISITED_CHUNKS = "qVisitedChunks";
	
	/** This map contains, for each dimension, a map of chunks the info.getPlayer()
	 * has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private Map<Integer /*dimension ID*/, Map<ShortVec2, MapTile>> dimensionMap =
			new ConcurrentHashMap<Integer, Map<ShortVec2, MapTile>>();
	
	private MapTileStitcher tileStitcher;
	
	private Set<Entity> playersSentTo = new HashSet<Entity>();

	public AtlasData(String key) {
		super(key);
	}
	
	public void setTileStitcher(MapTileStitcher tileStitcher) {
		this.tileStitcher = tileStitcher;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
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

	@Override
	public void writeToNBT(NBTTagCompound compound) {
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
	
	/** Puts a given tile into given map at specified coordinates and,
	 * if tileStitcher is present, sets appropriate sectors on adjacent tiles. */
	public void putTile(Map<ShortVec2, MapTile> tiles, ShortVec2 tileCoords, MapTile tile) {
		tiles.put(tileCoords, tile);
		if (tileStitcher != null) {
			tileStitcher.stitchAdjacentTiles(tiles, new ShortVec2(tileCoords), tile);
		}
	}
	
	public Set<Integer> getVisitedDimensions() {
		return dimensionMap.keySet();
	}
	public Map<ShortVec2, MapTile> getSeenChunksInDimension(int dimension) {
		Map<ShortVec2, MapTile> seenChunks = dimensionMap.get(Integer.valueOf(dimension));
		if (seenChunks == null) {
			seenChunks = new ConcurrentHashMap<ShortVec2, MapTile>();
			dimensionMap.put(Integer.valueOf(dimension), seenChunks);
		}
		return seenChunks;
	}
	
	public boolean isSyncedOnPlayer(Entity player) {
		return playersSentTo.contains(player);
	}

	public void syncOnPlayer(int atlasID, Entity player) {
		int pieces = 0;
		int dataSizeBytes = 0;
		Map<ShortVec2, MapTile> data = new HashMap<ShortVec2, MapTile>();
		for (Integer dimension : getVisitedDimensions()) {
			Map<ShortVec2, MapTile> seenChunks = getSeenChunksInDimension(dimension.intValue());
			for (Entry<ShortVec2, MapTile> entry : seenChunks.entrySet()) {
				data.put(entry.getKey(), entry.getValue());
				dataSizeBytes += MapDataPacket.ENTRY_SIZE_BYTES;
				if (dataSizeBytes >= MapDataPacket.MAX_SIZE_BYTES) {
					MapDataPacket packet = new MapDataPacket(atlasID, dimension.intValue(), data);
					PacketDispatcher.sendPacketToPlayer(packet.makePacket(), (Player)player);
					pieces++;
					dataSizeBytes = 0;
					data.clear();
				}
			}
			if (data.size() > 0) {
				MapDataPacket packet = new MapDataPacket(atlasID, dimension.intValue(), data);
				PacketDispatcher.sendPacketToPlayer(packet.makePacket(), (Player)player);
				pieces++;
				dataSizeBytes = 0;
				data.clear();
			}
		}
		AntiqueAtlasMod.logger.info("Sent Atlas #" + atlasID + " data in " + pieces + " pieces.");
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return dimensionMap.isEmpty();
	}
}
