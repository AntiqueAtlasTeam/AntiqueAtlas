package hunternif.mc.atlas.gui;

import static hunternif.mc.atlas.gui.Textures.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.core.MapTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public enum BiomeTextureMap {
	INSTANCE;
	public static BiomeTextureMap instance() {
		return INSTANCE;
	}
	private final ListMultimap<BiomeGenBase, ResourceLocation> textureMap;
	private final Set<BiomeGenBase> noStitchSet;
	private BiomeTextureMap() {
		textureMap = ArrayListMultimap.create();
		noStitchSet = new HashSet<BiomeGenBase>();
	}
	
	public static final ResourceLocation defaultTexture = MAP_PLAINS;

	public void assignVanillaTextures() {
		addTexture(ocean,		MAP_WATER);
		addTexture(frozenOcean,	MAP_WATER);
		addTexture(river,		MAP_WATER);
		addTexture(frozenRiver,	MAP_WATER);
		addTexture(beach,		MAP_BEACH);
		addTexture(desert,		MAP_SAND);
		addTexture(plains,		MAP_PLAINS);
		//addTexture(icePlains,	MAP_PLAINS);
		addTexture(jungleHills,	MAP_FOREST_HILLS, MAP_FOREST_HILLS2);
		addTexture(forestHills,	MAP_FOREST_HILLS, MAP_FOREST_HILLS2);
		addTexture(desertHills,	MAP_HILLS);
		addTexture(extremeHills,	 MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(extremeHillsEdge, MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(iceMountains,MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(forest,		MAP_FOREST, MAP_FOREST2);
		addTexture(jungle,		MAP_FOREST, MAP_FOREST2);
		addTexture(taiga,		MAP_PINES, MAP_PINES2, MAP_PINES3);
		addTexture(taigaHills,	MAP_PINES_HILLS, MAP_PINES_HILLS2, MAP_PINES_HILLS3);
		addTexture(swampland,	MAP_SWAMP, MAP_SWAMP, MAP_SWAMP, MAP_SWAMP2, MAP_SWAMP3, MAP_SWAMP4, MAP_SWAMP5, MAP_SWAMP6);
		addTexture(sky,			MAP_BEACH);
		//addTexture(hell,		NETHER);
		addTexture(mushroomIsland, MAP_MUSHROOM, MAP_MUSHROOM2);
		addTexture(mushroomIslandShore, MAP_BEACH);
	}

	public void addTexture(BiomeGenBase biome, ResourceLocation ... textures) {
		addTexture(biome, true, textures);
	}
	public void addTexture(BiomeGenBase biome, boolean stitch, ResourceLocation ... textures) {
		for (ResourceLocation texture : textures) {
			textureMap.put(biome, texture);
		}
		if (!stitch) {
			noStitchSet.add(biome);
		}
	}
	
	public void autoRegister(BiomeGenBase biome) {
		List<Type> types = Arrays.asList(BiomeDictionary.getTypesForBiome(biome));
		if (types.contains(Type.WATER)) {
			addTexture(biome, MAP_WATER);
		} else if (types.contains(Type.HILLS)) {
			addTexture(biome, MAP_HILLS);
		} else if (types.contains(Type.JUNGLE) || types.contains(Type.FOREST)) {
			addTexture(biome, MAP_FOREST, MAP_FOREST2);
		} else if (types.contains(Type.MOUNTAIN)) {
			addTexture(biome, MAP_MOUNTAINS, MAP_MOUNTAINS2);
		} else if (types.contains(Type.DESERT)) {
			addTexture(biome, MAP_SAND);
		} else {
			addTexture(biome, defaultTexture);
		}
	}
	
	public boolean isRegistered(BiomeGenBase biome) {
		return !textureMap.get(biome).isEmpty();
	}

	public int getVariations(BiomeGenBase biome) {
		if (!isRegistered(biome)) {
			autoRegister(biome);
		}
		List<ResourceLocation> list = textureMap.get(biome);
		return list.size();
	}

	public ResourceLocation getTexture(MapTile tile) {
		if (!isRegistered(tile.getBiome())) {
			autoRegister(tile.getBiome());
		}
		List<ResourceLocation> list = textureMap.get(tile.getBiome());
		return list.get(Math.min(list.size()-1, tile.variationNumber));
	}
	
	public boolean haveSameTexture(BiomeGenBase ... biomes) {
		List<ResourceLocation> textures = null;
		for (BiomeGenBase biome : biomes) {
			if (noStitchSet.contains(biome)) {
				// One of the biomes is set to no-stitch, therefore
				// only return true if it is the same biome:
				return areBiomesEqual(biomes);
			}
			if (textures == null) {
				textures = new ArrayList<ResourceLocation>(textureMap.get(biome));
			} else {
				textures.retainAll(textureMap.get(biome));
			}
		}
		return !textures.isEmpty();
	}
	public boolean areBiomesEqual(BiomeGenBase ... biomes) {
		for (int i = 1; i < biomes.length; i++) {
			if (biomes[0] != biomes[i]) {
				return false;
			}
		}
		return true;
	}
}