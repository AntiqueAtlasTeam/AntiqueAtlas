package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.util.Rect;
import net.minecraft.util.ResourceLocation;

public interface ITileStorage {
	void setTile(int x, int y, ResourceLocation tile);
	/** Returns the Tile previously set at given coordinates. */
	ResourceLocation removeTile(int x, int y);
	ResourceLocation getTile(int x, int y);
	boolean hasTileAt(int x, int y);
	Rect getScope();
}
