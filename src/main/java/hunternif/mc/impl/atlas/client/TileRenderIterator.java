package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.client.SubTile.Part;
import hunternif.mc.impl.atlas.client.SubTile.Shape;
import hunternif.mc.impl.atlas.core.ITileStorage;
import hunternif.mc.impl.atlas.util.Rect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.Iterator;

/**
 * Iterates through a tile storage for the purpose of rendering their textures.
 * Returned is an array of 4 {@link SubTile}s which constitute a whole Tile.
 * The SubTile objects are generated on the fly and not retained in memory.
 * May return null!
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class TileRenderIterator implements Iterator<SubTileQuartet>, Iterable<SubTileQuartet> {

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
	 *   a | b
	 * c d | e f
	 * ---------
	 * g h | i j
	 *   k | l
	 * </pre>
	 * 'i' is at (x, y).
	 * The returned array of subtiles represents the corner 'd-e-h-i'
	 */
	private Identifier a, b, c, d, e, f, g, h, i, j, k, l;

	/** Shortcuts for the quartet. */
	private final SubTile _d = new SubTile(Part.BOTTOM_RIGHT),
						  _e = new SubTile(Part.BOTTOM_LEFT),
						  _h = new SubTile(Part.TOP_RIGHT),
						  _i = new SubTile(Part.TOP_LEFT);
	private final SubTileQuartet quartet = new SubTileQuartet(_d, _e, _h, _i);

	/** Current index into the tile storage, which presumably has every tile spanning exactly 1 chunk. */
	private int chunkX, chunkY;
	/** Current index into the grid of subtiles, starting at (-1, -1). */
	private int subtileX = -1, subtileY = -1;

	public TileRenderIterator(ITileStorage tiles) {
		this.tiles = tiles;
		setScope(tiles.getScope());
	}

	@Override
	public Iterator<SubTileQuartet> iterator() {
		return this;
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

		quartet.setChunkCoords(chunkX, chunkY, step);
		quartet.setCoords(subtileX, subtileY);
		_d.tile = d;
		_e.tile = e;
		_h.tile = h;
		_i.tile = i;

		// At first assume all convex:
		for (SubTile subtile : quartet) {
			subtile.shape = Shape.CONVEX;
		}

		// Connect horizontally:
		if (shouldStitchToHorizontally(d, e)) {
			stitchHorizontally(_d);
		}
		if (shouldStitchToHorizontally(e, d)) {
			stitchHorizontally(_e);
		}
		if (shouldStitchToHorizontally(h, i)) {
			stitchHorizontally(_h);
		}
		if (shouldStitchToHorizontally(i, h)) {
			stitchHorizontally(_i);
		}

		// Connect vertically:
		if (shouldStitchToVertically(d, h)) {
			stitchVertically(_d);
			if (_d.shape == Shape.CONCAVE && shouldStitchTo(d, i)) {
				_d.shape = Shape.FULL;
			}
		}
		if (shouldStitchToVertically(h, d)) {
			stitchVertically(_h);
			if (_h.shape == Shape.CONCAVE && shouldStitchTo(h, e)) {
				_h.shape = Shape.FULL;
			}
		}
		if (shouldStitchToVertically(e, i)) {
			stitchVertically(_e);
			if (_e.shape == Shape.CONCAVE && shouldStitchTo(e, h)) {
				_e.shape = Shape.FULL;
			}
		}
		if (shouldStitchToVertically(i, e)) {
			stitchVertically(_i);
			if (_i.shape == Shape.CONCAVE && shouldStitchTo(i, d)) {
				_i.shape = Shape.FULL;
			}
		}

		// For any convex subtile check for single-object:
		if (_d.shape == Shape.CONVEX && !shouldStitchToVertically(d, a) && !shouldStitchToHorizontally(d, c)) {
			_d.shape = Shape.SINGLE_OBJECT;
		}
		if (_e.shape == Shape.CONVEX && !shouldStitchToVertically(e, b) && !shouldStitchToHorizontally(e, f)) {
			_e.shape = Shape.SINGLE_OBJECT;
		}
		if (_h.shape == Shape.CONVEX && !shouldStitchToHorizontally(h, g) && !shouldStitchToVertically(h, k)) {
			_h.shape = Shape.SINGLE_OBJECT;
		}
		if (_i.shape == Shape.CONVEX && !shouldStitchToHorizontally(i, j) && !shouldStitchToVertically(i, l)) {
			_i.shape = Shape.SINGLE_OBJECT;
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

	/** Whether the first tile should be stitched to the 2nd (in any direction)
	 * (but the opposite is not always true!) */
	private static boolean shouldStitchTo(Identifier tile, Identifier to) {
		if (tile == null) return false;
		TextureSet set = TileTextureMap.instance().getTextureSet(tile);
		TextureSet toSet = TileTextureMap.instance().getTextureSet(to);
		return set != null && set.shouldStitchTo(toSet);
	}
	/** Whether the first tile should be stitched to the 2nd along the X axis
	 * (but the opposite is not always true!) */
	private static boolean shouldStitchToHorizontally(Identifier tile, Identifier to) {
		if (tile == null) return false;
		TextureSet set = TileTextureMap.instance().getTextureSet(tile);
		TextureSet toSet = TileTextureMap.instance().getTextureSet(to);
		return set != null && set.shouldStitchToHorizontally(toSet);
	}
	/** Whether the first tile should be stitched to the 2nd along the Z axis
	 * (but the opposite is not always true!) */
	private static boolean shouldStitchToVertically(Identifier tile, Identifier to) {
		if (tile == null) return false;
		TextureSet set = TileTextureMap.instance().getTextureSet(tile);
		TextureSet toSet = TileTextureMap.instance().getTextureSet(to);
		return set != null && set.shouldStitchToVertically(toSet);
	}

	/** Change the shape of the subtile in order to stitch it vertically
	 * to another subtile. It doesn't matter if it's top or bottom. */
	private static void stitchVertically(SubTile subtile) {
		if (subtile.shape == Shape.HORIZONTAL) subtile.shape = Shape.CONCAVE;
		else if (subtile.shape == Shape.CONVEX) subtile.shape = Shape.VERTICAL;
	}
	/** Change the shape of the subtile in order to stitch it horizontally
	 * to another subtile. It doesn't matter if it's left or right. */
	private static void stitchHorizontally(SubTile subtile) {
		if (subtile.shape == Shape.VERTICAL) subtile.shape = Shape.CONCAVE;
		else if (subtile.shape == Shape.CONVEX) subtile.shape = Shape.HORIZONTAL;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("cannot remove subtiles from tile storage");
	}

}
