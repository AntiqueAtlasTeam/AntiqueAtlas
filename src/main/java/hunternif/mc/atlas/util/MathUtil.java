package hunternif.mc.atlas.util;

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
}
