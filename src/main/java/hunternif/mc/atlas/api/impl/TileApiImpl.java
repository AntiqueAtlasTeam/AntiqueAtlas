package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.core.ChunkBiomeAnalyzer;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import hunternif.mc.atlas.network.client.TilesPacket;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TileApiImpl implements TileAPI {
	/**
	 * Because pseudo-biome IDs have to be synced with the server, they may not
	 * have been initialized when the texture registration methods are called on
	 * the client. In that case the textures are put in this map to be later
	 * registered when the server sends the packet with pseudo-biome ID for the
	 * corresponding unique name.
	 * <p>This map maps unique tile name to a StandardTextureSet or an array of
	 * ResourceLocations of textures.</p>
	 */
	public final Map<String, Object> pendingTextures = new HashMap<String, Object>(),
			pendingTexturesIfNone = new HashMap<String, Object>();
	
	@Override
	public void setTexture(String uniqueTileName, ResourceLocation ... textures) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			BiomeTextureMap.instance().setTexture(id, textures);
		} else {
			pendingTextures.put(uniqueTileName, textures);
		}
	}
	
	@Override
	public void setTexture(String uniqueTileName, StandardTextureSet textureSet) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			BiomeTextureMap.instance().setTexture(id, textureSet);
		} else {
			pendingTextures.put(uniqueTileName, textureSet);
		}
	}
	
	@Override
	public boolean setTextureIfNone(String uniqueTileName, ResourceLocation ... textures) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			return BiomeTextureMap.instance().setTextureIfNone(id, textures);
		} else {
			pendingTexturesIfNone.put(uniqueTileName, textures);
			return false;
		}
	}
	
	@Override
	public boolean setTextureIfNone(String uniqueTileName, StandardTextureSet textureSet) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			return BiomeTextureMap.instance().setTextureIfNone(id, textureSet);
		} else {
			pendingTexturesIfNone.put(uniqueTileName, textureSet);
			return false;
		}
	}
	
	@Override
	public void putCustomTile(World world, int dimension, String tileName, int chunkX, int chunkZ) {
		boolean isIdRegistered = ExtTileIdMap.instance().getPseudoBiomeID(tileName) != ChunkBiomeAnalyzer.NOT_FOUND;
		int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		data.setBiomeIdAt(dimension, chunkX, chunkZ, biomeID);
		if (!world.isRemote) {
			data.markDirty();
			// Send name-ID packet:
			if (!isIdRegistered) {
				TileNameIDPacket packet = new TileNameIDPacket();
				packet.put(tileName, biomeID);
				PacketDispatcher.sendToDimension(packet, world.provider.dimensionId);
			}
			// Send tile packet:
			TilesPacket packet = new TilesPacket(dimension);
			packet.addTile(chunkX, chunkZ, biomeID);
			PacketDispatcher.sendToDimension(packet, world.provider.dimensionId);
		}
	}
}
