package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** All tiles seen in dimension */
public class DimensionData {
	public final int dimension;
	
	/** a map of chunks the player has seen. This map is thread-safe.
	 * CAREFUL! Don't modify chunk coordinates that are already put in the map! */
	private final Map<ShortVec2, MapTile> tiles;
	
	/** Limits of explored area. */
	private short minX = Short.MAX_VALUE, maxX = Short.MIN_VALUE,
				  minY = Short.MAX_VALUE, maxY = Short.MIN_VALUE;
	
	protected DimensionData(int dimension) {
		this(dimension, new ConcurrentHashMap<ShortVec2, MapTile>());
	}
	
	protected DimensionData(int dimension, Map<ShortVec2, MapTile> tiles) {
		this.dimension = dimension;
		this.tiles = tiles;
	}
	
	public Map<ShortVec2, MapTile> getSeenChunks() {
		return tiles;
	}

	/** Put tile in the tile map and update explored area limits. */
	protected void putTile(ShortVec2 tileCoords, MapTile tile) {
		tiles.put(tileCoords, tile);
		if (tileCoords.x < minX) minX = tileCoords.x;
		if (tileCoords.x > maxX) maxX = tileCoords.x;
		if (tileCoords.y < minY) minY = tileCoords.y;
		if (tileCoords.y > maxY) maxY = tileCoords.y;
	}
	
	public short getMinX() {
		return minX;
	}
	public short getMaxX() {
		return maxX;
	}
	public short getMinY() {
		return minY;
	}
	public short getMaxY() {
		return maxY;
	}
}
