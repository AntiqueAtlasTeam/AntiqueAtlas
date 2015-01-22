package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetMap;
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
	 * <p>This map maps unique tile name to a TextureSet.</p>
	 */
	public final Map<String, TextureSet> pendingTextures = new HashMap<String, TextureSet>();
	
	@Override
	public void setTexture(String uniqueTileName, ResourceLocation ... textures) {
		TextureSet set = new TextureSet(uniqueTileName, textures);
		TextureSetMap.instance().register(set);
		setTexture(uniqueTileName, set);
	}
	
	@Override
	public void setTexture(String uniqueTileName, TextureSet textureSet) {
		int id = ExtTileIdMap.instance().getPseudoBiomeID(uniqueTileName);
		if (id != ChunkBiomeAnalyzer.NOT_FOUND) {
			BiomeTextureMap.instance().setTexture(id, textureSet);
		} else {
			pendingTextures.put(uniqueTileName, textureSet);
		}
	}
	
	@Override
	public void putCustomTile(World world, int atlasID, String tileName, int chunkX, int chunkZ) {
		//TODO put custom tiles in atlas
	}
	
	@Override
	public void putCustomGlobalTile(World world, String tileName, int chunkX, int chunkZ) {
		if (world.isRemote) {
			AntiqueAtlasMod.logger.warn("Client tried to put global tile");
			return;
		}
		boolean isIdRegistered = ExtTileIdMap.instance().getPseudoBiomeID(tileName) != ChunkBiomeAnalyzer.NOT_FOUND;
		int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		data.setBiomeIdAt(world.provider.dimensionId, chunkX, chunkZ, biomeID);
		// Send name-ID packet:
		if (!isIdRegistered) {
			TileNameIDPacket packet = new TileNameIDPacket();
			packet.put(tileName, biomeID);
			PacketDispatcher.sendToAll(packet);
		}
		// Send tile packet:
		TilesPacket packet = new TilesPacket(world.provider.dimensionId);
		packet.addTile(chunkX, chunkZ, biomeID);
		PacketDispatcher.sendToAll(packet);
	}
}
