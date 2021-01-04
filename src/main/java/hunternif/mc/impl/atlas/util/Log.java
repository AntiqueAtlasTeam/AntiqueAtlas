package hunternif.mc.impl.atlas.util;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple logging helper that is not directly dependent on any mod classes.
 *
 * @author Hunternif
 */
public class Log {
    private static final Logger LOGGER = LogManager.getLogger(AntiqueAtlasMod.ID);

    private static void log(Level level, Throwable ex, String msg, Object... data) {
        LOGGER.log(level, () -> data == null ? msg : String.format(msg, data), ex);
    }

    public static void debug(String msg, Object... data) {
        log(Level.DEBUG, null, msg, data);
    }

    public static void info(String msg, Object... data) {
        log(Level.INFO, null, msg, data);
    }

    public static void warn(String msg, Object... data) {
        log(Level.WARN, null, msg, data);
    }

    public static void warn(Throwable ex, String msg, Object... data) {
        log(Level.WARN, ex, msg, data);
    }

    public static void error(String msg, Object... data) {
        log(Level.ERROR, null, msg, data);
    }

    public static void error(Throwable ex, String msg, Object... data) {
        log(Level.ERROR, ex, msg, data);
    }

    public static void fatal(String msg, Object... data) {
        log(Level.FATAL, null, msg, data);
    }

    public static void fatal(Throwable ex, String msg, Object... data) {
        log(Level.FATAL, ex, msg, data);
    }
}
