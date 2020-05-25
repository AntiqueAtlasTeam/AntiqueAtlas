package hunternif.mc.atlas.client;

import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


import static hunternif.mc.atlas.client.Textures.*;

public class TextureSet implements Comparable<TextureSet> {
	public static final TextureSet
	// This first texture set is meant to be an example for the config
	TEST        = new TextureSet(false, new ResourceLocation("test"), TILE_TEST, TILE_TEST),
	
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
	FOREST              = standard("FOREST", TILE_FOREST, TILE_FOREST2, TILE_FOREST3),
	FOREST_HILLS        = standard("FOREST_HILLS", TILE_FOREST_HILLS, TILE_FOREST_HILLS2, TILE_FOREST_HILLS3),
	FOREST_FLOWERS      = standard("FOREST_FLOWERS", TILE_FOREST_FLOWERS, TILE_FOREST_FLOWERS2, TILE_FOREST_FLOWERS3),
	SPARSE_FOREST       = standard("SPARSE_FOREST", TILE_SPARSE_FOREST, TILE_SPARSE_FOREST2, TILE_SPARSE_FOREST3),
	SPARSE_FOREST_HILLS = standard("SPARSE_FOREST_HILLS", TILE_SPARSE_FOREST_HILLS, TILE_SPARSE_FOREST_HILLS2, TILE_SPARSE_FOREST_HILLS3),
	DENSE_FOREST        = standard("DENSE_FOREST", TILE_DENSE_FOREST, TILE_DENSE_FOREST2),
	DENSE_FOREST_HILLS  = standard("DENSE_FOREST_HILLS", TILE_DENSE_FOREST_HILLS, TILE_DENSE_FOREST_HILLS2),
	
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
	
	END_VOID = standard("END_VOID", TILE_END_VOID),
	END_ISLAND = standard("END_ISLAND", TILE_END_ISLAND, TILE_END_ISLAND2),
	END_ISLAND_PLANTS = standard("END_ISLAND_PLANTS", TILE_END_ISLAND_PLANTS, TILE_END_ISLAND_PLANTS2),
	
	// Misc. stuff:
	MUSHROOM   = standard("MUSHROOM", TILE_MUSHROOM, TILE_MUSHROOM2),
	CAVE_WALLS = standard("CAVE_WALLS", TILE_CAVE_WALLS),
	RAVINE     = standard("RAVINE", TILE_RAVINE),
	
	// Village:
	HOUSE          = standard("HOUSE", TILE_HOUSE),
	FENCE          = standard("FENCE", TILE_FENCE).stitchTo(HOUSE),
	LIBRARY        = standard("LIBRARY", TILE_LIBRARY),
	SMITHY         = standard("SMITHY", TILE_SMITHY),
	L_HOUSE        = standard("L-HOUSE", TILE_L_HOUSE),
	FARMLAND_LARGE = standard("FARMLAND_LARGE", TILE_FARMLAND_LARGE),
	FARMLAND_SMALL = standard("FARMLAND_SMALL", TILE_FARMLAND_SMALL),
	VILLAGE_TORCH  = standard("VILLAGE_TORCH", TILE_VILLAGE_TORCH),
	WELL           = standard("WELL", TILE_WELL),
//	VILLAGE_PATH_X = standard("VILLAGE_PATH_X", TILE_VILLAGE_PATH_X),
//	VILLAGE_PATH_Z = standard("VILLAGE_PATH_Z", TILE_VILLAGE_PATH_Z),
	HUT            = standard("HUT", TILE_HUT),
	HOUSE_SMALL    = standard("HOUSE_SMALL", TILE_HOUSE_SMALL),
	BUTCHERS_SHOP  = standard("BUTCHERS_SHOP", TILE_BUTCHERS_SHOP),
	CHURCH         = standard("CHURCH", TILE_CHURCH),
	
	// Nether Fortress:
	NETHER_BRIDGE       = standard("NETHER_BRIDGE", TILE_NETHER_BRIDGE),
	NETHER_BRIDGE_X     = standard("NETHER_BRIDGE_X", TILE_NETHER_BRIDGE_X),
	NETHER_BRIDGE_Z     = standard("NETHER_BRIDGE_Z", TILE_NETHER_BRIDGE_Z),
	NETHER_BRIDGE_END_X = standard("NETHER_BRIDGE_END_X", TILE_NETHER_BRIDGE_END_X),
	NETHER_BRIDGE_END_Z = standard("NETHER_BRIDGE_END_Z", TILE_NETHER_BRIDGE_END_Z),
	NETHER_BRIDGE_GATE  = standard("NETHER_BRIDGE_GATE", TILE_NETHER_BRIDGE_GATE),
	NETHER_TOWER        = standard("NETHER_TOWER", TILE_NETHER_TOWER),
	NETHER_WALL         = standard("NETHER_WALL", TILE_NETHER_WALL),
	NETHER_HALL         = standard("NETHER_HALL", TILE_NETHER_HALL),
	NETHER_FORT_STAIRS  = standard("NETHER_FORT_STAIRS", TILE_NETHER_FORT_STAIRS),
	NETHER_THRONE       = standard("NETHER_THRONE", TILE_NETHER_THRONE);

	// Sophisticated stitching stuff:
	static {
		stitchMutually(PLAINS, SUNFLOWERS);
		WATER.stitchTo(SHORE, ROCK_SHORE, SWAMP);
		LAVA.stitchTo(LAVA_SHORE);
		SWAMP.stitchTo(SWAMP_HILLS);
		SNOW.stitchTo(SNOW_PINES, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
		SNOW_PINES.stitchTo(SNOW, SNOW_HILLS, ICE_SPIKES, SNOW_PINES_HILLS);
		stitchMutually(MOUNTAINS, MOUNTAINS_NAKED, MOUNTAINS_SNOW_CAPS, MOUNTAINS_ALL);
		DESERT.stitchTo(MESA, BRYCE);
		stitchMutually(PLATEAU_MESA, PLATEAU_MESA_TREES, PLATEAU_SAVANNA, PLATEAU_SAVANNA_M);
		stitchMutually(PLATEAU_MESA_LOW, PLATEAU_MESA_TREES_LOW);
		stitchMutually(END_ISLAND, END_ISLAND_PLANTS);

		// Village stuff:
//		stitchMutually(VILLAGE_PATH_X, VILLAGE_PATH_Z);

		// Nether Fortress stuff:
		LAVA.stitchTo(NETHER_BRIDGE, NETHER_BRIDGE_GATE, NETHER_TOWER, NETHER_WALL,
				NETHER_HALL, NETHER_FORT_STAIRS,
				NETHER_BRIDGE_X, NETHER_BRIDGE_END_X,
				NETHER_BRIDGE_Z, NETHER_BRIDGE_END_Z);
		stitchMutuallyHorizontally(NETHER_BRIDGE, NETHER_BRIDGE_GATE, NETHER_TOWER,
				NETHER_HALL, NETHER_FORT_STAIRS, NETHER_THRONE,
				NETHER_BRIDGE_X, NETHER_BRIDGE_END_X);
		stitchMutuallyVertically(NETHER_BRIDGE, NETHER_BRIDGE_GATE, NETHER_TOWER,
				NETHER_HALL, NETHER_FORT_STAIRS, NETHER_THRONE,
				NETHER_BRIDGE_Z, NETHER_BRIDGE_END_Z);
		stitchMutuallyHorizontally(NETHER_WALL, NETHER_HALL, NETHER_FORT_STAIRS);
		stitchMutuallyVertically(NETHER_WALL, NETHER_HALL, NETHER_FORT_STAIRS);
	}
	
	/** Name of the texture pack to write in the config file. */
	public final ResourceLocation name;
	
	/** The actual textures in this set. */
	public final ResourceLocation[] textures;
	
	/** Texture sets that a tile rendered with this set can be stitched to,
	 * excluding itself. */
	private final Set<TextureSet> stitchTo = new HashSet<>();
	private final Set<TextureSet> stitchToHorizontal = new HashSet<>();
	private final Set<TextureSet> stitchToVertical = new HashSet<>();
	
	/** Whether the texture set is part of the standard pack. Only true for
	 * static constants in this class. */
	final boolean isStandard;
	
	private boolean stitchesToNull = false;
	private boolean anisotropicStitching = false;
	
	private static TextureSet standard(String name, ResourceLocation ... textures) {
		return new TextureSet(true, new ResourceLocation("antiqueatlas", name.toLowerCase(Locale.ROOT)), textures);
	}
	
	private TextureSet(boolean isStandard, ResourceLocation name, ResourceLocation ... textures) {
		this.isStandard = isStandard;
		this.name = name;
		this.textures = textures;
	}
	/** Name has to be unique, it is used for equals() tests. */
	public TextureSet(ResourceLocation name, ResourceLocation ... textures) {
		this(false, name, textures);
	}
	
	/** Allow this texture set to be stitched to empty space, i.e. edge of the map. */
    TextureSet stitchesToNull() {
		this.stitchesToNull = true;
		return this;
	}
	
	/** Add other texture sets that this texture set will be stitched to
	 * (but the opposite may be false, in case of asymmetric stitching.) */
    private TextureSet stitchTo(TextureSet... textureSets) {
		Collections.addAll(stitchTo, textureSets);
		return this;
	}
	/** Same as {@link #stitchTo(TextureSet...)}, but symmetrical. */
	public TextureSet stitchToMutual(TextureSet ... textureSets) {
		for (TextureSet textureSet : textureSets) {
			stitchTo.add(textureSet);
			textureSet.stitchTo.add(this);
		}
		return this;
	}
	
	private TextureSet stitchToHorizontal(TextureSet... textureSets) {
		this.anisotropicStitching = true;
		Collections.addAll(stitchToHorizontal, textureSets);
		return this;
	}
	private TextureSet stitchToVertical(TextureSet... textureSets) {
		this.anisotropicStitching = true;
		Collections.addAll(stitchToVertical, textureSets);
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
		return this.name.equals(set.name);
	}
	
	/** A special texture set that is stitched to everything except water. */
	private static class TextureSetShore extends TextureSet {
		private final TextureSet water;
		TextureSetShore(String name, TextureSet water, ResourceLocation... textures) {
			super(true, new ResourceLocation("antiqueatlas", name.toLowerCase(Locale.ROOT)), textures);
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
	private static void stitchMutually(TextureSet... sets) {
		for (TextureSet set1 : sets) {
			for (TextureSet set2 : sets) {
				if (set1 != set2) set1.stitchTo(set2);
			}
		}
	}
	private static void stitchMutuallyHorizontally(TextureSet... sets) {
		for (TextureSet set1 : sets) {
			for (TextureSet set2 : sets) {
				if (set1 != set2) set1.stitchToHorizontal(set2);
			}
		}
	}
	private static void stitchMutuallyVertically(TextureSet... sets) {
		for (TextureSet set1 : sets) {
			for (TextureSet set2 : sets) {
				if (set1 != set2) set1.stitchToVertical(set2);
			}
		}
	}

	@Override
	public int compareTo(TextureSet textureSet) {
		return name.toString().compareTo(textureSet.name.toString());
	}
}
