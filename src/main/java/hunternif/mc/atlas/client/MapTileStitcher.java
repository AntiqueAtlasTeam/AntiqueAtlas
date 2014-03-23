package hunternif.mc.atlas.client;

import hunternif.mc.atlas.core.BiomeTextureMap;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;

public class MapTileStitcher {
	public static final MapTileStitcher instance = new MapTileStitcher();
	
	public void stitchAdjacentTiles(Map<ShortVec2, MapTile> tiles, ShortVec2 coords, MapTile tile) {
		MapTile top = tiles.get(coords.add(0, -1));
		MapTile topLeft = tiles.get(coords.add(-1, 0));
		MapTile left = tiles.get(coords.add(0, 1));
		MapTile bottomLeft = tiles.get(coords.add(0, 1));
		MapTile bottom = tiles.get(coords.add(1, 0));
		MapTile bottomRight = tiles.get(coords.add(1, 0));
		MapTile right = tiles.get(coords.add(0, -1));
		MapTile topRight = tiles.get(coords.add(0, -1));
		
		stitchVertically(top, tile);
		stitchVertically(tile, bottom);
		stitchHorizontally(left, tile);
		stitchHorizontally(tile, right);
		
		stitchSquare(topLeft, top, left, tile);
		stitchSquare(top, topRight, tile, right);
		stitchSquare(left, tile, bottomLeft, bottom);
		stitchSquare(tile, right, bottom, bottomRight);
	}
	public void stitchVertically(MapTile upper, MapTile lower) {
		if (upper == null || lower == null || !shouldStitch(upper.biomeID, lower.biomeID)) return;
		if (upper.bottomLeft == MapTile.CONVEX) upper.bottomLeft = MapTile.VERTICAL;
		if (upper.bottomLeft == MapTile.HORIZONTAL) upper.bottomLeft = MapTile.CONCAVE;
		if (upper.bottomRight == MapTile.CONVEX) upper.bottomRight = MapTile.VERTICAL;
		if (upper.bottomRight == MapTile.HORIZONTAL) upper.bottomRight = MapTile.CONCAVE;
		if (lower.topLeft == MapTile.CONVEX) lower.topLeft = MapTile.VERTICAL;
		if (lower.topLeft == MapTile.HORIZONTAL) lower.topLeft = MapTile.CONCAVE;
		if (lower.topRight == MapTile.CONVEX) lower.topRight = MapTile.VERTICAL;
		if (lower.topRight == MapTile.HORIZONTAL) lower.topRight = MapTile.CONCAVE;
	}
	public void stitchHorizontally(MapTile left, MapTile right) {
		if (left == null || right == null || !shouldStitch(left.biomeID, right.biomeID)) return;
		if (left.topRight == MapTile.CONVEX) left.topRight = MapTile.HORIZONTAL;
		if (left.topRight == MapTile.VERTICAL) left.topRight = MapTile.CONCAVE;
		if (left.bottomRight == MapTile.CONVEX) left.bottomRight = MapTile.HORIZONTAL;
		if (left.bottomRight == MapTile.VERTICAL) left.bottomRight = MapTile.CONCAVE;
		if (right.topLeft == MapTile.CONVEX) right.topLeft = MapTile.HORIZONTAL;
		if (right.topLeft == MapTile.VERTICAL) right.topLeft = MapTile.CONCAVE;
		if (right.bottomLeft == MapTile.CONVEX) right.bottomLeft = MapTile.HORIZONTAL;
		if (right.bottomLeft == MapTile.VERTICAL) right.bottomLeft = MapTile.CONCAVE;
	}
	public void stitchSquare(MapTile topLeft, MapTile topRight,
							MapTile bottomLeft, MapTile bottomRight) {
		if (topLeft == null || topRight == null || bottomLeft == null || bottomRight == null ||
				!shouldStitch(topLeft.biomeID, topRight.biomeID, bottomLeft.biomeID, bottomRight.biomeID)) {
			return;
		}
		topLeft.bottomRight = MapTile.FULL;
		topRight.bottomLeft = MapTile.FULL;
		bottomLeft.topRight = MapTile.FULL;
		bottomRight.topLeft = MapTile.FULL;
	}
	
	public boolean shouldStitch(int ... biomeIDs) {
		return BiomeTextureMap.instance().haveSameTexture(biomeIDs);
	}
}
