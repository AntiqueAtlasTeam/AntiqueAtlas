package hunternif.mc.atlas.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class BoundingBox {

    static public BlockPos getCenter(StructureBoundingBox box)
    {
        double x = (box.maxX + box.minX) / 2;
        double y = (box.maxY + box.minY) / 2;
        double z = (box.maxZ + box.minZ) / 2;

        return new BlockPos(x, y, z);
    }
}
