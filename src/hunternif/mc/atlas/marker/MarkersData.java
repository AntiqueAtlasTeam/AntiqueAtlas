package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.network.CustomPacket;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * Contains markers, mapped to dimensions, and then to their chunk coordinates.
 * Used by Atlases for local markers, and a single instance is used for global
 * markers.
 * @author Hunternif
 */
public class MarkersData extends WorldSavedData {
	private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
	private static final String TAG_DIMENSION_ID = "dimID";
	private static final String TAG_MARKERS = "markers";
	private static final String TAG_MARKER_TYPE = "markerType";
	private static final String TAG_MARKER_LABEL = "label";
	private static final String TAG_MARKER_X = "x";
	private static final String TAG_MARKER_Y = "y";
	
	private static final Supplier<SortedSet<Marker>> concurrentSortedSetSupplier = new Supplier<SortedSet<Marker>>() {
		@Override
		public SortedSet<Marker> get() {
			return new ConcurrentSkipListSet<Marker>();
		}
	};
	
	public MarkersData(String key) {
		super(key);
	}
	
	/** SetMultimap, because markers shouldn't be duplicated. SortedSet, because
	 * markers must be sorted by their y coordinate. ConcurrentHashMap, because
	 * it is concurrently updated by executing MarkerPacket.
	 * The sorted sets inside the multimap allow concurrent iteration. */
	private final Map<Integer /*dimension ID*/, SortedSetMultimap<ShortVec2, Marker>> dimensionMap =
			new ConcurrentHashMap<Integer, SortedSetMultimap<ShortVec2, Marker>>();

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList dimensionMapList = compound.getTagList(TAG_DIMENSION_MAP_LIST);
		for (int d = 0; d < dimensionMapList.tagCount(); d++) {
			NBTTagCompound tag = (NBTTagCompound) dimensionMapList.tagAt(d);
			int dimensionID = tag.getInteger(TAG_DIMENSION_ID);
			SortedSetMultimap<ShortVec2, Marker> markers = getMarkersInDimension(dimensionID);
			NBTTagList tagList = tag.getTagList(TAG_MARKERS);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound markerTag = (NBTTagCompound) tagList.tagAt(i);
				Marker marker = new Marker(
						markerTag.getString(TAG_MARKER_TYPE),
						markerTag.getString(TAG_MARKER_LABEL),
						markerTag.getInteger(TAG_MARKER_X),
						markerTag.getInteger(TAG_MARKER_Y));
				markers.put(marker.getChunkCoords(), marker);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList dimensionMapList = new NBTTagList();
		for (Integer dimension : dimensionMap.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(TAG_DIMENSION_ID, dimension);
			SortedSetMultimap<ShortVec2, Marker> markers = getMarkersInDimension(dimension);
			NBTTagList tagList = new NBTTagList();
			for (Marker marker : markers.values()) {
				NBTTagCompound markerTag = new NBTTagCompound();
				markerTag.setString(TAG_MARKER_TYPE, marker.getType());
				markerTag.setString(TAG_MARKER_LABEL, marker.getLabel());
				markerTag.setInteger(TAG_MARKER_X, marker.getX());
				markerTag.setInteger(TAG_MARKER_Y, marker.getY());
				tagList.appendTag(markerTag);
			}
			tag.setTag(TAG_MARKERS, tagList);
			dimensionMapList.appendTag(tag);
		}
		compound.setTag(TAG_DIMENSION_MAP_LIST, dimensionMapList);
	}
	
	/** Creates a new multimap, if needed. */
	private SortedSetMultimap<ShortVec2, Marker> getMarkersInDimension(int dimension) {
		SortedSetMultimap<ShortVec2, Marker> map = dimensionMap.get(Integer.valueOf(dimension));
		if (map == null) {
			map = Multimaps.synchronizedSortedSetMultimap(Multimaps.newSortedSetMultimap(
					new ConcurrentHashMap<ShortVec2, Collection<Marker>>(), concurrentSortedSetSupplier));
			dimensionMap.put(Integer.valueOf(dimension), map);
		}
		return map;
	}
	
	public SortedSet<Marker> getMarkersAtChunk(int dimension, ShortVec2 coords) {
		return getMarkersInDimension(dimension).get(coords);
	}
	
	/** Use the {@link MarkerAPI} to put markers! */
	public void putMarker(int dimension, Marker marker) {
		getMarkersInDimension(dimension).put(marker.getChunkCoords(), marker);
	}
	
	/** Send all data to the player in several zipped packets. */
	protected void syncOnPlayer(int atlasID, EntityPlayer player) {
		int pieces = 0;
		int dataSizeBytes = 0;
		for (Integer dimension : dimensionMap.keySet()) {
			MarkersPacket packet = newMarkersPacket(atlasID, dimension);
			SortedSetMultimap<ShortVec2, Marker> markers = getMarkersInDimension(dimension);
			for (Marker marker : markers.values()) {
				packet.putMarker(marker);
				dataSizeBytes += 4 + 4 + (marker.getLabel().length() + marker.getType().length())*2;
				if (dataSizeBytes >= CustomPacket.MAX_SIZE_BYTES) {
					PacketDispatcher.sendPacketToPlayer(packet.makePacket(), (Player)player);
					pieces++;
					dataSizeBytes = 0;
					packet = newMarkersPacket(atlasID, dimension);
				}
			}
			if (!packet.isEmpty()) {
				PacketDispatcher.sendPacketToPlayer(packet.makePacket(), (Player)player);
				pieces++;
				dataSizeBytes = 0;
			}
		}
		AntiqueAtlasMod.logger.info("Sent markers data to player " + player.username + " in " + pieces + " pieces.");
	}
	/** To be overriden in GlobalMarkersData. */
	protected MarkersPacket newMarkersPacket(int atlasID, int dimension) {
		return new MarkersPacket(atlasID, dimension);
	}
}
