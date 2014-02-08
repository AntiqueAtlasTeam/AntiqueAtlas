package hunternif.mc.atlas.api;

import hunternif.mc.atlas.api.impl.BiomeApiImpl;
import hunternif.mc.atlas.api.impl.TileApiImpl;

public class AtlasAPI {
	private static final BiomeAPI biomeApi = new BiomeApiImpl();
	private static final TileAPI tileApi = new TileApiImpl();
	
	/** API for custom biomes. Methods only for the client side. */
	public static BiomeAPI getBiomeAPI() {
		return biomeApi;
	}
	
	/** API for custom tiles, i.e. dungeons, towns etc. */
	public static TileAPI getTileAPI() {
		return tileApi;
	}
}
