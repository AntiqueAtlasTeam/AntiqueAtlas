package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.util.Log;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
	public static final int CHUNK_STEP = 8;

	/** Set of players this data has been sent to, only once after they connect. */
	private final Set<PlayerEntity> playersSentTo = new HashSet<>();

	private final AtomicInteger largestID = new AtomicInteger(0);

	private int getNewID() {
		return largestID.incrementAndGet();
	}

	private final Map<Integer /*marker ID*/, Marker> idMap = new ConcurrentHashMap<>(2, 0.75f, 2);
	/**
	 * Maps a list of markers in a square to the square's coordinates, then to
	 * dimension ID. It exists in case someone needs to quickly find markers
	 * located in a square.
	 * Within the list markers are ordered by the Z coordinate, so that markers
	 * placed closer to the south will appear in front of those placed closer to
	 * the north.
	 * TODO: consider using Quad-tree. At small zoom levels iterating through
	 * chunks to render markers gets very slow.
	 */
	private final Map<DimensionType, DimensionMarkersData> dimensionMap =
			new ConcurrentHashMap<>(2, 0.75f, 2);

	public MarkersData(String key) {
		super(key);
	}

	@Override
	public void read(CompoundNBT nbt) {
		int version = nbt.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}
		ListNBT dimensionMapList = nbt.getList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.size(); d++) {
			CompoundNBT tag = dimensionMapList.getCompound(d);
			DimensionType dimensionID;
			if (tag.contains(TAG_DIMENSION_ID, Constants.NBT.TAG_ANY_NUMERIC)) {
				dimensionID = Registry.DIMENSION_TYPE.getByValue(tag.getInt(TAG_DIMENSION_ID));
			} else {
				dimensionID = Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(tag.getString(TAG_DIMENSION_ID)));
			}
			ListNBT tagList = tag.getList(TAG_MARKERS, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.size(); i++) {
				CompoundNBT markerTag = tagList.getCompound(i);
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
					id = markerTag.getInt(TAG_MARKER_ID);
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
						markerTag.getInt(TAG_MARKER_X),
						markerTag.getInt(TAG_MARKER_Y),
						visibleAhead);
				loadMarker(marker);
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		Log.info("Saving local markers data to NBT");
		compound.putInt(TAG_VERSION, VERSION);
		ListNBT dimensionMapList = new ListNBT();
		for (DimensionType dimension : dimensionMap.keySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString(TAG_DIMENSION_ID, Registry.DIMENSION_TYPE.getKey(dimension).toString());
			DimensionMarkersData data = getMarkersDataInDimension(dimension);
			ListNBT tagList = new ListNBT();
			for (Marker marker : data.getAllMarkers()) {
				CompoundNBT markerTag = new CompoundNBT();
				markerTag.putInt(TAG_MARKER_ID, marker.getId());
				markerTag.putString(TAG_MARKER_TYPE, marker.getType());
				markerTag.putString(TAG_MARKER_LABEL, marker.getLabel());
				markerTag.putInt(TAG_MARKER_X, marker.getX());
				markerTag.putInt(TAG_MARKER_Y, marker.getZ());
				markerTag.putBoolean(TAG_MARKER_VISIBLE_AHEAD, marker.isVisibleAhead());
				tagList.add(markerTag);
			}
			tag.put(TAG_MARKERS, tagList);
			dimensionMapList.add(tag);
		}
		compound.put(TAG_DIMENSION_MAP_LIST, dimensionMapList);

		return compound;
	}

	public Set<DimensionType> getVisitedDimensions() {
		return dimensionMap.keySet();
	}

	/** This method is rather inefficient, use it sparingly. */
	public Collection<Marker> getMarkersInDimension(DimensionType dimension) {
		return getMarkersDataInDimension(dimension).getAllMarkers();
	}

	/** Creates a new instance of {@link DimensionMarkersData}, if necessary. */
	public DimensionMarkersData getMarkersDataInDimension(DimensionType dimension) {
		return dimensionMap.computeIfAbsent(dimension, k -> new DimensionMarkersData(this, dimension));
	}

	/** The "chunk" here is {@link MarkersData#CHUNK_STEP} times larger than the
	 * Minecraft 16x16 chunk! May return null. */
	public List<Marker> getMarkersAtChunk(DimensionType dimension, int x, int z) {
		return getMarkersDataInDimension(dimension).getMarkersAtChunk(x, z);
	}

	private Marker getMarkerByID(int id) {
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
	public Marker createAndSaveMarker(String type, String label, DimensionType dimension, int x, int z, boolean visibleAhead) {
		Marker marker = new Marker(getNewID(), type, label, dimension, x, z, visibleAhead);
		Log.info("Created new marker %s", marker.toString());
		idMap.put(marker.getId(), marker);
		getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
		markDirty();
		return marker;
	}

	/**
	 * For internal use, when markers are loaded from NBT or sent from the
	 * server. IF a marker's id is conflicting, the marker will not load!
	 * @return the marker instance that was added.
	 */
	public Marker loadMarker(Marker marker) {
		if (!idMap.containsKey(marker.getId())) {
			idMap.put(marker.getId(), marker);
			int totalMarkers = 0;
			for (Entry<DimensionType, DimensionMarkersData> e: dimensionMap.entrySet()){
				totalMarkers += e.getValue().getAllMarkers().size();
			}
			if (totalMarkers < SettingsConfig.markerLimit){
				getMarkersDataInDimension(marker.getDimension()).insertMarker(marker);
			} else {
				Log.warn("Could not add new marker. Atlas is at it's limit of %d markers", SettingsConfig.markerLimit);
			}
		}
		return marker;
	}

	public boolean isSyncedOnPlayer(PlayerEntity player) {
		return playersSentTo.contains(player);
	}

	/** Send all data to the player in several packets. Called once during the
	 * first run of ItemAtals.onUpdate(). */
	public void syncOnPlayer(int atlasID, PlayerEntity player) {
		for (DimensionType dimension : dimensionMap.keySet()) {
			MarkersPacket packet = newMarkersPacket(atlasID, dimension);
			DimensionMarkersData data = getMarkersDataInDimension(dimension);
			for (Marker marker : data.getAllMarkers()) {
				packet.putMarker(marker);
			}
			PacketDispatcher.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
		}
		Log.info("Sent markers data #%d to player %s", atlasID, player.getCommandSource().getName());
		playersSentTo.add(player);
	}

	/** To be overridden in GlobalMarkersData. */
	MarkersPacket newMarkersPacket(int atlasID, DimensionType dimension) {
		return new MarkersPacket(atlasID, dimension);
	}

	public boolean isEmpty() {
		return idMap.isEmpty();
	}

}
