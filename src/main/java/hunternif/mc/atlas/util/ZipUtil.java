package hunternif.mc.atlas.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtil {
	public static byte[] decompressByteArray(byte[] bytes) {
		ByteArrayOutputStream baos = null;
		Inflater iflr = new Inflater();
		iflr.setInput(bytes);
		baos = new ByteArrayOutputStream();
		byte[] tmp = new byte[4 * 1024];
		try {
			while (!iflr.finished()) {
				int size = iflr.inflate(tmp);
				baos.write(tmp, 0, size);
			}
		} catch (DataFormatException e) {
			Log.error(e, "Error unzipping bytes");
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				Log.error(e, "Error unzipping bytes");
			}
		}
		return baos.toByteArray();
	}
	
	public static byte[] compressByteArray(byte[] bytes) {
		ByteArrayOutputStream baos = null;
		Deflater dfl = new Deflater();
		dfl.setLevel(Deflater.BEST_COMPRESSION);
		dfl.setInput(bytes);
		dfl.finish();
		baos = new ByteArrayOutputStream();
		byte[] tmp = new byte[4 * 1024];
		try {
			while (!dfl.finished()) {
				int size = dfl.deflate(tmp);
				baos.write(tmp, 0, size);
			}
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				Log.error(e, "Error zipping bytes");
			}
		}
		return baos.toByteArray();
	}
}
