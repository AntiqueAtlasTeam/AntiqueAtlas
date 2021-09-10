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
        return new Box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static BlockPos getCenter(BlockBox box) {
        return new BlockPos(box.minX + (box.getBlockCountX() / 2), box.minY + (box.getBlockCountY() / 2), box.minZ + (box.getBlockCountZ() / 2));
    }
}
