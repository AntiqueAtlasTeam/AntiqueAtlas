package hunternif.mc.atlas.util;

public class MathUtil {
	/** Returns the nearest number to a multiple of a given number. */
	public static int roundToBase(int a, int base) {
		return a - a % base;
	}
}
