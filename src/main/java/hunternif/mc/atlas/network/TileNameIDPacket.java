package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.impl.TileApiImpl;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.network.ByteBufUtils;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 * @author Hunternif
 */
public class TileNameIDPacket extends ModPacket {

	private Map<String, Integer> nameToIdMap;
	
	public TileNameIDPacket() {}
	
	public TileNameIDPacket(Map<String, Integer> nameToIdMap) {
		this.nameToIdMap = nameToIdMap;
	}
	
	public TileNameIDPacket put(String name, int biomeID) {
		if (nameToIdMap == null) {
			nameToIdMap = new HashMap<String, Integer>();
		}
		nameToIdMap.put(name, biomeID);
		return this;
	}
	
	
	@Override
	public void encodeInto(ByteBuf buffer) {
		buffer.writeShort(nameToIdMap.size());
		for (Entry<String, Integer> entry : nameToIdMap.entrySet()) {
			ByteBufUtils.writeUTF8String(buffer, entry.getKey());
			buffer.writeShort(entry.getValue());
		}
	}
	
	@Override
	public void handleServerSide(EntityPlayer player, ByteBuf buffer) {}
	
	@Override
	public void handleClientSide(EntityPlayer player, ByteBuf buffer) {
		TileApiImpl api = (TileApiImpl) AtlasAPI.getTileAPI();
		int length = buffer.readShort();
		for (int i = 0; i < length; i++) {
			String name = ByteBufUtils.readUTF8String(buffer);
			int biomeID = buffer.readShort();
			ExtTileIdMap.instance().setPseudoBiomeID(name, biomeID);
			
			// Register pending textures:
			if (api.pendingTextures.containsKey(name)) {
				Object pending = api.pendingTextures.remove(name);
				if (pending instanceof StandardTextureSet) {
					BiomeTextureMap.instance().setTexture(biomeID, (StandardTextureSet)pending);
				} else if (pending instanceof ResourceLocation[]){
					BiomeTextureMap.instance().setTexture(biomeID, (ResourceLocation[])pending);
				}
				AntiqueAtlasMod.proxy.updateBiomeTextureConfig();
			}
			if (api.pendingTexturesIfNone.containsKey(name)) {
				Object pending = api.pendingTexturesIfNone.remove(name);
				boolean textureChanged = false;
				if (pending instanceof StandardTextureSet) {
					textureChanged = BiomeTextureMap.instance().setTextureIfNone(biomeID, (StandardTextureSet)pending);
				} else if (pending instanceof ResourceLocation[]){
					textureChanged = BiomeTextureMap.instance().setTextureIfNone(biomeID, (ResourceLocation[])pending);
				}
				if (textureChanged) {
					AntiqueAtlasMod.proxy.updateBiomeTextureConfig();
				}
			}
		}
	}

}
