package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.Rect;
import net.minecraft.nbt.CompoundNBT;

/** Represents a group of tiles that may be sent/stored as a single NBT */
public class TileGroup implements ITileStorage {

	public static final String TAG_POSITION = "p";
	public static final String TAG_TILES = "t";

	/** The width/height of this TileGroup */
	public static final int CHUNK_STEP = 16;

	/** The area of chunks this group covers */
	Rect scope = new Rect(0, 0, CHUNK_STEP, CHUNK_STEP);

	/** The tiles in this scope */
	TileKind[][] tiles = new TileKind[CHUNK_STEP][CHUNK_STEP];
	
	public TileGroup(int x, int y) {
		scope.minX = x;
		scope.minY = y;
		scope.maxX = scope.minX + CHUNK_STEP - 1;
		scope.maxY = scope.minY + CHUNK_STEP - 1;
	}

	public void readFromNBT(CompoundNBT compound) {
		scope.minX = compound.getIntArray(TAG_POSITION)[0];
		scope.minY = compound.getIntArray(TAG_POSITION)[1];
		scope.maxX = scope.minX + CHUNK_STEP - 1;
		scope.maxY = scope.minY + CHUNK_STEP - 1;
		int[] tileArray = compound.getIntArray(TAG_TILES);
		for (int y = 0; y < CHUNK_STEP; y++) {
			for (int x = 0; x < CHUNK_STEP; x++) {
				// order:
				// 0 1 2
				// 3 4 5
				// 6 7 8
				if (tileArray[x + y * CHUNK_STEP] == -1) {
					tiles[x][y] = null;
				} else {
					tiles[x][y] = TileKindFactory.get(tileArray[x + y * CHUNK_STEP]);
				}
			}
		}
	}

	public CompoundNBT writeToNBT(CompoundNBT compound) {
		int[] tileArray = new int[CHUNK_STEP * CHUNK_STEP];
		int[] pos = { scope.minX, scope.minY };
		for (int y = 0; y < CHUNK_STEP; y++) {
			for (int x = 0; x < CHUNK_STEP; x++) {
				// order:
				// 0 1 2
				// 3 4 5
				// 6 7 8
				if (tiles[x][y] == null) {
					tileArray[x + y * CHUNK_STEP] = -1;
				} else {
					tileArray[x + y * CHUNK_STEP] = tiles[x][y].getId();
				}
			}
		}
		compound.putIntArray(TAG_POSITION, pos);
		compound.putIntArray(TAG_TILES, tileArray);
		return compound;
	}

	@Override
	public void setTile(int x, int y, TileKind tile) {
		if (x >= scope.minX && y >= scope.minY && x <= scope.maxX && y <= scope.maxY) {
			int rx = x - scope.minX;
			int ry = y - scope.minY;
			tiles[rx][ry] = tile;
		}else{
			Log.warn("TileGroup tried to set tile out of bounds:"+
		"\n\tbounds:"+scope+
		"\n\ttarget: x:"+x+", y:"+y);
		}
	}

	@Override
	public TileKind removeTile(int x, int y) {
		TileKind tmp = getTile(x,y);
		setTile(x,y,null);
		return tmp;
	}

	@Override
	public TileKind getTile(int x, int y) {
		if (x >= scope.minX && y >= scope.minY && x <= scope.maxX && y <= scope.maxY) {
			int rx = x - scope.minX;
			int ry = y - scope.minY;
			return tiles[rx][ry];
		}
		return null;
	}

	@Override
	public boolean hasTileAt(int x, int y) {
		return getTile(x, y) != null;
	}

	@Override
	public Rect getScope() {
		return scope;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TileGroup))
			return false;
		TileGroup other= (TileGroup) obj;
		if (!scope.equals(other.scope))
			return false;
		int a;
		int b;
		for (int y = 0; y < CHUNK_STEP; y++) {
			for (int x = 0; x < CHUNK_STEP; x++) {
				a = (this.tiles[x][y] == null)? -1:this.tiles[x][y].getId();
				b = (other.tiles[x][y] == null)? -1:other.tiles[x][y].getId();
				if (a!=b)
					return false;
			}
		}
		return true;
	}
}
