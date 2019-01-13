package hunternif.mc.atlas;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = AntiqueAtlasMod.ID, name = "antiqueatlas/antiqueatlas", category = "")
@Mod.EventBusSubscriber(modid = AntiqueAtlasMod.ID)
public class SettingsConfig {
    @Config.Comment({"These settings will affect how the mod behaves in certain situations and the players' overall gameplay, but generally won't affect performance."})
    public static Gameplay gameplay = new Gameplay();
    @Config.Comment({"These setting will affect the look and feel of the Atlas' interface."})
    public static Interface userInterface = new Interface(); // Added a "_" because "interface" is reserved :(
    @Config.Comment({"These settings affect the algorithms for scanning the world, drawing the map etc. Changing them may improve the game's overall stability and performance at the cost of Atlas' functionality."})
    public static Performance performance = new Performance();


    //============= Gameplay settings =============
    public static class Gameplay {
        @Config.Comment({"Whether to remember last open browsing position and zoom level for each dimension in every atlas.", "If disabled, all dimensions and all atlases will be \"synchronized\" at the same coordinates and zoom level, and map will \"follow\" player by default."})
        public boolean doSaveBrowsingPos = true;
        @Config.Comment({"Whether to add local marker for the spot where the player died."})
        public boolean autoDeathMarker = true;
        @Config.Comment({"Whether to add global markers for NPC villages."})
        public boolean autoVillageMarkers = true;
        @Config.Comment({"Whether to add global markers for Nether Portals."})
        public boolean autoNetherPortalMarkers = true;
        @Config.Comment({"Player will need to craft atlas item to use atlas."})
        @Config.RequiresMcRestart
        public boolean itemNeeded = true;
    }

    //============ Interface settings =============
    public static class Interface {
        public boolean doScaleMarkers = false;
        @Config.Comment({"Default zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2."})
        @Config.RangeDouble(min = 0.001953125f, max = 16.0f)
        public double defaultScale = 0.5f;
        @Config.Comment({"Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2. Smaller values may decrease performance!"})
        @Config.RangeDouble(min = 0.001953125f, max = 16.0f)
        public double minScale = 1.0 / 32.0;
        @Config.Comment({"Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2."})
        @Config.RangeDouble(min = 0.001953125f, max = 16.0f)
        public double maxScale = 4;
        @Config.Comment({"If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.", "If true, then the direction is reversed."})
        public boolean doReverseWheelZoom = false;
    }

    //=========== Performance settings ============
    public static class Performance {
        @Config.Comment({"The radius of the area around the player which is scanned by the Atlas at regular intervals.", "Note that this will not force faraway chunks to load, unless force_chunk_loading is enabled.", "Lower value gives better performance."})
        public int scanRadius = 11;
        @Config.Comment({"Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.", "Enabling this may SEVERELY decrease performance!"})
        public boolean forceChunkLoading = false;
        @Config.Comment({"Time in seconds between two scans of the area.", "Higher value gives better performance."})
        public float newScanInterval = 1f;
        @Config.Comment({"Whether to rescan chunks in the area that have been previously mapped. This is useful in case of changes in coastline (including small ponds of water and lava), or if land disappears completely (for sky worlds).", "Disable for better performance."})
        public boolean doRescan = true;
        @Config.Comment({"The number of area scans between full rescans.", "Higher value gives better performance."})
        @Config.RangeInt(min = 1, max = 1000)
        public int rescanRate = 4;
        @Config.Comment({"The maximum number of markers a particular atlas can hold."})
        @Config.RangeInt(min = 0, max = 2147483647)
        public int markerLimit = 1024;
        @Config.Comment({"Whether to perform additional scanning to locate small ponds of water or lava.", "Disable for better performance."})
        public boolean doScanPonds = true;
        @Config.Comment({"Whether to perform additional scanning to locate ravines.", "Disable for better performance."})
        public boolean doScanRavines = true;
        @Config.Comment({"If true, map render time will be output."})
        public boolean debugRender = false;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(AntiqueAtlasMod.ID))
            ConfigManager.sync(AntiqueAtlasMod.ID, Config.Type.INSTANCE);
    }
}