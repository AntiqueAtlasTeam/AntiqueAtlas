package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Rect;

public interface ITileStorage {
	void setTile(int x, int y, Tile tile);
	/** Returns the Tile previously set at given coordinates. */
	Tile removeTile(int x, int y);
	Tile getTile(int x, int y);
	boolean hasTileAt(int x, int y);
	Rect getScope();
}
