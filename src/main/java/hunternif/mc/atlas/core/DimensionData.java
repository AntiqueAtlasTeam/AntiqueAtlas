package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.Rect;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** All tiles seen in dimension. Thread-safe (probably) */
public class DimensionData implements ITileStorage {
	public final AtlasData parent;
	public final int dimension;
	
	private int browsingX, browsingY;
	private double browsingZoom = 0.5;
	
	/** a map of chunks the player has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private final Map<ShortVec2, Tile> tiles = new ConcurrentHashMap<ShortVec2, Tile>(2, 0.75f, 2);
	
	/** Maps threads to the temporary key for thread-safe access to the tile map. */
	private final Map<Thread, ShortVec2> thread2KeyMap = new ConcurrentHashMap<Thread, ShortVec2>(2, 0.75f, 2);
	
	/** Limits of explored area, in chunks. */
	private final Rect scope = new Rect();
	
	public DimensionData(AtlasData parent, int dimension) {
		this.parent = parent;
		this.dimension = dimension;
	}
	
	public Map<ShortVec2, Tile> getSeenChunks() {
		return tiles;
	}
	
	/** Set world coordinates that are in the center of the GUI. */
	public void setBrowsingPosition(int x, int y, double zoom) {
		this.browsingX = x;
		this.browsingY = y;
		this.browsingZoom = zoom;
		if (browsingZoom <= 0) {
			Log.warn("Setting map zoom to invalid value of %f", zoom);
			browsingZoom = AntiqueAtlasMod.settings.minScale;
		}
		parent.markDirty();
	}
	public int getBrowsingX() {
		return browsingX;
	}
	public int getBrowsingY() {
		return browsingY;
	}
	public double getBrowsingZoom() {
		return browsingZoom;
	}
	
	/** Temporary key for thread-safe access to the tile map. */
	private ShortVec2 getKey() {
		ShortVec2 key = thread2KeyMap.get(Thread.currentThread());
		if (key == null) {
			key = new ShortVec2(0, 0);
			thread2KeyMap.put(Thread.currentThread(), key);
		}
		return key;
	}

	@Override
	public void setTile(int x, int y, Tile tile) {
		tiles.put(new ShortVec2(x, y), tile);
		scope.extendTo(x, y);
		parent.markDirty();
	}
	
	@Override
	public Tile removeTile(int x, int y) {
		Tile oldTile = tiles.remove(getKey().set(x, y));
		if (oldTile != null) parent.markDirty();
		return oldTile;
	}

	@Override
	public Tile getTile(int x, int y) {
		return tiles.get(getKey().set(x, y));
	}

	@Override
	public boolean hasTileAt(int x, int y) {
		return tiles.containsKey(getKey().set(x, y));
	}

	@Override
	public Rect getScope() {
		return scope;
	}
	
	@Override
	public DimensionData clone() {
		DimensionData data = new DimensionData(parent, dimension);
		data.tiles.putAll(tiles);
		data.scope.set(scope);
		return data;
	}
}
