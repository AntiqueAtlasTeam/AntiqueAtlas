package hunternif.mc.atlas.api;

import hunternif.mc.atlas.api.impl.BiomeApiImpl;
import hunternif.mc.atlas.api.impl.MarkerApiImpl;
import hunternif.mc.atlas.api.impl.TileApiImpl;

/**
 * Use this class to obtain a reference to the APIs.
 * @author Hunternif
 */
public class AtlasAPI {
	private static final int VERSION = 2;
	private static final BiomeAPI biomeApi = new BiomeApiImpl();
	private static final TileAPI tileApi = new TileApiImpl();
	private static final MarkerAPI markerApi = new MarkerApiImpl();
	
	/** Version of the API, meaning only this particular class. You might
	 * want to check static field VERSION in the specific API interfaces. */
	public static int getVersion() {
		return VERSION;
	}
	
	/** API for custom biomes. Methods only for the client side. */
	public static BiomeAPI getBiomeAPI() {
		return biomeApi;
	}
	
	/** API for custom tiles, i.e. dungeons, towns etc. */
	public static TileAPI getTileAPI() {
		return tileApi;
	}
	
	/** API for custom markers. */
	public static MarkerAPI getMarkerAPI() {
		return markerApi;
	}
}
