package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.MapTileStitcher;
import hunternif.mc.atlas.network.CustomPacket;
import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class AtlasData extends WorldSavedData {
	private static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
	private static final String TAG_DIMENSION_ID = "qDimensionID";
	private static final String TAG_VISITED_CHUNKS = "qVisitedChunks";
	
	/** This map contains, for each dimension, a map of chunks the player
	 * has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private Map<Integer /*dimension ID*/, DimensionData> dimensionMap =
			new ConcurrentHashMap<Integer, DimensionData>();
	
	private MapTileStitcher tileStitcher;
	
	/** Set of players this Atlas data has been sent to. */
	private final Set<EntityPlayer> playersSentTo = new HashSet<EntityPlayer>();

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
			int[] intArray = tag.getIntArray(TAG_VISITED_CHUNKS);
			for (int i = 0; i < intArray.length; i += 3) {
				ShortVec2 coords = new ShortVec2(intArray[i], intArray[i+1]);
				putTile(dimensionID, coords, new MapTile(intArray[i+2]));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList dimensionMapList = new NBTTagList();
		for (Entry<Integer, DimensionData> dimensionEntry : dimensionMap.entrySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(TAG_DIMENSION_ID, dimensionEntry.getKey().intValue());
			Map<ShortVec2, MapTile> seenChunks = dimensionEntry.getValue().getSeenChunks();
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
	public void putTile(int dimension, ShortVec2 tileCoords, MapTile tile) {
		DimensionData dimData = getDimensionData(dimension);
		dimData.putTile(tileCoords, tile);
		if (tileStitcher != null) {
			tileStitcher.stitchAdjacentTiles(dimData.getSeenChunks(), new ShortVec2(tileCoords), tile);
		}
	}
	
	public Set<Integer> getVisitedDimensions() {
		return dimensionMap.keySet();
	}
	/** If this dimension is not yet visited, empty DimensionData will be created. */
	public DimensionData getDimensionData(int dimension) {
		DimensionData dimData = dimensionMap.get(Integer.valueOf(dimension));
		if (dimData == null) {
			dimData = new DimensionData(dimension);
			dimensionMap.put(Integer.valueOf(dimension), dimData);
		}
		return dimData;
	}
	public Map<ShortVec2, MapTile> getSeenChunksInDimension(int dimension) {
		return getDimensionData(dimension).getSeenChunks();
	}
	
	public boolean isSyncedOnPlayer(EntityPlayer player) {
		return playersSentTo.contains(player);
	}

	/** Send all data to the player in several zipped packets. Called once
	 * during the first run of ItemAtals.onUpdate(). */
	public void syncOnPlayer(int atlasID, EntityPlayer player) {
		int pieces = 0;
		int dataSizeBytes = 0;
		Map<ShortVec2, MapTile> data = new HashMap<ShortVec2, MapTile>();
		for (Integer dimension : getVisitedDimensions()) {
			Map<ShortVec2, MapTile> seenChunks = getSeenChunksInDimension(dimension.intValue());
			for (Entry<ShortVec2, MapTile> entry : seenChunks.entrySet()) {
				data.put(entry.getKey(), entry.getValue());
				dataSizeBytes += MapDataPacket.ENTRY_SIZE_BYTES;
				if (dataSizeBytes >= CustomPacket.MAX_SIZE_BYTES) {
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
		AntiqueAtlasMod.logger.info("Sent Atlas #" + atlasID + " data to player " + player.username + " in " + pieces + " pieces.");
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return dimensionMap.isEmpty();
	}
}
