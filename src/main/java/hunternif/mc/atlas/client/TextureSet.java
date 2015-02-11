package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.Textures.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

public class TextureSet {
	public static final TextureSet
	TEST        = standard("TEST", TILE_TEST),
	
	// Plains & wasteland stuff:
	ICE         = standard("ICE", TILE_ICE_NOBORDER),
	DESERT      = standard("DESERT",
					TILE_SAND, TILE_SAND,
					TILE_SAND2, TILE_SAND2,
					TILE_SAND3, TILE_SAND3,
					TILE_SAND_BUSHES, TILE_SAND_BUSHES,
					TILE_CACTI),
	HILLS        = standard("HILLS", TILE_HILLS),
	DESERT_HILLS = standard("DESERT_HILLS",
					TILE_HILLS, TILE_HILLS, TILE_HILLS,
					TILE_HILLS_BUSHES,
					TILE_HILLS_CACTI),
	PLAINS      = standard("PLAINS", TILE_GRASS, TILE_GRASS2, TILE_GRASS3, TILE_GRASS4),
	SUNFLOWERS  = standard("SUNFLOWERS", TILE_SUNFLOWERS, TILE_SUNFLOWERS2, TILE_GRASS3, TILE_GRASS4),
	
	// Snowy stuff:
	ICE_SPIKES       = standard("ICE_SPIKES", TILE_ICE_SPIKES, TILE_ICE_SPIKES2),
	SNOW_PINES       = standard("SNOW_PINES", TILE_SNOW_PINES, TILE_SNOW_PINES2, TILE_SNOW_PINES3),
	SNOW_PINES_HILLS = standard("SNOW_PINES_HILLS", TILE_SNOW_PINES_HILLS, TILE_SNOW_PINES_HILLS2, TILE_SNOW_PINES_HILLS3),
	SNOW_HILLS       = standard("SNOW_HILLS", TILE_SNOW_HILLS, TILE_SNOW_HILLS2),
	SNOW             = standard("SNOW", // you know nothing.
						TILE_SNOW, TILE_SNOW, TILE_SNOW, TILE_SNOW, TILE_SNOW,
						TILE_SNOW1, TILE_SNOW1, TILE_SNOW1,
						TILE_SNOW2, TILE_SNOW2, TILE_SNOW2,
						TILE_SNOW3,
						TILE_SNOW4,
						TILE_SNOW5,
						TILE_SNOW6),
	
	// Mountain stuff:
	MOUNTAINS_NAKED     = standard("MOUNTAINS_NAKED", TILE_MOUNTAINS, TILE_MOUNTAINS2),
	MOUNTAINS           = standard("MOUNTAINS", // Has a few trees on top.
							TILE_MOUNTAINS, TILE_MOUNTAINS,
							TILE_MOUNTAINS2, TILE_MOUNTAINS2,
							TILE_MOUNTAINS3,
							TILE_MOUNTAINS4),
	MOUNTAINS_SNOW_CAPS = standard("MOUNTAINS_SNOW_CAPS", TILE_MOUNTAINS, TILE_SNOW_CAPS),
	MOUNTAINS_ALL       = standard("MOUNTAINS_ALL", // Has everything: naked mountains, trees on top and snow caps
							TILE_SNOW_CAPS, TILE_SNOW_CAPS,
							TILE_MOUNTAINS,
							TILE_MOUNTAINS2,
							TILE_MOUNTAINS3,
							TILE_MOUNTAINS4),
	
	// Oak forest stuff:
	FOREST             = standard("FOREST", TILE_FOREST, TILE_FOREST2, TILE_FOREST3),
	FOREST_HILLS       = standard("FOREST_HILLS", TILE_FOREST_HILLS, TILE_FOREST_HILLS2, TILE_FOREST_HILLS3),
	FOREST_FLOWERS     = standard("FOREST_FLOWERS", TILE_FOREST_FLOWERS, TILE_FOREST_FLOWERS2, TILE_FOREST_FLOWERS3),
	DENSE_FOREST       = standard("DENSE_FOREST", TILE_DENSE_FOREST, TILE_DENSE_FOREST2),
	DENSE_FOREST_HILLS = standard("DENSE_FOREST_HILLS", TILE_DENSE_FOREST_HILLS, TILE_DENSE_FOREST_HILLS2),
	
	// Birch stuff:
	BIRCH            = standard("BIRCH", TILE_BIRCH, TILE_BIRCH2),
	BIRCH_HILLS      = standard("BIRCH_HILLS", TILE_BIRCH_HILLS, TILE_BIRCH_HILLS2),
	TALL_BIRCH       = standard("TALL_BIRCH", TILE_TALL_BIRCH, TILE_TALL_BIRCH2),
	TALL_BIRCH_HILLS = standard("TALL_BIRCH_HILLS", TILE_TALL_BIRCH_HILLS, TILE_TALL_BIRCH_HILLS2),
	DENSE_BIRCH      = standard("DENSE_BIRCH", TILE_DENSE_BIRCH),
	
	// Jungle stuff:
	JUNGLE            = standard("JUNGLE", TILE_JUNGLE, TILE_JUNGLE2),
	JUNGLE_HILLS      = standard("JUNGLE_HILLS", TILE_JUNGLE_HILLS, TILE_JUNGLE_HILLS2),
	JUNGLE_CLIFFS     = standard("JUNGLE_CLIFFS",
						TILE_JUNGLE_CLIFFS,	TILE_JUNGLE_CLIFFS2,
						TILE_BUSHES_CLIFFS),
	JUNGLE_EDGE       = standard("JUNGLE_EDGE",
						TILE_JUNGLE_EDGE, TILE_JUNGLE_EDGE2, TILE_JUNGLE_EDGE3,
						TILE_GRASS2, TILE_GRASS3, TILE_GRASS4),
	JUNGLE_EDGE_HILLS = standard("JUNGLE_EDGE_HILLS",
						TILE_JUNGLE_EDGE_HILLS, TILE_JUNGLE_EDGE_HILLS2, TILE_JUNGLE_EDGE_HILLS3,
						TILE_HILLS_GRASS, TILE_HILLS_GRASS),
	
	// Coniferous stuff:
	PINES             = standard("PINES", TILE_PINES, TILE_PINES2, TILE_PINES3),
	PINES_HILLS       = standard("PINES_HILLS", TILE_PINES_HILLS, TILE_PINES_HILLS2, TILE_PINES_HILLS3),
	MEGA_SPRUCE       = standard("MEGA_SPRUCE", TILE_MEGA_SPRUCE, TILE_MEGA_SPRUCE2),
	MEGA_SPRUCE_HILLS = standard("MEGA_SPRUCE_HILLS", TILE_MEGA_SPRUCE_HILLS, TILE_MEGA_SPRUCE_HILLS2),
	MEGA_TAIGA        = standard("MEGA_TAIGA", TILE_MEGA_TAIGA, TILE_MEGA_TAIGA2),
	MEGA_TAIGA_HILLS  = standard("MEGA_TAIGA_HILLS", TILE_MEGA_TAIGA_HILLS, TILE_MEGA_TAIGA_HILLS2),
	
	// Savanna stuff:
	SAVANNA          = standard("SAVANNA",
						TILE_SAVANNA, TILE_SAVANNA2, TILE_SAVANNA3,
						TILE_GRASS,
						TILE_GRASS2, TILE_GRASS2,
						TILE_GRASS3, TILE_GRASS3,
						TILE_GRASS4, TILE_GRASS4),
	SAVANNA_CLIFFS   = standard("SAVANNA_CLIFFS",
						TILE_SAVANNA_CLIFFS,
						TILE_SAVANNA_CLIFFS2,
						TILE_SAVANNA_CLIFFS3,
						TILE_CLIFFS),
	PLATEAU_SAVANNA  = standard("PLATEAU_SAVANNA",
						TILE_PLATEAU_GRASS, TILE_PLATEAU_GRASS,
						TILE_PLATEAU_GRASS2, TILE_PLATEAU_GRASS2,
						TILE_PLATEAU_GRASS3, TILE_PLATEAU_GRASS3,
						TILE_PLATEAU_SAVANNA,
						TILE_PLATEAU_SAVANNA2,
						TILE_PLATEAU_SAVANNA3),
	PLATEAU_SAVANNA_M = standard("PLATEAU_SAVANNA_M",
						TILE_PLATEAU_GRASS,
						TILE_PLATEAU_GRASS2,
						TILE_PLATEAU_GRASS3,
						TILE_PLATEAU_SAVANNA2,
						TILE_PLATEAU_SAVANNA3,
						TILE_CLIFFS_CLOUDS,
						TILE_SAVANNA_CLIFFS_CLOUDS,
						TILE_SAVANNA_CLIFFS_CLOUDS2,
						TILE_SAVANNA_CLIFFS_CLOUDS3),
	
	// Mesa stuff:
	MESA                   = standard("MESA",
								TILE_MESA, TILE_MESA2, TILE_MESA3, TILE_MESA4,
								TILE_SAND_BUSHES),
	BRYCE                  = standard("BRYCE", TILE_BRYCE, TILE_BRYCE2, TILE_BRYCE3, TILE_BRYCE4),
	PLATEAU_MESA           = standard("PLATEAU_MESA", TILE_PLATEAU_MESA, TILE_PLATEAU_MESA2),
	PLATEAU_MESA_LOW       = standard("PLATEAU_MESA_LOW", TILE_PLATEAU_MESA_LOW, TILE_PLATEAU_MESA_LOW2),
	PLATEAU_MESA_TREES     = standard("PLATEAU_MESA_TREES",
								TILE_PLATEAU_MESA, TILE_PLATEAU_MESA2,
								TILE_PLATEAU_TREES),
	PLATEAU_MESA_TREES_LOW = standard("PLATEAU_MESA_TREES_LOW",
								TILE_PLATEAU_MESA_LOW, TILE_PLATEAU_MESA_LOW2,
								TILE_PLATEAU_TREES_LOW),
	
	// Swamp stuff:
	SWAMP       = standard("SWAMP",
					TILE_SWAMP, TILE_SWAMP, TILE_SWAMP,
					TILE_SWAMP2,
					TILE_SWAMP3,
					TILE_SWAMP4,
					TILE_SWAMP5,
					TILE_SWAMP6),
	SWAMP_HILLS = standard("SWAMP_HILLS",
					TILE_SWAMP_HILLS,
					TILE_SWAMP_HILLS2,
					TILE_SWAMP_HILLS3,
					TILE_SWAMP_HILLS4,
					TILE_SWAMP_HILLS5),
	
	// Coastline stuff (water, lava, shores)
	WATER       = standard("WATER", TILE_WATER, TILE_WATER2),
	LAVA        = standard("LAVA", TILE_LAVA, TILE_LAVA2),
	SHORE       = new TextureSetShore("SHORE", WATER, TILE_SHORE, TILE_SHORE2, TILE_SHORE3),
	ROCK_SHORE  = new TextureSetShore("ROCK_SHORE", WATER, TILE_ROCK_SHORE).stitchesToNull(),
	LAVA_SHORE  = new TextureSetShore("LAVA_SHORE", LAVA, TILE_LAVA_SHORE, TILE_LAVA_SHORE2).stitchesToNull(),
	
	// Misc. stuff:
	MUSHROOM   = standard("MUSHROOM", TILE_MUSHROOM, TILE_MUSHROOM2),
	CAVE_WALLS = standard("CAVE_WALLS", TILE_CAVE_WALLS),
	
	// Structure stuff:
	HOUSE         = standard("HOUSE", TILE_HOUSE),
	FENCE         = standard("FENCE", TILE_FENCE).stitchTo(HOUSE),
	NETHER_BRIDGE = standard("NETHER_BRIDGE", TILE_NETHER_BRIDGE),
	NETHER_BRIDGE_X = standard("NETHER_BRIDGE_X", TILE_NETHER_BRIDGE),
	NETHER_BRIDGE_Z = standard("NETHER_BRIDGE_Z", TILE_NETHER_BRIDGE),
	NETHER_BRIDGE_END_X = standard("NETHER_BRIDGE_END_X", TILE_NETHER_BRIDGE),
	NETHER_BRIDGE_END_Z = standard("NETHER_BRIDGE_END_Z", TILE_NETHER_BRIDGE);
	
	// Sophisticated stitching stuff:
	static {
		stitchMutually(PLAINS, SUNFLOWERS);
		WATER.stitchTo(SHORE, ROCK_SHORE, SWAMP);
		LAVA.stitchTo(SHORE, ROCK_SHORE, LAVA_SHORE, NETHER_BRIDGE,
				NETHER_BRIDGE_X, NETHER_BRIDGE_END_X,
				NETHER_BRIDGE_Z, NETHER_BRIDGE_END_Z);
		SWAMP.stitchTo(SWAMP_HILLS);
		SNOW.stitchTo(SNOW_PINES, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
		SNOW_PINES.stitchTo(SNOW, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
		stitchMutually(MOUNTAINS, MOUNTAINS_NAKED, MOUNTAINS_SNOW_CAPS, MOUNTAINS_ALL);
		DESERT.stitchTo(MESA, BRYCE);
		stitchMutually(PLATEAU_MESA, PLATEAU_MESA_TREES, PLATEAU_SAVANNA, PLATEAU_SAVANNA_M);
		stitchMutually(PLATEAU_MESA_LOW, PLATEAU_MESA_TREES_LOW);
		
		// Nether Fortress stuff:
		NETHER_BRIDGE.stitchToHorizontal(NETHER_BRIDGE_X, NETHER_BRIDGE_END_X);
		NETHER_BRIDGE.stitchToVertical(NETHER_BRIDGE_Z, NETHER_BRIDGE_END_Z);
		NETHER_BRIDGE_X.stitchToHorizontal(NETHER_BRIDGE, NETHER_BRIDGE_END_X);
		NETHER_BRIDGE_END_X.stitchToHorizontal(NETHER_BRIDGE, NETHER_BRIDGE_X);
		NETHER_BRIDGE_Z.stitchToVertical(NETHER_BRIDGE, NETHER_BRIDGE_END_Z);
		NETHER_BRIDGE_END_Z.stitchToVertical(NETHER_BRIDGE, NETHER_BRIDGE_Z);
	}
	
	/** Name of the texture pack to write in the config file. */
	public final String name; 
	
	/** The actual textures in this set. */
	public final ResourceLocation[] textures;
	
	/** Texture sets that a tile rendered with this set can be stitched to,
	 * excluding itself. */
	private final Set<TextureSet> stitchTo = new HashSet<TextureSet>();
	private final Set<TextureSet> stitchToHorizontal = new HashSet<TextureSet>();
	private final Set<TextureSet> stitchToVertical = new HashSet<TextureSet>();
	
	/** Whether the texture set is part of the standard pack. Only true for
	 * static constants in this class. */
	final boolean isStandard;
	
	private boolean stitchesToNull = false;
	private boolean anisotropicStitching = false;
	
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
	
	/** Allow this texture set to be stitched to empty space, i.e. edge of the map. */
	public TextureSet stitchesToNull() {
		this.stitchesToNull = true;
		return this;
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
	
	public TextureSet stitchToHorizontal(TextureSet ... textureSets) {
		this.anisotropicStitching = true;
		for (TextureSet textureSet : textureSets) {
			stitchToHorizontal.add(textureSet);
		}
		return this;
	}
	public TextureSet stitchToVertical(TextureSet ... textureSets) {
		this.anisotropicStitching = true;
		for (TextureSet textureSet : textureSets) {
			stitchToVertical.add(textureSet);
		}
		return this;
	}
	
	/** Actually used when stitching along the diagonal. */
	public boolean shouldStitchTo(TextureSet toSet) {
		return toSet == this || stitchesToNull && toSet == null || stitchTo.contains(toSet);
	}
	public boolean shouldStitchToHorizontally(TextureSet toSet) {
		if (toSet == this || stitchesToNull && toSet == null) return true;
		if (anisotropicStitching) return stitchToHorizontal.contains(toSet);
		else return stitchTo.contains(toSet);
	}
	public boolean shouldStitchToVertically(TextureSet toSet) {
		if (toSet == this || stitchesToNull && toSet == null) return true;
		if (anisotropicStitching) return stitchToVertical.contains(toSet);
		else return stitchTo.contains(toSet);
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
		private final TextureSet water;
		public TextureSetShore(String name, TextureSet water, ResourceLocation ... textures) {
			super(true, name, textures);
			this.water = water;
		}
		@Override
		public boolean shouldStitchToHorizontally(TextureSet otherSet) {
			return otherSet == this || !water.shouldStitchToHorizontally(otherSet);
		}
		public boolean shouldStitchToVertically(TextureSet otherSet) {
			return otherSet == this || !water.shouldStitchToVertically(otherSet);
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
