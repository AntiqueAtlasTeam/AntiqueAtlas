package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.Textures.*;

public enum StandardTextureSet {
	WATER		(MAP_WATER), 
	BEACH		(MAP_BEACH), 
	SAND		(MAP_SAND), 
	PLAINS		(MAP_PLAINS), 
	MOUNTAINS	(MAP_MOUNTAINS, MAP_MOUNTAINS2),  
	HILLS		(MAP_HILLS), 
	FOREST		(MAP_FOREST, MAP_FOREST2), 
	FOREST_HILLS(MAP_FOREST_HILLS, MAP_FOREST_HILLS2), 
	JUNGLE		(MAP_JUNGLE, MAP_JUNGLE2), 
	JUNGLE_HILLS(MAP_JUNGLE_HILLS, MAP_JUNGLE_HILLS2), 
	PINES		(MAP_PINES, MAP_PINES2, MAP_PINES3), 
	PINES_HILLS	(MAP_PINES_HILLS, MAP_PINES_HILLS2, MAP_PINES_HILLS3), 
	SWAMP		(MAP_SWAMP, MAP_SWAMP, MAP_SWAMP, MAP_SWAMP2, MAP_SWAMP3, MAP_SWAMP4, MAP_SWAMP5, MAP_SWAMP6), 
	MUSHROOM	(MAP_MUSHROOM, MAP_MUSHROOM2);
	
	public final String[] textures;
	
	StandardTextureSet(String ... textures) {
		this.textures = textures;
	}
	
	public static boolean contains(String name) {
		for (StandardTextureSet entry : values()) {
			if (entry.name().equals(name)) {
				return true;
			}
		}
		return false;
	}
}
