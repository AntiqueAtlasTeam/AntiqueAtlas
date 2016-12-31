package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.Rect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

/** Represents a group of tiles that may be sent/stored as a single NBT */
public class TileGroup extends WorldSavedData implements ITileStorage {

	public static final int VERSION = 1;

	public static final String TAG_VERSION = "v";
	public static final String TAG_POSITION = "p";
	public static final String TAG_TILES = "t";

	/** The width/height of this TileGroup */
	public static final int CHUNK_STEP = 16;

	/** The area of chunks this group covers */
	Rect scope = new Rect(0, 0, CHUNK_STEP, CHUNK_STEP);

	/** The tiles in this scope */
	Tile[][] tiles = new Tile[CHUNK_STEP][CHUNK_STEP];

	public TileGroup(String p_i2141_1_) {
		super(p_i2141_1_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.getInteger(TAG_VERSION) < VERSION) {
			Log.warn("Outdated atlas data format! Was %d but current is %d", compound.getInteger(TAG_VERSION), VERSION);
		}
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
					tiles[x][y] = new Tile(tileArray[x + y * CHUNK_STEP]);
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound p_76187_1_) {
		NBTTagCompound me = new NBTTagCompound();
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
					tileArray[x + y * CHUNK_STEP] = tiles[x][y].biomeID;
				}
			}
		}
		me.setInteger(TAG_VERSION, VERSION);
		me.setIntArray(TAG_POSITION, pos);
		me.setIntArray(TAG_TILES, tileArray);
	}

	@Override
	public void setTile(int x, int y, Tile tile) {
		if (x >= scope.minX && y >= scope.minY && x < scope.maxX && y < scope.maxY) {
			int rx = x - scope.minX;
			int ry = y - scope.minY;
			tiles[rx][ry] = tile;
		}
	}

	@Override
	public Tile removeTile(int x, int y) {
		Tile tmp = getTile(x,y);
		setTile(x,y,null);
		return tmp;
	}

	@Override
	public Tile getTile(int x, int y) {
		if (x >= scope.minX && y >= scope.minY && x < scope.maxX && y < scope.maxY) {
			int rx = x - scope.minX;
			int ry = y - scope.minY;
			return tiles[rx][ry];
		}
		return null;
	}

	@Override
	public boolean hasTileAt(int x, int y) {
		return getTile(x, y) == null;
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
				a = (this.tiles[x][y] == null)? -1:this.tiles[x][y].biomeID;
				b = (other.tiles[x][y] == null)? -1:other.tiles[x][y].biomeID;
				if (a!=b)
					return false;
			}
		}
		return true;
	}
}
