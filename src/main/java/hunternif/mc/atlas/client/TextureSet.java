package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.Textures.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public class TextureSet {
	public static final TextureSet TEST			= standard("TEST", TILE_TEST);
	public static final TextureSet ICE			= standard("ICE", TILE_ICE_NOBORDER); // previously FROZEN_WATER
	public static final TextureSet SHORE		= new TextureSetShore("SHORE", TILE_SHORE, TILE_SHORE2, TILE_SHORE3); // previously BEACH
	public static final TextureSet SAND			= standard("SAND", TILE_SAND, TILE_SAND2);
	public static final TextureSet PLAINS		= standard("PLAINS", TILE_GRASS, TILE_GRASS2, TILE_GRASS3, TILE_GRASS4);
	public static final TextureSet ICE_SPIKES	= standard("ICE_SPIKES", TILE_ICE_SPIKES, TILE_ICE_SPIKES2);
	public static final TextureSet SNOW			= standard("SNOW", TILE_SNOW, TILE_SNOW, TILE_SNOW, TILE_SNOW, TILE_SNOW,
			TILE_SNOW1, TILE_SNOW1, TILE_SNOW1, TILE_SNOW2, TILE_SNOW2, TILE_SNOW2, TILE_SNOW3, TILE_SNOW4, TILE_SNOW5, TILE_SNOW6).stitchTo(ICE_SPIKES);
	public static final TextureSet MOUNTAINS	= standard("MOUNTAINS", TILE_MOUNTAINS, TILE_MOUNTAINS2);
	public static final TextureSet HILLS		= standard("HILLS", TILE_HILLS);
	public static final TextureSet FOREST		= standard("FOREST", TILE_FOREST, TILE_FOREST2);
	public static final TextureSet FOREST_HILLS	= standard("FOREST_HILLS", TILE_FOREST_HILLS, TILE_FOREST_HILLS2);
	public static final TextureSet JUNGLE		= standard("JUNGLE", TILE_JUNGLE, TILE_JUNGLE2);
	public static final TextureSet JUNGLE_HILLS = standard("JUNGLE_HILLS", TILE_JUNGLE_HILLS, TILE_JUNGLE_HILLS2);
	public static final TextureSet PINES		= standard("PINES", TILE_PINES, TILE_PINES2, TILE_PINES3);
	public static final TextureSet PINES_HILLS	= standard("PINES_HILLS", TILE_PINES_HILLS, TILE_PINES_HILLS2, TILE_PINES_HILLS3);
	public static final TextureSet SNOW_PINES		= standard("SNOW_PINES", TILE_SNOW_PINES, TILE_SNOW_PINES2, TILE_SNOW_PINES3).stitchToMutual(SNOW);
	public static final TextureSet SWAMP		= standard("SWAMP", TILE_SWAMP, TILE_SWAMP, TILE_SWAMP, TILE_SWAMP2, TILE_SWAMP3, TILE_SWAMP4, TILE_SWAMP5, TILE_SWAMP6);
	public static final TextureSet MUSHROOM		= standard("MUSHROOM", TILE_MUSHROOM, TILE_MUSHROOM2);
	public static final TextureSet WATER		= standard("WATER", TILE_WATER, TILE_WATER2).stitchTo(SHORE, SWAMP);
	public static final TextureSet HOUSE		= standard("HOUSE", TILE_HOUSE);
	public static final TextureSet FENCE		= standard("FENCE", TILE_FENCE).stitchTo(HOUSE);
	
	/** Name of the texture pack to write in the config file. */
	public final String name; 
	
	/** The actual textures in this set. */
	public final ResourceLocation[] textures;
	
	/** Texture sets that a tile rendered with this set can be stitched to,
	 * excluding itself. */
	private final Set<TextureSet> stitchTo = new HashSet<TextureSet>();
	
	/** Whether the texture set is part of the standard pack. Only true for
	 * static constants in this class. */
	final boolean isStandard;
	
	private static TextureSet standard(String name, ResourceLocation ... textures) {
		return new TextureSet(true, name, textures);
	}
	
	private TextureSet(boolean isStandard, String name, ResourceLocation ... textures) {
		this.isStandard = isStandard;
		this.name = name;
		this.textures = textures;
	}
	
	public TextureSet(String name, ResourceLocation ... textures) {
		this(false, name, textures);
	}
	
	/** Add other texture sets that this texture set will be stitched to
	 * (but the opposite may be false, in case of asymmetric stitching.) */
	public TextureSet stitchTo(TextureSet ... textureSets) {
		for (TextureSet textureSet : textureSets) {
			stitchTo.add(textureSet);
		}
		return this;
	}
	/** Same as {@link #stitchTo()}, but symmetrical. */
	public TextureSet stitchToMutual(TextureSet ... textureSets) {
		for (TextureSet textureSet : textureSets) {
			stitchTo.add(textureSet);
			textureSet.stitchTo.add(this);
		}
		return this;
	}
	
	/** Whether this texture set should be stitched to the other specified set. */
	public boolean shouldStichTo(TextureSet otherSet) {
		return otherSet == this || stitchTo.contains(otherSet);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TextureSet)) {
			return false;
		}
		TextureSet set = (TextureSet) obj;
		return this.name.equals(set.name) && Arrays.equals(this.textures, set.textures);
	}
	
	/** A special texture set that is stitched to everything except water. */
	private static class TextureSetShore extends TextureSet {
		public TextureSetShore(String name, ResourceLocation ... textures) {
			super(true, name, textures);
		}
		@Override
		public boolean shouldStichTo(TextureSet otherSet) {
			return otherSet == this || !WATER.shouldStichTo(otherSet);
		}
	}
}
