package hunternif.mc.atlas.util;

public class ByteUtil {
	public static int unsignedByteToInt(byte b) {
		return b & 0xff;
	}
	
	public static int[] unsignedByteToIntArray(byte[] bytes) {
		int[] ints = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			ints[i] = unsignedByteToInt(bytes[i]);
		}
		return ints;
	}
}
