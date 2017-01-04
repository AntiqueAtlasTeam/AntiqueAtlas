package hunternif.mc.atlas.core;

import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MapDataPacket;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class AtlasData extends WorldSavedData {
	public static final int VERSION = 2;
	public static final String TAG_VERSION = "aaVersion";
	public static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
	public static final String TAG_DIMENSION_ID = "qDimensionID";
	public static final String TAG_VISITED_CHUNKS = "qVisitedChunks";
	
	// Navigation
	public static final String TAG_BROWSING_X = "qBrowseX";
	public static final String TAG_BROWSING_Y = "qBrowseY";
	public static final String TAG_BROWSING_ZOOM = "qBrowseZoom";
	
	/** This map contains, for each dimension, a map of chunks the player
	 * has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private Map<Integer /*dimension ID*/, DimensionData> dimensionMap =
			new ConcurrentHashMap<Integer, DimensionData>(2, 0.75f, 2);
	
	/** Set of players this Atlas data has been sent to. */
	private final Set<EntityPlayer> playersSentTo = new HashSet<EntityPlayer>();
	
	private NBTTagCompound nbt;

	public AtlasData(String key) {
		super(key);
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
			if (zoom == 0) zoom = 0.5;
			dimData.setBrowsingPosition(dimTag.getInteger(TAG_BROWSING_X),
					dimTag.getInteger(TAG_BROWSING_Y), zoom);
		}
	}
	
	/**Reads from NBT version 2*/
	public void readFromNBT2(NBTTagCompound compound) {
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
			if (zoom == 0) zoom = 0.5;
			dimData.setBrowsingPosition(dimTag.getInteger(TAG_BROWSING_X),
					dimTag.getInteger(TAG_BROWSING_Y), zoom);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_VERSION, VERSION);
		NBTTagList dimensionMapList = new NBTTagList();
		for (Entry<Integer, DimensionData> dimensionEntry : dimensionMap.entrySet()) {
			NBTTagCompound dimTag = new NBTTagCompound();
			dimTag.setInteger(TAG_DIMENSION_ID, dimensionEntry.getKey().intValue());
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
	}
	
	/** Puts a given tile into given map at specified coordinates and,
	 * if tileStitcher is present, sets appropriate sectors on adjacent tiles. */
	public void setTile(int dimension, int x, int y, Tile tile) {
		DimensionData dimData = getDimensionData(dimension);
		dimData.setTile(x, y, tile);
	}
	
	/** Returns the Tile previously set at given coordinates. */
	public Tile removeTile(int dimension, int x, int y) {
		DimensionData dimData = getDimensionData(dimension);
		return dimData.removeTile(x, y);
	}
	
	public Set<Integer> getVisitedDimensions() {
		return dimensionMap.keySet();
	}
	
	/* TODO: Packet Rework
	 *   Dimension data should check the server for updates*/
	/** If this dimension is not yet visited, empty DimensionData will be created. */
	public DimensionData getDimensionData(int dimension) {
		DimensionData dimData = dimensionMap.get(Integer.valueOf(dimension));
		if (dimData == null) {
			dimData = new DimensionData(this, dimension);
			dimensionMap.put(Integer.valueOf(dimension), dimData);
		}
		return dimData;
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
		Log.info("Sent Atlas #%d data to player %s", atlasID, player.getCommandSenderName());
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return dimensionMap.isEmpty();
	}
}
