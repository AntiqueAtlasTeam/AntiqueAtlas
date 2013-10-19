package hunternif.mc.atlas.gui;

import static hunternif.mc.atlas.gui.Textures.MAP_BEACH;
import static hunternif.mc.atlas.gui.Textures.MAP_FOREST;
import static hunternif.mc.atlas.gui.Textures.MAP_FOREST2;
import static hunternif.mc.atlas.gui.Textures.MAP_HILLS;
import static hunternif.mc.atlas.gui.Textures.MAP_MOUNTAINS;
import static hunternif.mc.atlas.gui.Textures.MAP_MOUNTAINS2;
import static hunternif.mc.atlas.gui.Textures.MAP_MUSHROOM;
import static hunternif.mc.atlas.gui.Textures.MAP_MUSHROOM2;
import static hunternif.mc.atlas.gui.Textures.MAP_PINES;
import static hunternif.mc.atlas.gui.Textures.MAP_PINES2;
import static hunternif.mc.atlas.gui.Textures.MAP_PINES3;
import static hunternif.mc.atlas.gui.Textures.MAP_PLAINS;
import static hunternif.mc.atlas.gui.Textures.MAP_SAND;
import static hunternif.mc.atlas.gui.Textures.MAP_SWAMP;
import static hunternif.mc.atlas.gui.Textures.MAP_SWAMP2;
import static hunternif.mc.atlas.gui.Textures.MAP_SWAMP3;
import static hunternif.mc.atlas.gui.Textures.MAP_SWAMP4;
import static hunternif.mc.atlas.gui.Textures.MAP_SWAMP5;
import static hunternif.mc.atlas.gui.Textures.MAP_SWAMP6;
import static hunternif.mc.atlas.gui.Textures.MAP_WATER;
import static net.minecraft.world.biome.BiomeGenBase.beach;
import static net.minecraft.world.biome.BiomeGenBase.desert;
import static net.minecraft.world.biome.BiomeGenBase.desertHills;
import static net.minecraft.world.biome.BiomeGenBase.extremeHills;
import static net.minecraft.world.biome.BiomeGenBase.extremeHillsEdge;
import static net.minecraft.world.biome.BiomeGenBase.forest;
import static net.minecraft.world.biome.BiomeGenBase.forestHills;
import static net.minecraft.world.biome.BiomeGenBase.frozenOcean;
import static net.minecraft.world.biome.BiomeGenBase.frozenRiver;
import static net.minecraft.world.biome.BiomeGenBase.iceMountains;
import static net.minecraft.world.biome.BiomeGenBase.jungle;
import static net.minecraft.world.biome.BiomeGenBase.jungleHills;
import static net.minecraft.world.biome.BiomeGenBase.mushroomIsland;
import static net.minecraft.world.biome.BiomeGenBase.mushroomIslandShore;
import static net.minecraft.world.biome.BiomeGenBase.ocean;
import static net.minecraft.world.biome.BiomeGenBase.plains;
import static net.minecraft.world.biome.BiomeGenBase.river;
import static net.minecraft.world.biome.BiomeGenBase.sky;
import static net.minecraft.world.biome.BiomeGenBase.swampland;
import static net.minecraft.world.biome.BiomeGenBase.taiga;
import static net.minecraft.world.biome.BiomeGenBase.taigaHills;
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
		addTexture(jungleHills,	MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(forestHills,	false, MAP_FOREST, MAP_FOREST2);
		addTexture(desertHills,	MAP_HILLS);
		addTexture(extremeHills,	 MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(extremeHillsEdge, MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(iceMountains,MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(forest,		MAP_FOREST, MAP_FOREST2);
		addTexture(jungle,		MAP_FOREST, MAP_FOREST2);
		addTexture(taiga,		MAP_PINES, MAP_PINES2, MAP_PINES3);
		addTexture(taigaHills,	MAP_PINES, MAP_PINES2, MAP_PINES3);
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