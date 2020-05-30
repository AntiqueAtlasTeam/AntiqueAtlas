package hunternif.mc.atlas;

//import blue.endless.jankson.Comment;
//import io.github.cottonmc.cotton.config.ConfigManager;
//import io.github.cottonmc.cotton.config.annotations.ConfigFile;


import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SettingsConfig {

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    // CLIENT CONFIG
    public static boolean itemNeeded;
    public static boolean doScaleMarkers;

    public static double defaultScale;

    public static double minScale;

    public static double maxScale;

    public static boolean doReverseWheelZoom;

    // COMMON CONFIG
    public static boolean doSaveBrowsingPos;

    public static boolean autoDeathMarker;

    public static boolean autoVillageMarkers;

    public static boolean autoNetherPortalMarkers;

    public static int scanRadius;

    public static boolean forceChunkLoading;

    public static double newScanInterval;

    public static boolean doRescan;

    public static int rescanRate;

    public static int markerLimit;

    public static boolean doScanPonds;

    public static boolean doScanRavines;

    public static boolean debugRender;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();

        final Pair<CommonConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair2.getRight();
        COMMON = specPair2.getLeft();
        AntiqueAtlasMod.logger.info("Initialised config objects");
    }

    public static void bakeConfigClient() {
        doScaleMarkers = CLIENT.doScaleMarkers.get();
        defaultScale = CLIENT.defaultScale.get();
        minScale = CLIENT.minScale.get();
        maxScale = CLIENT.maxScale.get();
        doReverseWheelZoom = CLIENT.doReverseWheelZoom.get();
    }

    public static void bakeConfigCommon() {
        AntiqueAtlasMod.logger.info("Loaded config values");
        doSaveBrowsingPos = COMMON.doSaveBrowsingPos.get();
        autoDeathMarker = COMMON.autoDeathMarker.get();
        autoVillageMarkers = COMMON.autoVillageMarkers.get();
        autoNetherPortalMarkers = COMMON.autoNetherPortalMarkers.get();
        scanRadius = COMMON.scanRadius.get();
        forceChunkLoading = COMMON.forceChunkLoading.get();
        newScanInterval = COMMON.newScanInterval.get();
        doRescan = COMMON.doRescan.get();
        rescanRate = COMMON.rescanRate.get();
        markerLimit = COMMON.markerLimit.get();
        doScanPonds = COMMON.doScanPonds.get();
        doScanRavines = COMMON.doScanRavines.get();
        debugRender = COMMON.doScanRavines.get();
    }

    // Doesn't need to be an inner class
    public static class ClientConfig {
        public ForgeConfigSpec.BooleanValue doScaleMarkers;

        public ForgeConfigSpec.DoubleValue defaultScale;

        public ForgeConfigSpec.DoubleValue minScale;

        public ForgeConfigSpec.DoubleValue maxScale;

        public ForgeConfigSpec.BooleanValue doReverseWheelZoom;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("interface");
            doScaleMarkers = builder
                    .comment("This setting probably does something. Try it!")
                    .define("doScaleMarkers", false);
            defaultScale = builder
                    .comment("Default zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferably a power of 2.")
                    .defineInRange("defaultScale", 0.5f, 1.0 / 512.0, 16.0f);
            minScale = builder
                    .comment("Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferably a power of 2. Smaller values may decrease performance!")
                    .defineInRange("minScale", 1.0 / 32.0, 0.001953125f, 16.0f);
            maxScale = builder
                    .comment("Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferably a power of 2.")
                    .defineInRange("maxScale", 4, 1.0 / 512.0, 16.0f);
            doReverseWheelZoom = builder
                    .comment("If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.\\nIf true, then the direction is reversed.")
                    .define("doReverseWheelZoom", false);
            builder.pop();
        }

    }

    public static class CommonConfig {

        // GAMEPLAY
        public final ForgeConfigSpec.BooleanValue doSaveBrowsingPos;

        public final ForgeConfigSpec.BooleanValue autoDeathMarker;

        public final ForgeConfigSpec.BooleanValue autoVillageMarkers;

        public final ForgeConfigSpec.BooleanValue autoNetherPortalMarkers;

        //@Config.RequiresMcRestart
        public final ForgeConfigSpec.BooleanValue itemNeeded;

        // PERFORMANCE
        public final ForgeConfigSpec.IntValue scanRadius;

        public final ForgeConfigSpec.BooleanValue forceChunkLoading;

        public final ForgeConfigSpec.DoubleValue newScanInterval;

        public final ForgeConfigSpec.BooleanValue doRescan;

        public final ForgeConfigSpec.IntValue rescanRate;

        public final ForgeConfigSpec.IntValue markerLimit;

        public final ForgeConfigSpec.BooleanValue doScanPonds;

        public final ForgeConfigSpec.BooleanValue doScanRavines;

        //@Comment("If true, map render time will be output.")
        public final ForgeConfigSpec.BooleanValue debugRender;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("gameplay");
            doSaveBrowsingPos = builder
                    .comment("Whether to remember last open browsing position and zoom level for each dimension in every atlas.\\nIf disabled, all dimensions and all atlases will be \\\"synchronized\\\" at the same coordinates and zoom level, and map will \\\"follow\\\" player by default.")
                    .define("doSaveBrowsingPos", true);
            autoDeathMarker = builder
                    .comment("Whether to add local marker for the spot where the player died.")
                    .define("autoDeathMarker", true);
            autoVillageMarkers = builder
                    .comment("Whether to add global markers for NPC villages.")
                    .define("autoVillageMarkers", true);
            autoNetherPortalMarkers = builder
                    .comment("Whether to add global markers for Nether Portals.")
                    .define("autoNetherPortalMarkers", true);
            itemNeeded = builder
                    .comment("Player will need to craft atlas item to use atlas.")
                    .define("itemNeeded", true);
            builder.pop();

            builder.push("performance");
            scanRadius = builder
                    .comment("The radius of the area around the player which is scanned by the Atlas at regular intervals.\nNote that this will not force faraway chunks to load, unless force_chunk_loading is enabled.\nLower value gives better performance.")
                    .defineInRange("scanRadius", 11, 1, 64);
            forceChunkLoading = builder
                    .comment("Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.\nEnabling this may SEVERELY decrease performance!")
                    .define("forceChunkLoading", false);
            newScanInterval = builder
                    .comment("Time in seconds between two scans of the area.\\nHigher value gives better performance.")
                    .defineInRange("newScanInterval", 1f, 1.0 / 64.0, 64);
            doRescan = builder
                    .comment("Whether to rescan chunks in the area that have been previously mapped. This is useful in case of changes in coastline (including small ponds of water and lava), or if land disappears completely (for sky worlds).\nDisable for better performance.")
                    .define("doRescan", true);
            rescanRate = builder
                    .comment("The number of area scans between full rescans.\nHigher value gives better performance.")
                    .defineInRange("rescanRate", 4, 1, 1000);
            markerLimit = builder
                    .comment("The maximum number of markers a particular atlas can hold.")
                    .defineInRange("markerLimit", 1024, 0, 2147483647);
            doScanPonds = builder
                    .comment("Whether to perform additional scanning to locate small ponds of water or lava.\nDisable for better performance.")
                    .define("doScanPonds", true);
            doScanRavines = builder
                    .comment("Whether to perform additional scanning to locate ravines.\nDisable for better performance.")
                    .define("doScanRavines", true);
            debugRender = builder
                    .comment("If true, map render time will be output.")
                    .define("debugRender", false);

            builder.pop();
        }
    }
}
