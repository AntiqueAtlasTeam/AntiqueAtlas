package hunternif.mc.impl.atlas.util;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

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

    public static Box toAABB(BlockBox box) {
        return new Box(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ());
    }

    public static BlockPos getCenter(BlockBox box) {
        return new BlockPos(box.getMinX() + (box.getBlockCountX() / 2), box.getMinY() + (box.getBlockCountY() / 2), box.getMinZ() + (box.getBlockCountZ() / 2));
    }
}
