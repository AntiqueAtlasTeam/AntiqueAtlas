package hunternif.mc.atlas.gui;

import static net.minecraft.world.biome.BiomeGenBase.*;
import static hunternif.mc.atlas.gui.Textures.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public enum BiomeTextureMap {
	INSTANCE;
	public static BiomeTextureMap instance() {
		return INSTANCE;
	}
	private final ListMultimap<BiomeGenBase, ResourceLocation> textureMap;
	private BiomeTextureMap() {
		textureMap = ArrayListMultimap.create();
	}

	public void assignVanillaTextures() {
		addTexture(ocean,		MAP_WATER);
		addTexture(frozenOcean,	MAP_WATER);
		addTexture(river,		MAP_WATER);
		addTexture(frozenRiver,	MAP_WATER);
		addTexture(beach,		MAP_BEACH);
		addTexture(desert,		MAP_SAND);
		addTexture(plains,		MAP_PLAINS);
		//addTexture(icePlains,	null);
		addTexture(jungleHills,	MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(forestHills,	MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(desertHills,	MAP_HILLS);
		addTexture(extremeHills,	 MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(extremeHillsEdge, MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(iceMountains,MAP_MOUNTAINS, MAP_MOUNTAINS2);
		addTexture(forest,		MAP_FOREST, MAP_FOREST2);
		addTexture(jungle,		MAP_FOREST, MAP_FOREST2);
		addTexture(taiga,		MAP_PINES, MAP_PINES2);
		addTexture(taigaHills,	MAP_PINES, MAP_PINES2);
		//addTexture(swampland,	SWAMP);
		addTexture(sky,			MAP_BEACH);
		//addTexture(hell,		NETHER);
		//addTexture(mushroomIsland, MUSHROOM);
		addTexture(mushroomIslandShore, MAP_BEACH);
	}

	public void addTexture(BiomeGenBase biome, ResourceLocation ... textures) {
		for (ResourceLocation texture : textures) {
			textureMap.put(biome, texture);
		}
	}

	public int getVariations(BiomeGenBase biome) {
		List<ResourceLocation> list = textureMap.get(biome);
		return list.size();
	}

	public ResourceLocation getTexture(MapTile tile) {
		List<ResourceLocation> list = textureMap.get(tile.getBiome());
		if (list.isEmpty() || tile.variationNumber >= list.size()) {
			return null;
		}
		return list.get(tile.variationNumber);
	}
	
	public boolean haveSameTexture(BiomeGenBase ... biomes) {
		List<ResourceLocation> textures = null;
		for (BiomeGenBase biome : biomes) {
			if (textures == null) {
				textures = new ArrayList<ResourceLocation>(textureMap.get(biome));
			} else {
				textures.retainAll(textureMap.get(biome));
			}
		}
		return !textures.isEmpty();
	}
}