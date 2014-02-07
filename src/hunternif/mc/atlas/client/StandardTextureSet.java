package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.Textures.*;
import net.minecraft.util.ResourceLocation;

public enum StandardTextureSet {
	WATER		(MAP_WATER),
	FROZEN_WATER(MAP_FROZEN_WATER),
	BEACH		(MAP_BEACH),
	SAND		(MAP_SAND, MAP_SAND2),
	PLAINS		(MAP_GRASS, MAP_GRASS2, MAP_GRASS3, MAP_GRASS4),
	SNOW		(MAP_SNOW, MAP_SNOW2),
	MOUNTAINS	(MAP_MOUNTAINS, MAP_MOUNTAINS2),
	HILLS		(MAP_HILLS),
	FOREST		(MAP_FOREST, MAP_FOREST2),
	FOREST_HILLS(MAP_FOREST_HILLS, MAP_FOREST_HILLS2),
	JUNGLE		(MAP_JUNGLE, MAP_JUNGLE2),
	JUNGLE_HILLS(MAP_JUNGLE_HILLS, MAP_JUNGLE_HILLS2),
	PINES		(MAP_PINES, MAP_PINES2, MAP_PINES3),
	PINES_HILLS	(MAP_PINES_HILLS2, MAP_PINES_HILLS3),
	SWAMP		(MAP_SWAMP, MAP_SWAMP, MAP_SWAMP, MAP_SWAMP2, MAP_SWAMP3, MAP_SWAMP4, MAP_SWAMP5, MAP_SWAMP6),
	MUSHROOM	(MAP_MUSHROOM, MAP_MUSHROOM2),
	HOUSE		(MAP_HOUSE),
	FENCE		(MAP_FENCE);
	
	public final ResourceLocation[] textures;
	
	StandardTextureSet(ResourceLocation ... textures) {
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
