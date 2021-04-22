package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.minecraftforge.common.ForgeConfigSpec;

public class AntiqueAtlasConfig {
	//============ Gameplay settings ==============
    public static ForgeConfigSpec.BooleanValue doSaveBrowsingPos;

    public static ForgeConfigSpec.BooleanValue autoDeathMarker;

    public static ForgeConfigSpec.BooleanValue autoVillageMarkers;

    public static ForgeConfigSpec.BooleanValue autoNetherPortalMarkers;

    public static ForgeConfigSpec.BooleanValue itemNeeded;

    //============ Interface settings =============
    public static ForgeConfigSpec.BooleanValue doScaleMarkers;

    public static ForgeConfigSpec.DoubleValue defaultScale;

    public static ForgeConfigSpec.DoubleValue minScale;

    public static ForgeConfigSpec.DoubleValue maxScale;

    public static ForgeConfigSpec.BooleanValue doReverseWheelZoom;

    //=========== Performance settings ============
    public static ForgeConfigSpec.IntValue scanRadius;

    public static ForgeConfigSpec.BooleanValue forceChunkLoading;

    public static ForgeConfigSpec.DoubleValue newScanInterval; //Was A float

    public static ForgeConfigSpec.BooleanValue doRescan;

    public static ForgeConfigSpec.IntValue rescanRate;

    public static ForgeConfigSpec.IntValue markerLimit;

    public static ForgeConfigSpec.BooleanValue doScanPonds;

    public static ForgeConfigSpec.BooleanValue doScanRavines;

    public static ForgeConfigSpec.BooleanValue debugRender;

//    //=========== Overlay settings ============
//    public static ForgeConfigSpec.BooleanValue alignRight;
//
//    public static ForgeConfigSpec.BooleanValue alignBottom;
//
//    public static ForgeConfigSpec.IntValue xPosition;
//
//    public static ForgeConfigSpec.IntValue yPosition;
//
//    public static ForgeConfigSpec.IntValue width;
//
//    public static ForgeConfigSpec.IntValue height;

    public static ForgeConfigSpec.IntValue tileSize;

    public static ForgeConfigSpec.IntValue markerSize;

    public static ForgeConfigSpec.IntValue playerIconWidth;

    public static ForgeConfigSpec.IntValue playerIconHeight;

    public static ForgeConfigSpec.DoubleValue borderX; //Was A float

    public static ForgeConfigSpec.DoubleValue borderY; //Was A float

    public static ForgeConfigSpec.BooleanValue requiresHold;

    public static ForgeConfigSpec.BooleanValue enabled;
	
	public static int MAX = 2147483647;
	public static void init(ForgeConfigSpec.Builder client, ForgeConfigSpec.Builder common) {
		String category = "gameplay.";
		
		doSaveBrowsingPos = client
				.comment("Whether to remember last open browsing position and zoom level for each dimension in every atlas.\nIf disabled, all dimensions and all atlases will be \"synchronized\" at the same coordinates and zoom level, and map will \"follow\" player by default.")
				.define(category+"doSaveBrowsingPos", true);
		
		autoDeathMarker = client
				.comment("Whether to add local marker for the spot where the player died.")
				.define(category+"autoDeathMarker", true);
		
		autoVillageMarkers = client
				.comment("Whether to add global markers for NPC villages.")
				.define(category+"autoVillageMarkers", true);
		
		autoNetherPortalMarkers = client
				.comment("Whether to add global markers for Nether Portals.")
				.define(category+"autoNetherPortalMarkers", true);
		
		itemNeeded = client
				.comment("Player will need to craft atlas item to use atlas.")
				.define(category+"itemNeeded", true);
		
		category = "userInterface.";
		
		doScaleMarkers = client
				.define(category+"doReverseWheelZoom", false);
		
		defaultScale = client
				.comment("Default zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.")
				.defineInRange(category+"defaultScale", 0.5f, 0.001953125, 16.0);
		
		minScale = client
				.comment("Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2. Smaller values may decrease performance!")
				.defineInRange(category+"minScale", 1.0 / 32.0, 0.001953125, 16.0);
		
		maxScale = client
				.comment("Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.")
				.defineInRange(category+"maxScale", 4, 0.001953125, 16.0);
		
		doReverseWheelZoom = client
				.comment("If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.\nIf true, then the direction is reversed.")
				.define(category+"doReverseWheelZoom", false);
		
		category = "performance.";
		
		scanRadius = common
				.comment("The radius of the area around the player which is scanned by the Atlas at regular intervals.\nNote that this will not force faraway chunks to load, unless force_chunk_loading is enabled.\nLower value gives better performance.")
				.defineInRange(category+"scanRadius", 11, 0, MAX);
		
		forceChunkLoading = common
				.comment("Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.\nEnabling this may SEVERELY decrease performance!")
				.define(category+"forceChunkLoading", false);
		
		newScanInterval = common
				.comment("Time in seconds between two scans of the area.\nHigher value gives better performance.")
				.defineInRange(category+"newScanInterval", 1f, 0.0f, MAX);
		
		doRescan = common
				.comment("Whether to rescan chunks in the area that have been previously mapped. This is useful in case of changes in coastline (including small ponds of water and lava), or if land disappears completely (for sky worlds).\nDisable for better performance.")
				.define(category+"doRescan", true);
		
		rescanRate = common
				.comment("The number of area scans between full rescans.\nHigher value gives better performance.")
				.defineInRange(category+"rescanRate", 4, 1, 1000);
		
		markerLimit = common
				.comment("The maximum number of markers a particular atlas can hold.")
				.defineInRange(category+"markerLimit", 1024, 0, 2147483647);
		
		doScanPonds = common
				.comment("Whether to perform additional scanning to locate small ponds of water or lava.\nDisable for better performance.")
				.define(category+"doScanPonds", true);
		
		doScanRavines = common
				.comment("Whether to perform additional scanning to locate ravines.\nDisable for better performance.")
				.define(category+"doScanRavines", true);
		
		debugRender = common
				.comment("If true, map render time will be output.")
				.define(category+"debugRender", false);
		
//		category = "overlayPosition.";
//		
//		alignRight = client
//				.comment("If true, the map position's x axis will align 0 to the right\nof the screen, increasing towards the left.")
//				.define(category+"alignRight", false);
//		
//		alignBottom = client
//				.comment("If true, the map position's y axis will align 0 to the bottom\nof the screen, increasing towards the top.")
//				.define(category+"alignBottom", false);
//		
//		xPosition = client
//				.comment("Map's minimum position along the x axis in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
//				.defineInRange(category+"xPosition", 2, 0, MAX);
//		
//		yPosition = client
//				.comment("Map's minimum position along the y axis in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
//				.defineInRange(category+"yPosition", 2, 0, MAX);
//		
//		width = client
//				.comment("Map's width in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
//				.defineInRange(category+"width", GuiAtlas.WIDTH / 2, 0, MAX);
//		
//		height = client
//				.comment("Map's height in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
//				.defineInRange(category+"height", GuiAtlas.HEIGHT / 2, 0, MAX);
		

		category = "appearance.";
		
		tileSize = client
				.comment("The size (in GUI pixels) of a map's tile.\nNote that this will change with Minecraft's GUI scale configuration.\nWhen using a small gui scale, the map may look better with a TILE_SIZE of 16 or more.")
				.defineInRange(category+"tileSize", 8, 1, 10);
		
		markerSize = client
				.comment("The size (in GUI pixels) of a marker on the map.\nNote that this will change with Minecraft's GUI scale configuration.")
				.defineInRange(category+"markerSize", GuiAtlas.MARKER_SIZE / 2, 0, MAX);
		
		playerIconWidth = client
				.comment("The width (in GUI pixels) of the player's icon.")
				.defineInRange(category+"playerIconWidth", 14, 0, MAX);
		
		playerIconHeight = client
				.comment("The height (in GUI pixels) of the player's icon.")
				.defineInRange(category+"playerIconHeight", 16, 0, MAX);
		
		borderX = client
				.comment("The width of the map border on the left and right sides of the minimap tiles.\nRepresented as a fraction of the image width.\nBelow a certain threshold, this border will be overtaken by the map border graphic.")
				.defineInRange(category+"borderX", 0.05, 0.0, 0.5);
		
		borderY = client
				.comment("The width of the map border on the top and bottom sides of the minimap tiles.\nRepresented as a fraction of the image width.\nBelow a certain threshold, this border will be overtaken by the map border graphic.")
				.defineInRange(category+"borderY", 0.05, 0.0, 0.5);
		
		requiresHold = client
				.comment("If true, the minimap will show the map of an atlas only while it is held.\nIf false, the minimap will show the map of the first atlas in the hotbar.")
				.define(category+"requiresHold", true);
		
		enabled = client
				.comment("Set true to enable minimap")
				.define(category+"enabled", false);

	}


}
