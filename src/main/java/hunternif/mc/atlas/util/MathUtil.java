package hunternif.mc.atlas.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class MathUtil {
	/** Returns the nearest number to a multiple of a given number. */
	public static int roundToBase(int a, int base) {
		return a - a % base;
	}
	
	/** Returns the nearest, largest by value, multiple of a given number. */
	public static int ceilAbsToBase(int a, int base) {
		int ceil = a - a % base;
		if (a >= 0) {
			return a > ceil ? ceil + base : ceil;
		} else {
			return a < ceil ? ceil - base : ceil;
		}
	}

	public static AxisAlignedBB toAABB(StructureBoundingBox box) {
		return new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public static BlockPos getCenter(StructureBoundingBox box) {
		return new BlockPos(box.minX + (box.getXSize() / 2), box.minY + (box.getYSize() / 2), box.minZ + (box.getZSize() / 2));
	}
}
