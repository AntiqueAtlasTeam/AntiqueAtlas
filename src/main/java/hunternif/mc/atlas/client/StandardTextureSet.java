package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.Textures.*;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.ResourceLocation;

public enum StandardTextureSet {
	// TODO Make it possible to register custom texture sets.
	// That would probably be part of the solution to GitHub issue #16.
	
	WATER		(TILE_WATER, TILE_WATER2),
	FROZEN_WATER(TILE_ICE_NOBORDER),
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
	FENCE		(TILE_FENCE),
	VILLAGE_FENCE(FENCE, HOUSE);
	
	/** The actual textures in this set. */
	public final ResourceLocation[] textures;
	
	/** Texture sets that a tile rendered with this set can be stitched to,
	 * including itself (usually). */
	public final Set<StandardTextureSet> stitchTo;
	
	/** Creates a texture set that also can be stitched to another set. */
	StandardTextureSet(StandardTextureSet original, StandardTextureSet ... stitchTo) {
		//TODO make this constructor not dependable on the "original" texture set.
		this.textures = original.textures;
		ImmutableSet.Builder<StandardTextureSet> builder = ImmutableSet.builder();
		builder.add(this);
		builder.add(original);
		builder.add(stitchTo);
		this.stitchTo = builder.build();
	}
	
	StandardTextureSet(ResourceLocation ... textures) {
		this.textures = textures;
		this.stitchTo = ImmutableSet.of(this);
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
