package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.StandardTextureSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeApiImpl implements BiomeAPI {
	@Override
	public void setTexture(int biomeID, ResourceLocation ... textures) {
		BiomeTextureMap.instance().setTexture(biomeID, textures);
	}
	
	@Override
	public void setTexture(BiomeGenBase biome, ResourceLocation ... textures) {
		setTexture(biome.biomeID, textures);
	}
	
	@Override
	public void setTexture(int biomeID, StandardTextureSet textureSet) {
		BiomeTextureMap.instance().setTexture(biomeID, textureSet);
	}
	
	@Override
	public void setTexture(BiomeGenBase biome, StandardTextureSet textureSet) {
		setTexture(biome.biomeID, textureSet);
	}
	
	@Override
	public void setBiome(World world, int atlasID, int biomeID, int chunkX, int chunkZ) {
		//TODO set biomes in an atlas
	}
	
	@Override
	public void setBiome(World world, int atlasID, BiomeGenBase biome, int chunkX, int chunkZ) {
		setBiome(world, atlasID, biome.biomeID, chunkX, chunkZ);
	}
}
