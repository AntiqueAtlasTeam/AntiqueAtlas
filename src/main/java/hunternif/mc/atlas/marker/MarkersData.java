package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

/**
 * Contains markers, mapped to dimensions, and then to their chunk coordinates.
 * <p>
 * On the server a separate instance of MarkersData contains all the global
 * markers, which are also copied to atlases, but not saved with them.
 * At runtime clients have both types of markers in the same collection..
 * </p>
 * @author Hunternif
 */
public class MarkersData extends WorldSavedData {
	private static final int VERSION = 3;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
	private static final String TAG_DIMENSION_ID = "dimID";
	private static final String TAG_MARKERS = "markers";
	private static final String TAG_MARKER_ID = "id";
	private static final String TAG_MARKER_TYPE = "markerType";
	private static final String TAG_MARKER_LABEL = "label";
	private static final String TAG_MARKER_X = "x";
	private static final String TAG_MARKER_Y = "y";
	private static final String TAG_MARKER_VISIBLE_AHEAD = "visAh";
	
	/** Markers are stored in lists within square areas this many MC chunks
	 * across. */
	public static final int CHUNK_STEP = 4;
	
	/** Set of players this data has been sent to, only once after they connect. */
	private final Set<EntityPlayer> playersSentTo = new HashSet<EntityPlayer>();
	
	private final AtomicInteger largestID = new AtomicInteger(0);
	
	protected int getNewID() {
		return largestID.incrementAndGet();
	}
	
	private final Map<Integer /*marker ID*/, Marker> idMap = new ConcurrentHashMap<Integer, Marker>(2, 0.75f, 2);
	/**
	 * Maps a list of markers in a square to the square's coordinates, then to
	 * dimension ID. It exists in case someone needs to quickly find markers
	 * located in a square.
	 * Within the list markers are ordered by the Z coordinate, so that markers
	 * placed closer to the south will appear in front of those placed closer to
	 * the north.
	 * TODO: consider using Quad-tree.
	 */
	private final Map<Integer /*dimension ID*/, DimensionMarkersData> dimensionMap =
			new ConcurrentHashMap<Integer, DimensionMarkersData>(2, 0.75f, 2);
	
	public MarkersData(String key) {
		super(key);
	}


	@Override
	public void readFromNBT(NBTTagCompound compound) {
		int version = compound.getInteger(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}
		NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.tagCount(); d++) {
			NBTTagCompound tag = dimensionMapList.getCompoundTagAt(d);
			int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
			NBTTagList tagList = tag.getTagList(TAG_MARKERS, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound markerTag = tagList.getCompoundTagAt(i);
				boolean visibleAhead = true;
				if (version < 2) {
					Log.warn("Marker is visible ahead by default");
				} else {
					visibleAhead = markerTag.getBoolean(TAG_MARKER_VISIBLE_AHEAD);
				}
				int id;
				if (version < 3) {
					id = getNewID();
				} else {
					id = markerTag.getInteger(TAG_MARKER_ID);
					if (getMarkerByID(id) != null) {
						Log.warn("Loading marker with duplicate id %d. Getting new id", id);
						id = getNewID();
					}
					this.markDirty();
				}
				if (largestID.intValue() < id) {
					largestID.set(id);
				}
				Marker marker = new Marker(
						id,
						markerTag.getString(TAG_MARKER_TYPE),
						markerTag.getString(TAG_MARKER_LABEL),
						dimensionID,
						markerTag.getInteger(TAG_MARKER_X),
						markerTag.getInteger(TAG_MARKER_Y),
						visibleAhead);
				loadMarker(marker);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_VERSION, VERSION);
		NBTTagList dimensionMapList = new NBTTagList();
		for (Integer dimension : dimensionMap.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(TAG_DIMENSION_ID, dimension);
			DimensionMarkersData data = getMarkersDataInDimension(dimension);
			NBTTagList tagList = new NBTTagList();
			for (Marker marker : data.getAllMarkers()) {
				NBTTagCompound markerTag = new NBTTagCompound();
				markerTag.setInteger(TAG_MARKER_ID, marker.getId());
				markerTag.setString(TAG_MARKER_TYPE, marker.getType());
				markerTag.setString(TAG_MARKER_LABEL, marker.getLabel());
				markerTag.setInteger(TAG_MARKER_X, marker.getX());
				markerTag.setInteger(TAG_MARKER_Y, marker.getZ());
				markerTag.setBoolean(TAG_MARKER_VISIBLE_AHEAD, marker.isVisibleAhead());
				tagList.appendTag(markerTag);
			}
			tag.setTag(TAG_MARKERS, tagList);
			dimensionMapList.appendTag(tag);
		}
		compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
	}
	
	public Set<Integer> getVisitedDimensions() {
		return dimensionMap.keySet();
	}
	
	/** This method is rather inefficient, use it sparingly. */
	public Collection<Marker> getMarkersInDimension(int dimension) {
		return getMarkersDataInDimension(dimension).getAllMarkers();
	}
	
	/** Creates a new instance of {@link DimensionMarkersData}, if necessary. */
	public DimensionMarkersData getMarkersDataInDimension(int dimension) {
		DimensionMarkersData data = dimensionMap.get(dimension);
		if (data == null) {
			data = new DimensionMarkersData(this, dimension);
			dimensionMap.put(dimension, data);
		}
		return data;
	}
	
	/** The "chunk" here is {@link MarkersData#CHUNK_STEP} times larger than the
	 * Minecraft 16x16 chunk! May return null. */
	public List<Marker> getMarkersAtChunk(int dimension, int x, int z) {
		return getMarkersDataInDimension(dimension).getMarkersAtChunk(x, z);
	}
	
	public Marker getMarkerByID(int id) {
		return idMap.get(id);
	}
	public Marker removeMarker(int id) {
		Marker marker = getMarkerByID(id);
		if (marker == null) return null;
		if (idMap.remove(id) != null) {
			getMarkersDataInDimension(marker.getDimension()).removeMarker(marker);
			markDirty();
		}
		return marker;
	}
	
	/** For internal use. Use the {@link MarkerAPI} to put markers! This method
	 * creates a new marker from the given data, saves and returns it.
	 * Server side only! */
	public Marker createAndSaveMarker(String type, String label, int dimension, int x, int z, boolean visibleAhead) {
		Marker marker = new Marker(getNewID(), type, label, dimension, x, z, visibleAhead);
		idMap.put(marker.getId(), marker);
		getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
		markDirty();
		return marker;
	}
	
	/**
	 * For internal use, when markers are loaded from NBT or sent from the
	 * server. IF a marker's id is conflicting, the marker is not loaded!
	 * @return the marker instance that was added.
	 */
	public Marker loadMarker(Marker marker) {
		if (!idMap.containsKey(marker.getId())) {
			idMap.put(marker.getId(), marker);
			getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
		}
		return marker;
	}
	
	public boolean isSyncedOnPlayer(EntityPlayer player) {
		return playersSentTo.contains(player);
	}
	
	/** Send all data to the player in several packets. Called once during the
	 * first run of ItemAtals.onUpdate(). */
	public void syncOnPlayer(int atlasID, EntityPlayer player) {
		for (Integer dimension : dimensionMap.keySet()) {
			MarkersPacket packet = newMarkersPacket(atlasID, dimension);
			DimensionMarkersData data = getMarkersDataInDimension(dimension);
			for (Marker marker : data.getAllMarkers()) {
				packet.putMarker(marker);
			}
			PacketDispatcher.sendTo(packet, (EntityPlayerMP) player);
		}
		Log.info("Sent markers data to player %s", player.getCommandSenderName());
		playersSentTo.add(player);
	}
	
	/** To be overridden in GlobalMarkersData. */
	protected MarkersPacket newMarkersPacket(int atlasID, int dimension) {
		return new MarkersPacket(atlasID, dimension);
	}
	
	public boolean isEmpty() {
		return idMap.isEmpty();
	}
	
}
