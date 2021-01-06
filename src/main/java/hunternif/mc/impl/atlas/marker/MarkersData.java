package hunternif.mc.impl.atlas.marker;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.api.MarkerAPI;
import hunternif.mc.impl.atlas.network.packet.s2c.play.MarkersS2CPacket;
import hunternif.mc.impl.atlas.registry.MarkerType;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import stereowalker.forge.NbtType;

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
	
	public MarkersData(String key) {
		super(key);
	}


	@Override
	public void read(CompoundNBT compound) {
		int version = compound.getInt(TAG_VERSION);
		if (version < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}
		ListNBT dimensionMapList = compound.getList(TAG_WORLD_MAP_LIST, NbtType.CompoundNBT);
		for (int d = 0; d < dimensionMapList.size(); d++) {
			CompoundNBT tag = dimensionMapList.getCompound(d);
			RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,new ResourceLocation(tag.getString(TAG_WORLD_ID)));

			ListNBT tagList = tag.getList(TAG_MARKERS, NbtType.CompoundNBT);
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
						new ResourceLocation(markerTag.getString(TAG_MARKER_TYPE)),
						ITextComponent.Serializer.getComponentFromJson(markerTag.getString(TAG_MARKER_LABEL)),
						world,
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
		for (RegistryKey<World> world : worldMap.keySet()) {
			CompoundNBT tag = new CompoundNBT();
			tag.putString(TAG_WORLD_ID, world.getLocation().toString());
			DimensionMarkersData data = getMarkersDataInWorld(world);
			ListNBT tagList = new ListNBT();
			for (Marker marker : data.getAllMarkers()) {
				CompoundNBT markerTag = new CompoundNBT();
				markerTag.putInt(TAG_MARKER_ID, marker.getId());
				markerTag.putString(TAG_MARKER_TYPE, marker.getType().toString());
				markerTag.putString(TAG_MARKER_LABEL, ITextComponent.Serializer.toJson(marker.getLabel()));
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
	public Collection<Marker> getMarkersInDimension(RegistryKey<World> world) {
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
	public Marker createAndSaveMarker(ResourceLocation type, RegistryKey<World> world, int x, int z, boolean visibleAhead, ITextComponent label) {
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
			if (totalMarkers < AntiqueAtlasConfig.markerLimit.get()){
				getMarkersDataInWorld(marker.getWorld()).insertMarker(marker);
			} else {
				Log.warn("Could not add new marker. Atlas is at it's limit of %d markers", AntiqueAtlasConfig.markerLimit.get());
			}
		}
		return marker;
	}
	
	public boolean isSyncedOnPlayer(PlayerEntity player) {
		return playersSentTo.contains(player);
	}
	
	/** Send all data to the player in several packets. Called once during the
	 * first run of ItemAtals.onUpdate(). */
	public void syncOnPlayer(int atlasID, ServerPlayerEntity player) {
		for (RegistryKey<World> world : worldMap.keySet()) {
			DimensionMarkersData data = getMarkersDataInWorld(world);

			new MarkersS2CPacket(atlasID, world, data.getAllMarkers()).send(player);
		}
		Log.info("Sent markers data #%d to player %s", atlasID, player.getCommandSource().getName());
		playersSentTo.add(player);
	}

	public boolean isEmpty() {
		return idMap.isEmpty();
	}
	
}
