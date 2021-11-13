package hunternif.mc.impl.atlas.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class MathUtil {
    /**
     * Returns the nearest number to a multiple of a given number.
     */
    public static int roundToBase(int a, int base) {
        return a - a % base;
    }

    /**
     * Returns the nearest, largest by value, multiple of a given number.
     */
    public static int ceilAbsToBase(int a, int base) {
        int ceil = a - a % base;
        if (a >= 0) {
            return a > ceil ? ceil + base : ceil;
        } else {
            return a < ceil ? ceil - base : ceil;
        }
    }

    public static AABB toAABB(BoundingBox box) {
        return new AABB(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
    }

    public static BlockPos getCenter(BoundingBox box) {
        return new BlockPos(box.minX() + (box.getXSpan() / 2), box.minY() + (box.getYSpan() / 2), box.minZ() + (box.getZSpan() / 2));
    }
}
