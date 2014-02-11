package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.core.BiomeTextureMap;
import net.minecraft.util.ResourceLocation;
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
	public boolean setTextureIfNone(int biomeID, ResourceLocation ... textures) {
		return BiomeTextureMap.instance().setTextureIfNone(biomeID, textures);
	}
	
	@Override
	public boolean setTextureIfNone(BiomeGenBase biome, ResourceLocation ... textures) {
		return setTextureIfNone(biome.biomeID, textures);
	}
	
	@Override
	public boolean setTextureIfNone(int biomeID, StandardTextureSet textureSet) {
		return BiomeTextureMap.instance().setTextureIfNone(biomeID, textureSet);
	}
	
	@Override
	public boolean setTextureIfNone(BiomeGenBase biome, StandardTextureSet textureSet) {
		return setTextureIfNone(biome.biomeID, textureSet);
	}
	
	@Override
	public void save() {
		AntiqueAtlasMod.proxy.updateBiomeTextureConfig();
	}
}
