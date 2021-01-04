package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.util.Rect;
import net.minecraft.util.Identifier;

public interface ITileStorage {
    void setTile(int x, int y, Identifier tile);

    /**
     * Returns the Tile previously set at given coordinates.
     */
    Identifier removeTile(int x, int y);

    Identifier getTile(int x, int y);

    boolean hasTileAt(int x, int y);

    Rect getScope();
}
