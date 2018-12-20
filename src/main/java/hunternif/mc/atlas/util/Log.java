package hunternif.mc.atlas.util;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * The Logger is provided by Forge during the PreInit event {@link FMLPreInitializationEvent#getModLog()}
 */
public class Log {
	private static Logger modLogger;

	public static void setModLogger(Logger modLogger) {
		Log.modLogger = modLogger;
	}

	private static void log(Level level, Throwable ex, String msg, Object... data) {
		if (data != null)
			msg = String.format(msg, data);

		if (ex != null) {
			modLogger.log(level, msg, ex);
		} else {
			modLogger.log(level, msg);
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
