package hunternif.mc.impl.atlas.util;

public class ByteUtil {
	private static int unsignedByteToInt(byte b) {
		return b & 0xff;
	}

	public static int[] unsignedByteToIntArray(Object array) {
		if (array instanceof int[]) {
			return (int[]) array;
		}
		else if (array instanceof byte[]) {
			return unsignedByteToIntArray((byte[])array);
		}
		else {
			return new int[0];
		}
	}

	public static int[] unsignedByteToIntArray(byte[] bytes) {
		int[] ints = new int[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			ints[i] = unsignedByteToInt(bytes[i]);
		}
		return ints;
	}
}
