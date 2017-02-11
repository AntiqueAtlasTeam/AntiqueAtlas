package hunternif.mc.atlas.util;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * A simple logging helper that is not directly dependent on any mod classes.
 * Make sure to call {@link #setModID(String modID)} during the mod's init.
 * @author Hunternif
 */
public class Log {
	private static String modID;
	
	public static void setModID(String modID) {
		Log.modID = modID;
	}
	
	private static void log(Level level, Throwable ex, String msg, Object... data) {
		if (modID != null) {
			FMLLog.log(modID, level, ex, msg, data);
		} else {
			FMLLog.log(level, ex, msg, data);
		}
	}
	
	public static void debug(String msg, Object ... data) {
		log(Level.DEBUG, null, msg, data);
	}
	
	public static void info(String msg, Object ... data) {
		log(Level.INFO, null, msg, data);
	}
	
	public static void warn(String msg, Object ... data) {
		log(Level.WARN, null, msg, data);
	}
	public static void warn(Throwable ex, String msg, Object ... data) {
		log(Level.WARN, ex, msg, data);
	}
	
	public static void error(String msg, Object ... data) {
		log(Level.ERROR, null, msg, data);
	}
	public static void error(Throwable ex, String msg, Object ... data) {
		log(Level.ERROR, ex, msg, data);
	}
	
	public static void fatal(String msg, Object ... data) {
		log(Level.FATAL, null, msg, data);
	}
	public static void fatal(Throwable ex, String msg, Object ... data) {
		log(Level.FATAL, ex, msg, data);
	}
}
