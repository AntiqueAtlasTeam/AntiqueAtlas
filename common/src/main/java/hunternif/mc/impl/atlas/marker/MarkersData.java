package hunternif.mc.impl.atlas.marker;

import hunternif.mc.api.MarkerAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.s2c.play.PutMarkersS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

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
public class MarkersData extends PersistentState {
	private static final int VERSION = 4;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_WORLD_MAP_LIST = "worldMap";
	private static final String TAG_WORLD_ID = "worldID";
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
	private final Map<RegistryKey<World>, DimensionMarkersData> worldMap =
			new ConcurrentHashMap<>(2, 0.75f, 2);

	public MarkersData() {
	}

	public static MarkersData fromNbt(NbtCompound compound) {
		MarkersData data = new MarkersData();
		doReadNbt(compound, data);
		return data;
	}

	protected static void doReadNbt(NbtCompound compound, MarkersData data) {

		int version = compound.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			return;
		}

		NbtList dimensionMapList = compound.getList(TAG_WORLD_MAP_LIST, NbtElement.COMPOUND_TYPE);
		for (int d = 0; d < dimensionMapList.size(); d++) {
			NbtCompound tag = dimensionMapList.getCompound(d);
			RegistryKey<World> world = RegistryKey.of(RegistryKeys.WORLD, new Identifier(tag.getString(TAG_WORLD_ID)));

			NbtList tagList = tag.getList(TAG_MARKERS, NbtElement.COMPOUND_TYPE);
			for (int i = 0; i < tagList.size(); i++) {
				NbtCompound markerTag = tagList.getCompound(i);
				boolean visibleAhead = markerTag.getBoolean(TAG_MARKER_VISIBLE_AHEAD);

				int id = markerTag.getInt(TAG_MARKER_ID);
				if (data.getMarkerByID(id) != null) {
					Log.warn("Loading marker with duplicate id %d. Getting new id", id);
					id = data.getNewID();
				}
				data.markDirty();
				if (data.largestID.intValue() < id) {
					data.largestID.set(id);
				}

				Marker marker = new Marker(
						id,
						new Identifier(markerTag.getString(TAG_MARKER_TYPE)),
						Text.Serializer.fromJson(markerTag.getString(TAG_MARKER_LABEL)),
						world,
						markerTag.getInt(TAG_MARKER_X),
						markerTag.getInt(TAG_MARKER_Y),
						visibleAhead);
				data.loadMarker(marker);
			}
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound compound) {
		Log.info("Saving local markers data to NBT");
		compound.putInt(TAG_VERSION, VERSION);
		NbtList dimensionMapList = new NbtList();
		for (RegistryKey<World> world : worldMap.keySet()) {
			NbtCompound tag = new NbtCompound();
			tag.putString(TAG_WORLD_ID, world.getValue().toString());
			DimensionMarkersData data = getMarkersDataInWorld(world);
			NbtList tagList = new NbtList();
			for (Marker marker : data.getAllMarkers()) {
				NbtCompound markerTag = new NbtCompound();
				markerTag.putInt(TAG_MARKER_ID, marker.getId());
				markerTag.putString(TAG_MARKER_TYPE, marker.getType().toString());
				markerTag.putString(TAG_MARKER_LABEL, Text.Serializer.toJson(marker.getLabel()));
				markerTag.putInt(TAG_MARKER_X, marker.getX());
				markerTag.putInt(TAG_MARKER_Y, marker.getZ());
				markerTag.putBoolean(TAG_MARKER_VISIBLE_AHEAD, marker.isVisibleAhead());
				tagList.add(markerTag);
			}
			tag.put(TAG_MARKERS, tagList);
			dimensionMapList.add(tag);
		}
		compound.put(TAG_WORLD_MAP_LIST, dimensionMapList);

		return compound;
	}

	public Set<RegistryKey<World>> getVisitedDimensions() {
		return worldMap.keySet();
	}

	/** This method is rather inefficient, use it sparingly. */
	public Collection<Marker> getMarkersInWorld(RegistryKey<World> world) {
		return getMarkersDataInWorld(world).getAllMarkers();
	}

	/** Creates a new instance of {@link DimensionMarkersData}, if necessary. */
	public DimensionMarkersData getMarkersDataInWorld(RegistryKey<World> world) {
		return worldMap.computeIfAbsent(world, k -> new DimensionMarkersData(this, world));
	}

	/** The "chunk" here is {@link MarkersData#CHUNK_STEP} times larger than the
	 * Minecraft 16x16 chunk! May return null. */
	public List<Marker> getMarkersAtChunk(RegistryKey<World> world, int x, int z) {
		return getMarkersDataInWorld(world).getMarkersAtChunk(x, z);
	}

	private Marker getMarkerByID(int id) {
		return idMap.get(id);
	}
	public Marker removeMarker(int id) {
		Marker marker = getMarkerByID(id);
		if (marker == null) return null;
		if (idMap.remove(id) != null) {
			getMarkersDataInWorld(marker.getWorld()).removeMarker(marker);
			markDirty();
		}
		return marker;
	}

	/** For internal use. Use the {@link MarkerAPI} to put markers! This method
	 * creates a new marker from the given data, saves and returns it.
	 * Server side only! */
	public Marker createAndSaveMarker(Identifier type, RegistryKey<World> world, int x, int z, boolean visibleAhead, Text label) {
		Marker marker = new Marker(getNewID(), type, label, world, x, z, visibleAhead);
		Log.info("Created new marker %s", marker.toString());
		idMap.put(marker.getId(), marker);
		getMarkersDataInWorld(world).insertMarker(marker);
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
			for (Entry<RegistryKey<World>, DimensionMarkersData> e: worldMap.entrySet()){
				totalMarkers += e.getValue().getAllMarkers().size();
			}
			if (totalMarkers < AntiqueAtlasMod.CONFIG.markerLimit){
				getMarkersDataInWorld(marker.getWorld()).insertMarker(marker);
			} else {
				Log.warn("Could not add new marker. Atlas is at it's limit of %d markers", AntiqueAtlasMod.CONFIG.markerLimit);
			}
		}
		return marker;
	}

	public boolean isSyncedOnPlayer(PlayerEntity player) {
		return playersSentTo.contains(player);
	}

	/** Send all data to the player in several packets. Called once during the
	 * first run of ItemAtlas.onUpdate(). */
	public void syncToPlayer(int atlasID, ServerPlayerEntity player) {
		for (RegistryKey<World> world : worldMap.keySet()) {
			DimensionMarkersData data = getMarkersDataInWorld(world);

			new PutMarkersS2CPacket(atlasID, world, data.getAllMarkers()).send(player);
		}
		Log.info("Sent markers data #%d to player %s", atlasID, player.getCommandSource().getName());
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return idMap.isEmpty();
	}

}
