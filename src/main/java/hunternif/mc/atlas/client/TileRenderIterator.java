package hunternif.mc.atlas.client;

import hunternif.mc.atlas.client.SubTile.Shape;
import hunternif.mc.atlas.core.ITileStorage;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.util.Rect;

import java.util.Iterator;

/**
 * Iterates through a tile storage for the purpose of rendering their textures.
 * Returned is an array of 4 {@link SubTile}s which constitute a whole
 * {@link Tile}.
 * The SubTile objects are generated on the fly and not retained in memory.
 * May return null!
 * @author Hunternif
 */
public class TileRenderIterator implements Iterator<SubTileQuartet> {

	private final ITileStorage tiles;
	
	/** How many chunks a tile spans. Used for viewing the map at a scale below
	 * the threshold at which the tile texture is of minimum size and no longer
	 * scales down. Can't be less than 1. */
	private int step = 1;
	public void setStep(int step) {
		if (step >= 1) {
			this.step = step;
		}
	}
	
	/** The scope of iteration. */
	private final Rect scope = new Rect();
	public void setScope(int minX, int minY, int maxX, int maxY) {
		scope.set(minX, minY, maxX, maxY);
		chunkX = minX;
		chunkY = minY;
	}
	public void setScope(Rect scope){
		this.scope.set(scope);
		chunkX = scope.minX;
		chunkY = scope.minY;
	}
	
	/**
	 * The group of adjacent tiles used for traversing the storage.
	 * <pre>
	 *   a b
	 * c d e f
	 * g h i j
	 *   k l
	 * </pre>
	 * 'i' is at (x, y).
	 * The returned array of subtiles represents the corner 'd-e-h-i'
	 */
	private Tile a, b, c, d, e, f, g, h, i, j, k, l;
	
	private final SubTileQuartet quartet = new SubTileQuartet();
	
	/** Current index into the tile storage, which presumably has every tile spanning exactly 1 chunk. */
	private int chunkX, chunkY;
	/** Current index into the grid of subtiles, starting at (-1, -1). */
	private int subtileX = -1, subtileY = -1;
	
	public TileRenderIterator(ITileStorage tiles) {
		this.tiles = tiles;
		setScope(tiles.getScope());
	}
	
	@Override
	public boolean hasNext() {
		return chunkX >= scope.minX && chunkX <= scope.maxX + 1 &&
			   chunkY >= scope.minY && chunkY <= scope.maxY + 1;
	}

	@Override
	public SubTileQuartet next() {
		a = b;
		b = tiles.getTile(chunkX, chunkY - step * 2);
		c = d;
		d = e;
		e = f;
		f = tiles.getTile(chunkX + step, chunkY - step);
		g = h;
		h = i;
		i = j;
		j = tiles.getTile(chunkX + step, chunkY);
		k = l;
		l = tiles.getTile(chunkX, chunkY + step);
		
		quartet.setCoords(subtileX, subtileY);
		quartet.get(0).tile = d;
		quartet.get(1).tile = e;
		quartet.get(2).tile = h;
		quartet.get(3).tile = i;
		
		if (shouldStitch(d, e, h, i)) {
			// Full
			for (SubTile subtile : quartet) {
				subtile.shape = Shape.FULL;
			}
		} else {
			// At first assume all convex:
			for (SubTile subtile : quartet) {
				subtile.shape = Shape.CONVEX;
			}
			
			// Connect horizontally:
			if (shouldStitch(d, e)) {
				stitchHorizontally(quartet.get(0));
				stitchHorizontally(quartet.get(1));
			}
			if (shouldStitch(h, i)) {
				stitchHorizontally(quartet.get(2));
				stitchHorizontally(quartet.get(3));
			}
			
			// Connect vertically:
			if (shouldStitch(d, h)) {
				stitchVertically(quartet.get(0));
				stitchVertically(quartet.get(2));
			}
			if (shouldStitch(e, i)) {
				stitchVertically(quartet.get(1));
				stitchVertically(quartet.get(3));
			}
			
			// For any convex subtile check for single-object:
			if (quartet.get(0).shape == Shape.CONVEX && !shouldStitch(d, a) && !shouldStitch(d, c)) {
				quartet.get(0).shape = Shape.SINGLE_OBJECT;
			}
			if (quartet.get(1).shape == Shape.CONVEX && !shouldStitch(e, b) && !shouldStitch(e, f)) {
				quartet.get(1).shape = Shape.SINGLE_OBJECT;
			}
			if (quartet.get(2).shape == Shape.CONVEX && !shouldStitch(h, g) && !shouldStitch(h, k)) {
				quartet.get(2).shape = Shape.SINGLE_OBJECT;
			}
			if (quartet.get(3).shape == Shape.CONVEX && !shouldStitch(i, k) && !shouldStitch(i, l)) {
				quartet.get(3).shape = Shape.SINGLE_OBJECT;
			}
		}
		
		chunkX += step;
		subtileX += 2;
		if (chunkX > scope.maxX + 1) {
			chunkX = scope.minX;
			subtileX = -1;
			chunkY += step;
			subtileY += 2;
			a = null;
			b = null;
			c = null;
			d = null;
			e = null;
			f = tiles.getTile(chunkX, chunkY - step);
			g = null;
			h = null;
			i = null;
			j = tiles.getTile(chunkX, chunkY);
			k = null;
			l = null;
		}
		return quartet;
	}
	
	private static boolean shouldStitch(Tile ... tiles) {
		int[] biomeIDs = new int[tiles.length];
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] == null) return false;
			else biomeIDs[i] = tiles[i].biomeID;
		}
		return BiomeTextureMap.instance().haveSameTexture(biomeIDs);
	}
	
	/** Change the shape of the subtile in order to stitch it vertically
	 * to another subtile. It doesn't matter if it's top or bottom. */
	private static void stitchVertically(SubTile subtile) {
		if (subtile.shape == Shape.HORIZONTAL) subtile.shape = Shape.CONCAVE;
		if (subtile.shape == Shape.CONVEX) subtile.shape = Shape.VERTICAL;
	}
	/** Change the shape of the subtile in order to stitch it horizontally
	 * to another subtile. It doesn't matter if it's left or right. */
	private static void stitchHorizontally(SubTile subtile) {
		if (subtile.shape == Shape.VERTICAL) subtile.shape = Shape.CONCAVE;
		if (subtile.shape == Shape.CONVEX) subtile.shape = Shape.HORIZONTAL;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException("cannot remove subtiles from tile storage");
	}
	
}
