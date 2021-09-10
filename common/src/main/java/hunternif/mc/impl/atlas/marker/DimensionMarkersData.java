package hunternif.mc.impl.atlas.marker;

import hunternif.mc.impl.atlas.util.ListMapValueIterator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DimensionMarkersData {
	private final MarkersData parent;
	private final RegistryKey<World> world;

	private int size = 0;

	private final Map<ChunkPos, List<Marker>> chunkMap =
			new ConcurrentHashMap<>(2, 0.75f, 2);

	private final Values values = new Values();

	public DimensionMarkersData(MarkersData parent, RegistryKey<World> world) {
		this.parent = parent;
		this.world = world;
	}

	public RegistryKey<World> getWorld() {
		return world;
	}

	/** The "chunk" here is {@link MarkersData#CHUNK_STEP} times larger than the
	 * Minecraft 16x16 chunk! */
	public List<Marker> getMarkersAtChunk(int x, int z) {
		return chunkMap.get(new ChunkPos(x, z));
	}

	/** Insert marker into a list at chunk coordinates, maintaining the ordering
	 * of the list by Z coordinate. */
	public void insertMarker(Marker marker) {
		ChunkPos key = new ChunkPos(
				marker.getChunkX() / MarkersData.CHUNK_STEP,
				marker.getChunkZ() / MarkersData.CHUNK_STEP);
		List<Marker> list = chunkMap.get(key);
		if (list == null) {
			list = new CopyOnWriteArrayList<>();
			chunkMap.put(key, list);
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
