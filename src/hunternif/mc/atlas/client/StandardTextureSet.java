package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.Textures.*;
import net.minecraft.util.ResourceLocation;

public enum StandardTextureSet {
	WATER		(TILE_WATER),
	FROZEN_WATER(TILE_FROZEN_WATER),
	BEACH		(TILE_BEACH),
	SAND		(TILE_SAND, TILE_SAND2),
	PLAINS		(TILE_GRASS, TILE_GRASS2, TILE_GRASS3, TILE_GRASS4),
	SNOW		(TILE_SNOW, TILE_SNOW2),
	MOUNTAINS	(TILE_MOUNTAINS, TILE_MOUNTAINS2),
	HILLS		(TILE_HILLS),
	FOREST		(TILE_FOREST, TILE_FOREST2),
	FOREST_HILLS(TILE_FOREST_HILLS, TILE_FOREST_HILLS2),
	JUNGLE		(TILE_JUNGLE, TILE_JUNGLE2),
	JUNGLE_HILLS(TILE_JUNGLE_HILLS, TILE_JUNGLE_HILLS2),
	PINES		(TILE_PINES, TILE_PINES2, TILE_PINES3),
	PINES_HILLS	(TILE_PINES_HILLS, TILE_PINES_HILLS2, TILE_PINES_HILLS3),
	SWAMP		(TILE_SWAMP, TILE_SWAMP, TILE_SWAMP, TILE_SWAMP2, TILE_SWAMP3, TILE_SWAMP4, TILE_SWAMP5, TILE_SWAMP6),
	MUSHROOM	(TILE_MUSHROOM, TILE_MUSHROOM2),
	HOUSE		(TILE_HOUSE),
	FENCE		(TILE_FENCE);
	
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
