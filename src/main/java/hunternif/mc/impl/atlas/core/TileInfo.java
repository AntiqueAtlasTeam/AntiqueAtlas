package hunternif.mc.impl.atlas.core;

import net.minecraft.util.Identifier;

public class TileInfo {
    public final int x, z;
    public final Identifier id;

    public TileInfo(int x, int z, Identifier id) {
        this.x = x;
        this.z = z;
        this.id = id;
    }
}
