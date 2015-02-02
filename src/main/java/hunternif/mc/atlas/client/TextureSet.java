package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.Textures.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public class TextureSet {
	public static final TextureSet TEST			= standard("TEST", TILE_TEST);
	public static final TextureSet ICE			= standard("ICE", TILE_ICE_NOBORDER);
	public static final TextureSet SHORE		= new TextureSetShore("SHORE", TILE_SHORE, TILE_SHORE2, TILE_SHORE3);
	public static final TextureSet ROCK_SHORE	= new TextureSetShore("ROCK_SHORE", TILE_ROCK_SHORE);
	public static final TextureSet SAND			= standard("SAND", TILE_SAND, TILE_SAND2);
	public static final TextureSet PLAINS		= standard("PLAINS", TILE_GRASS, TILE_GRASS2, TILE_GRASS3, TILE_GRASS4);
	public static final TextureSet SUNFLOWERS	= standard("SUNFLOWERS", TILE_SUNFLOWERS, TILE_SUNFLOWERS2, TILE_GRASS3, TILE_GRASS4);
	
	// Snowy stuff
	public static final TextureSet ICE_SPIKES	= standard("ICE_SPIKES", TILE_ICE_SPIKES, TILE_ICE_SPIKES2);
	public static final TextureSet SNOW_PINES	= standard("SNOW_PINES", TILE_SNOW_PINES, TILE_SNOW_PINES2, TILE_SNOW_PINES3);
	public static final TextureSet SNOW_PINES_HILLS = standard("SNOW_PINES_HILLS", TILE_SNOW_PINES_HILLS, TILE_SNOW_PINES_HILLS2, TILE_SNOW_PINES_HILLS3);
	public static final TextureSet SNOW_HILLS	= standard("SNOW_HILLS", TILE_SNOW_HILLS, TILE_SNOW_HILLS2);
	public static final TextureSet SNOW			= standard("SNOW", TILE_SNOW, TILE_SNOW, TILE_SNOW, TILE_SNOW, TILE_SNOW,
			TILE_SNOW1, TILE_SNOW1, TILE_SNOW1, TILE_SNOW2, TILE_SNOW2, TILE_SNOW2, TILE_SNOW3, TILE_SNOW4, TILE_SNOW5, TILE_SNOW6);
	
	// Mountain stuff
	public static final TextureSet MOUNTAINS_NAKED = standard("MOUNTAINS_NAKED", TILE_MOUNTAINS, TILE_MOUNTAINS2);
	/** Has a few trees on. */
	public static final TextureSet MOUNTAINS = standard("MOUNTAINS", TILE_MOUNTAINS, TILE_MOUNTAINS,
			TILE_MOUNTAINS2, TILE_MOUNTAINS2, TILE_MOUNTAINS3, TILE_MOUNTAINS4);
	public static final TextureSet MOUNTAINS_SNOW_CAPS = standard("MOUNTAINS_SNOW_CAPS", TILE_MOUNTAINS, TILE_SNOW_CAPS);
	/** Has naked mountains, a few trees, and snow caps. */
	public static final TextureSet MOUNTAINS_ALL = standard("MOUNTAINS_ALL", TILE_MOUNTAINS, TILE_MOUNTAINS2,
			TILE_MOUNTAINS3, TILE_MOUNTAINS4, TILE_SNOW_CAPS, TILE_SNOW_CAPS);
	
	public static final TextureSet HILLS		= standard("HILLS", TILE_HILLS);
	public static final TextureSet FOREST		= standard("FOREST", TILE_FOREST, TILE_FOREST2, TILE_FOREST3);
	public static final TextureSet FOREST_FLOWERS	= standard("FOREST_FLOWERS", TILE_FOREST_FLOWERS, TILE_FOREST_FLOWERS2, TILE_FOREST_FLOWERS3);
	public static final TextureSet DENSE_FOREST		= standard("DENSE_FOREST", TILE_DENSE_FOREST, TILE_DENSE_FOREST2);
	public static final TextureSet DENSE_FOREST_HILLS	= standard("DENSE_FOREST_HILLS", TILE_DENSE_FOREST_HILLS, TILE_DENSE_FOREST_HILLS2);
	public static final TextureSet JUNGLE		= standard("JUNGLE", TILE_JUNGLE, TILE_JUNGLE2);
	public static final TextureSet JUNGLE_HILLS = standard("JUNGLE_HILLS", TILE_JUNGLE_HILLS, TILE_JUNGLE_HILLS2);
	public static final TextureSet PINES		= standard("PINES", TILE_PINES, TILE_PINES2, TILE_PINES3);
	public static final TextureSet PINES_HILLS	= standard("PINES_HILLS", TILE_PINES_HILLS, TILE_PINES_HILLS2, TILE_PINES_HILLS3);
	
	public static final TextureSet MEGA_SPRUCE	= standard("MEGA_SPRUCE", TILE_MEGA_SPRUCE, TILE_MEGA_SPRUCE2);
	public static final TextureSet MEGA_SPRUCE_HILLS = standard("MEGA_SPRUCE_HILLS", TILE_MEGA_SPRUCE_HILLS, TILE_MEGA_SPRUCE_HILLS2);
	public static final TextureSet MEGA_TAIGA	= standard("MEGA_TAIGA", TILE_MEGA_TAIGA, TILE_MEGA_TAIGA2);
	public static final TextureSet MEGA_TAIGA_HILLS = standard("MEGA_TAIGA_HILLS", TILE_MEGA_TAIGA_HILLS, TILE_MEGA_TAIGA_HILLS2);
	
	public static final TextureSet SWAMP		= standard("SWAMP", TILE_SWAMP, TILE_SWAMP, TILE_SWAMP, TILE_SWAMP2, TILE_SWAMP3, TILE_SWAMP4, TILE_SWAMP5, TILE_SWAMP6);
	public static final TextureSet MUSHROOM		= standard("MUSHROOM", TILE_MUSHROOM, TILE_MUSHROOM2);
	public static final TextureSet WATER		= standard("WATER", TILE_WATER, TILE_WATER2);
	public static final TextureSet HOUSE		= standard("HOUSE", TILE_HOUSE);
	public static final TextureSet FENCE		= standard("FENCE", TILE_FENCE).stitchTo(HOUSE);
	
	// Sophisticated stitching stuff:
	static {
		stitchMutually(PLAINS, SUNFLOWERS);
		WATER.stitchTo(SHORE, ROCK_SHORE, SWAMP);
		SNOW.stitchTo(SNOW_PINES, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
		SNOW_PINES.stitchTo(SNOW, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
		stitchMutually(MOUNTAINS, MOUNTAINS_NAKED, MOUNTAINS_SNOW_CAPS, MOUNTAINS_ALL);
	}
	
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
	
	/** Stitch provided texture sets mutually between each other. */
	public static final void stitchMutually(TextureSet ... sets) {
		for (TextureSet set1 : sets) {
			for (TextureSet set2 : sets) {
				if (set1 != set2) set1.stitchTo.add(set2);
			}
		}
	}
}
