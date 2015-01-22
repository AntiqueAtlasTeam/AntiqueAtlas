package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.PutBiomeTilePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeApiImpl implements BiomeAPI {
	@Override
	public void setTexture(int biomeID, ResourceLocation ... textures) {
		TextureSet set = TextureSetMap.instance().createAndRegister(textures);
		BiomeTextureMap.instance().setTexture(biomeID, set);
	}
	
	@Override
	public void setTexture(BiomeGenBase biome, ResourceLocation ... textures) {
		setTexture(biome.biomeID, textures);
	}
	
	@Override
	public void setTexture(int biomeID, TextureSet textureSet) {
		BiomeTextureMap.instance().setTexture(biomeID, textureSet);
	}
	
	@Override
	public void setTexture(BiomeGenBase biome, TextureSet textureSet) {
		setTexture(biome.biomeID, textureSet);
	}
	
	@Override
	public void setBiome(World world, int atlasID, int biomeID, int chunkX, int chunkZ) {
		int dimension = world.provider.dimensionId;
		PutBiomeTilePacket packet = new PutBiomeTilePacket(atlasID, dimension, chunkX, chunkZ, biomeID);
		if (world.isRemote) {
			PacketDispatcher.sendToServer(packet);
		} else {
			AtlasData data = AntiqueAtlasMod.itemAtlas.getAtlasData(atlasID, world);
			Tile tile = new Tile(biomeID);
			data.setTile(dimension, chunkX, chunkZ, tile);
			for (EntityPlayer syncedPlayer : data.getSyncedPlayers()) {
				PacketDispatcher.sendTo(new PutBiomeTilePacket(atlasID, dimension, chunkX, chunkZ, biomeID), (EntityPlayerMP)syncedPlayer);
			}
		}
	}
	
	@Override
	public void setBiome(World world, int atlasID, BiomeGenBase biome, int chunkX, int chunkZ) {
		setBiome(world, atlasID, biome.biomeID, chunkX, chunkZ);
	}
}
