package hunternif.mc.atlas;

import blue.endless.jankson.Comment;
import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.cotton.config.annotations.ConfigFile;

public class SettingsConfig {

    public static Gameplay gameplay = new Gameplay();
    public static Interface userInterface = new Interface(); // Added a "_" because "interface" is reserved :(
    public static Performance performance = new Performance();

    static void loadConfig()
    {
        gameplay = ConfigManager.loadConfig(Gameplay.class);
        userInterface = ConfigManager.loadConfig(Interface.class);
        performance = ConfigManager.loadConfig(Performance.class);
    }

    //============= Gameplay settings =============
    @ConfigFile(name="antiqueatlas/gameplay")
    public static class Gameplay {
        @Comment(value="Whether to remember last open browsing position and zoom level for each dimension in every atlas.\nIf disabled, all dimensions and all atlases will be \"synchronized\" at the same coordinates and zoom level, and map will \"follow\" player by default.")
        public boolean doSaveBrowsingPos = true;

        @Comment(value="Whether to add local marker for the spot where the player died.")
        public boolean autoDeathMarker = true;

        @Comment(value="Whether to add global markers for NPC villages.")
        public boolean autoVillageMarkers = true;

        @Comment(value="Whether to add global markers for Nether Portals.")
        public boolean autoNetherPortalMarkers = true;

        @Comment(value="Player will need to craft atlas item to use atlas.")
        //@Config.RequiresMcRestart
        public boolean itemNeeded = true;
    }

    //============ Interface settings =============
    @ConfigFile(name="antiqueatlas/interface")
    public static class Interface {
        public boolean doScaleMarkers = false;

        @Comment("Default zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.")
        //@Config.RangeDouble(min = 0.001953125f, max = 16.0f)
        public double defaultScale = 0.5f;

        @Comment("Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2. Smaller values may decrease performance!")
        //@Config.RangeDouble(min = 0.001953125f, max = 16.0f)
        public double minScale = 1.0 / 32.0;

        @Comment("Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.")
        //@Config.RangeDouble(min = 0.001953125f, max = 16.0f)
        public double maxScale = 4;

        @Comment("If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.\nIf true, then the direction is reversed.")
        public boolean doReverseWheelZoom = false;
    }

    //=========== Performance settings ============
    @ConfigFile(name="antiqueatlas/performance")
    public static class Performance {
        @Comment("The radius of the area around the player which is scanned by the Atlas at regular intervals.\nNote that this will not force faraway chunks to load, unless force_chunk_loading is enabled.\nLower value gives better performance.")
        public int scanRadius = 11;

        @Comment("Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.\nEnabling this may SEVERELY decrease performance!")
        public boolean forceChunkLoading = false;

        @Comment("Time in seconds between two scans of the area.\nHigher value gives better performance.")
        public float newScanInterval = 1f;

        @Comment("Whether to rescan chunks in the area that have been previously mapped. This is useful in case of changes in coastline (including small ponds of water and lava), or if land disappears completely (for sky worlds).\nDisable for better performance.")
        public boolean doRescan = true;

        @Comment("The number of area scans between full rescans.\nHigher value gives better performance.")
        //@Config.RangeInt(min = 1, max = 1000)
        public int rescanRate = 4;

        @Comment("The maximum number of markers a particular atlas can hold.")
        //@Config.RangeInt(min = 0, max = 2147483647)
        public int markerLimit = 1024;

        @Comment("Whether to perform additional scanning to locate small ponds of water or lava.\nDisable for better performance.")
        public boolean doScanPonds = true;

        @Comment("Whether to perform additional scanning to locate ravines.\nDisable for better performance.")
        public boolean doScanRavines = true;

        @Comment("If true, map render time will be output.")
        public boolean debugRender = false;
    }
}