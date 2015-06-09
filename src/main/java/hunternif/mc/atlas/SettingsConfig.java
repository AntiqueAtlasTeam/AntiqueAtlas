package hunternif.mc.atlas;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/** Config for various performance and interface settings. */
public class SettingsConfig {
	private static final int VERSION = 1;
	
	private static final String GAMEPLAY = "Gameplay";
	private static final String INTERFACE = "Interface";
	private static final String PERFORMANCE = "Performance";
	
	private File configFile;
	private Configuration config;
	
	//============ Interface settings =============
	public boolean doScaleMarkers = true;
	
	//=========== Performance settings ============
	public int scanRadius = 11;
	public boolean forceChunkLoading = false;
	public float newScanInterval = 1f;
	public boolean doRescan = true;
	public int rescanRate = 4;
	public boolean doScanPonds = true;
	
	public void load(File file) {
		Configuration config = new Configuration(file, String.valueOf(VERSION));
		config.setCategoryComment(GAMEPLAY,
				"These settings will affect how the mod behaves in certain situations and the players' overall gameplay,\n"
				+ "but generally won't affect performance.");
		config.setCategoryComment(INTERFACE,
				"These setting will affect the look and feel of the Atlas' interface.");
		config.setCategoryComment(PERFORMANCE,
				"These settings affect the algorithms for scanning the world, drawing the map etc. Changing them may\n"
				+ "improve the game's overall stability and performance at the cost of Atlas' functionality.");
		
		doScaleMarkers = config.getBoolean("do_scale_markers", INTERFACE, true, "Whether to change markers size depending on zoom level.");
		
		scanRadius = config.getInt("area_scan_radius", PERFORMANCE, scanRadius, 1, 256,
				"The radius of the area around the player which is scanned by the Atlas at regular intervals.\n"
				+ "Note that this will not force faraway chunks to load, unless force_chunk_loading is enabled.\n"
				+ "Lower value gives better performance.");
		forceChunkLoading = config.getBoolean("force_chunk_loading", PERFORMANCE, forceChunkLoading, 
				"Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.\n"
				+ "Enabling this may SEVERELY decrease performance!");
		newScanInterval = config.getFloat("area_scan_interval", PERFORMANCE, newScanInterval, 1f/20f, 3600, 
				"Time in seconds between two scans of the area.\nHigher value gives better performance.");
		doRescan = config.getBoolean("do_rescan", PERFORMANCE, doRescan, 
				"Whether to rescan chunks in the area that have been previously mapped. This is useful in case of\n"
				+ "changes in coastline (including small ponds of water and lava), or if land disappears completely\n"
				+ "(for sky worlds).\nDisable for better performance.");
		rescanRate = config.getInt("area_rescan_rate", PERFORMANCE, rescanRate, 1, 1000, 
				"The number of area scans between full rescans.\nHigher value gives better performance.");
		doScanPonds = config.getBoolean("do_scan_ponds", PERFORMANCE, doScanPonds,
				"Whether to perform additional scanning to locate small ponds of water or lava.\nDisable for better performance.");
		
		config.save();
	}
	
	public void save(File file) {
		if (configFile == null || !configFile.equals(file)) {
			load(file);
		}
		config.save();
	}
}
