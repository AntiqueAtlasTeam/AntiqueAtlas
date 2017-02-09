package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.util.ListMapValueIterator;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DimensionMarkersData {
	private final MarkersData parent;
	private final int dimension;
	
	private int size = 0;
	
	private final Map<ShortVec2 /*chunk coords*/, List<Marker>> chunkMap =
			new ConcurrentHashMap<>(2, 0.75f, 2);
	
	private final Values values = new Values();
	
	/** Maps threads to the temporary key for thread-safe access to chunkMap. */
	private final Map<Thread, ShortVec2> thread2KeyMap = new ConcurrentHashMap<>(2, 0.75f, 2);
	
	/** Temporary key for thread-safe access to chunkMap. */
	private ShortVec2 getKey() {
		return thread2KeyMap.computeIfAbsent(Thread.currentThread(), k -> new ShortVec2(0, 0));
	}
	
	public DimensionMarkersData(MarkersData parent, int dimension) {
		this.parent = parent;
		this.dimension = dimension;
	}
	
	public int getDimension() {
		return dimension;
	}
	
	/** The "chunk" here is {@link MarkersData#CHUNK_STEP} times larger than the
	 * Minecraft 16x16 chunk! */
	public List<Marker> getMarkersAtChunk(int x, int z) {
		return chunkMap.get(getKey().set(x, z));
	}
	
	/** Insert marker into a list at chunk coordinates, maintaining the ordering
	 * of the list by Z coordinate. */
	public void insertMarker(Marker marker) {
		ShortVec2 key = getKey().set(
				marker.getChunkX() / MarkersData.CHUNK_STEP,
				marker.getChunkZ() / MarkersData.CHUNK_STEP);
		List<Marker> list = chunkMap.get(key);
		if (list == null) {
			list = new CopyOnWriteArrayList<>();
			chunkMap.put(key.clone(), list);
		}
		boolean inserted = false;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getZ() > marker.getZ()) {
				list.add(i, marker);
				inserted = true;
				break;
			}
		}
		if (!inserted) {
			list.add(marker);
		}
		size++;
		parent.markDirty();
	}
	
	public boolean removeMarker(Marker marker) {
		size--;
		return getMarkersAtChunk(
				marker.getChunkX() / MarkersData.CHUNK_STEP,
				marker.getChunkZ() / MarkersData.CHUNK_STEP).remove(marker);
	}
	
	/** The returned view is immutable, i.e. remove() won't work. */
	public Collection<Marker> getAllMarkers() {
		return values;
	}
	
	private class Values extends AbstractCollection<Marker> {
		@Override
		public Iterator<Marker> iterator() {
			return new ListMapValueIterator<>(chunkMap).setImmutable(true);
		}
		@Override
		public int size() {
			return size;
		}
		
	}
}
