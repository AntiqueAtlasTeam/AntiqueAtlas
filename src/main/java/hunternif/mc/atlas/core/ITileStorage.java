package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.Rect;

public interface ITileStorage {
	void setTile(int x, int y, TileKind tile);
	/** Returns the Tile previously set at given coordinates. */
	TileKind removeTile(int x, int y);
	TileKind getTile(int x, int y);
	boolean hasTileAt(int x, int y);
	Rect getScope();
}
