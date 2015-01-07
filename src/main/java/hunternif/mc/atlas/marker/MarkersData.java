package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.network.ModPacket;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;

/**
 * Contains markers, mapped to dimensions, and then to their chunk coordinates.
 * Used by Atlases for local markers, and a single instance is used for global
 * markers.
 * @author Hunternif
 */
public class MarkersData extends WorldSavedData {
	private static final int VERSION = 2;
	private static final String TAG_VERSION = "aaVersion";
	private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
	private static final String TAG_DIMENSION_ID = "dimID";
	private static final String TAG_MARKERS = "markers";
	private static final String TAG_MARKER_TYPE = "markerType";
	private static final String TAG_MARKER_LABEL = "label";
	private static final String TAG_MARKER_X = "x";
	private static final String TAG_MARKER_Y = "y";
	private static final String TAG_MARKER_VISIBLE_AHEAD = "visAh";
	
	private static final Supplier<SortedSet<Marker>> concurrentSortedSetSupplier = new Supplier<SortedSet<Marker>>() {
		@Override
		public SortedSet<Marker> get() {
			return new ConcurrentSkipListSet<Marker>();
		}
	};
	
	/** Set of players this data has been sent to. */
	private final Set<EntityPlayer> playersSentTo = new HashSet<EntityPlayer>();
	
	public MarkersData(String key) {
		super(key);
	}
	
	/**
	 * SetMultimap, because markers shouldn't be duplicated. SortedSet, because
	 * markers must be sorted by their y coordinate. ConcurrentHashMap, because
	 * it is concurrently updated by executing MarkerPacket.
	 * The sorted sets inside the multimap allow concurrent iteration.
	 * Set of markers is mapped to chunk coordinates and then to dimension ID.
	 */
	private final Map<Integer /*dimension ID*/, SortedSetMultimap<ShortVec2 /*chunk*/, Marker>> dimensionMap =
			new ConcurrentHashMap<Integer, SortedSetMultimap<ShortVec2, Marker>>();
	
	private final ShortVec2 tempCoords = new ShortVec2(0, 0);

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		int version = compound.getInteger(TAG_VERSION);
		if (version < VERSION) {
			AntiqueAtlasMod.logger.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
			this.markDirty();
		}
		NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST, Constants.NBT.TAG_COMPOUND);
		for (int d = 0; d < dimensionMapList.tagCount(); d++) {
			NBTTagCompound tag = dimensionMapList.getCompoundTagAt(d);
			int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
			SortedSetMultimap<ShortVec2, Marker> markers = getMarkersDataInDimension(dimensionID);
			NBTTagList tagList = tag.getTagList(TAG_MARKERS, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound markerTag = tagList.getCompoundTagAt(i);
				boolean visibleAhead = true;
				if (version < 2) {
					AntiqueAtlasMod.logger.warn("Marker is visible ahead by default");
				} else {
					visibleAhead = markerTag.getBoolean(TAG_MARKER_VISIBLE_AHEAD);
				}
				Marker marker = new Marker(
						markerTag.getString(TAG_MARKER_TYPE),
						markerTag.getString(TAG_MARKER_LABEL),
						markerTag.getInteger(TAG_MARKER_X),
						markerTag.getInteger(TAG_MARKER_Y),
						visibleAhead);
				markers.put(marker.getChunkCoords(), marker);
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
			SortedSetMultimap<ShortVec2, Marker> markers = getMarkersDataInDimension(dimension);
			NBTTagList tagList = new NBTTagList();
			for (Marker marker : markers.values()) {
				NBTTagCompound markerTag = new NBTTagCompound();
				markerTag.setString(TAG_MARKER_TYPE, marker.getType());
				markerTag.setString(TAG_MARKER_LABEL, marker.getLabel());
				markerTag.setInteger(TAG_MARKER_X, marker.getX());
				markerTag.setInteger(TAG_MARKER_Y, marker.getY());
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
	
	/** Creates a new multimap, if needed. */
	public SortedSetMultimap<ShortVec2, Marker> getMarkersDataInDimension(int dimension) {
		SortedSetMultimap<ShortVec2, Marker> map = dimensionMap.get(Integer.valueOf(dimension));
		if (map == null) {
			map = Multimaps.synchronizedSortedSetMultimap(Multimaps.newSortedSetMultimap(
					new ConcurrentHashMap<ShortVec2, Collection<Marker>>(), concurrentSortedSetSupplier));
			dimensionMap.put(Integer.valueOf(dimension), map);
		}
		return map;
	}
	
	public Collection<Marker> getMarkersInDimension(int dimension) {
		return getMarkersDataInDimension(dimension).values();
	}
	
	public SortedSet<Marker> getMarkersAtChunk(int dimension, int x, int y) {
		return getMarkersDataInDimension(dimension).get(tempCoords.set(x, y));
	}
	
	/** Use the {@link MarkerAPI} to put markers! */
	public boolean putMarker(int dimension, Marker marker) {
		return getMarkersDataInDimension(dimension).put(marker.getChunkCoords(), marker);
	}
	
	public boolean isSyncedOnPlayer(EntityPlayer player) {
		return playersSentTo.contains(player);
	}
	
	/** Send all data to the player in several packets. Called once during the
	 * first run of ItemAtals.onUpdate(). */
	public void syncOnPlayer(int atlasID, EntityPlayer player) {
		int pieces = 0;
		int dataSizeBytes = 0;
		for (Integer dimension : dimensionMap.keySet()) {
			MarkersPacket packet = newMarkersPacket(atlasID, dimension);
			SortedSetMultimap<ShortVec2, Marker> markers = getMarkersDataInDimension(dimension);
			for (Marker marker : markers.values()) {
				packet.putMarker(marker);
				dataSizeBytes += 4 + 4 + (marker.getLabel().length() + marker.getType().length())*2;
				if (dataSizeBytes >= ModPacket.MAX_SIZE_BYTES) {
					AntiqueAtlasMod.packetPipeline.sendTo(packet, player);
					pieces++;
					dataSizeBytes = 0;
					packet = newMarkersPacket(atlasID, dimension);
				}
			}
			if (!packet.isEmpty()) {
				AntiqueAtlasMod.packetPipeline.sendTo(packet, player);
				pieces++;
				dataSizeBytes = 0;
			}
		}
		AntiqueAtlasMod.logger.info("Sent markers data to player " + player.getCommandSenderName() + " in " + pieces + " pieces.");
		playersSentTo.add(player);
	}
	
	/** To be overridden in GlobalMarkersData. */
	protected MarkersPacket newMarkersPacket(int atlasID, int dimension) {
		return new MarkersPacket(atlasID, dimension);
	}
	
	public boolean isEmpty() {
		return dimensionMap.isEmpty();
	}
}
