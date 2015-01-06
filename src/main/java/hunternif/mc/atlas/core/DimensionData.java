package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Rect;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** All tiles seen in dimension */
public class DimensionData implements ITileStorage{
	public final int dimension;
	
	/** a map of chunks the player has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private final Map<ShortVec2, Tile> tiles = new ConcurrentHashMap<ShortVec2, Tile>();
	
	private final ShortVec2 tempCoords = new ShortVec2(0, 0);
	
	/** Limits of explored area, in chunks. */
	private final Rect scope = new Rect();
	
	public DimensionData(int dimension) {
		this.dimension = dimension;
	}
	
	public Map<ShortVec2, Tile> getSeenChunks() {
		return tiles;
	}

	@Override
	public void setTile(int x, int y, Tile tile) {
		tiles.put(new ShortVec2(x, y), tile);
		scope.extendTo(x, y);
	}

	@Override
	public Tile getTile(int x, int y) {
		return tiles.get(tempCoords.set(x, y));
	}

	@Override
	public boolean hasTileAt(int x, int y) {
		return tiles.containsKey(tempCoords.set(x, y));
	}

	@Override
	public Rect getScope() {
		return scope;
	}
	
	@Override
	public DimensionData clone() {
		DimensionData data = new DimensionData(dimension);
		data.tiles.putAll(tiles);
		data.scope.set(scope);
		return data;
	}
}
