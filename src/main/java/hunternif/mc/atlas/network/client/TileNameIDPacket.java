package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.impl.TileApiImpl;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.AbstractMessageHandler;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 * @author Hunternif
 */
public class TileNameIDPacket implements IMessage
{
	// Initialize here so it cannot be null when received (such as when empty)
	private Map<String, Integer> nameToIdMap = new HashMap<String, Integer>();

	public TileNameIDPacket() {}

	public TileNameIDPacket(Map<String, Integer> nameToIdMap) {
		this.nameToIdMap = nameToIdMap;
	}

	public TileNameIDPacket put(String name, int biomeID) {
		nameToIdMap.put(name, biomeID);
		return this;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		int size = buffer.readShort();
		for (int i = 0; i < size; i++) {
			String name = ByteBufUtils.readUTF8String(buffer);
			int biomeID = buffer.readShort();
			nameToIdMap.put(name, biomeID);
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeShort(nameToIdMap.size());
		for (Entry<String, Integer> entry : nameToIdMap.entrySet()) {
			ByteBufUtils.writeUTF8String(buffer, entry.getKey());
			buffer.writeShort(entry.getValue());
		}
	}

	public static class Handler extends AbstractMessageHandler<TileNameIDPacket> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, TileNameIDPacket msg, MessageContext ctx) {
			if (msg.nameToIdMap == null) {
				AntiqueAtlasMod.logger.error("Exception handling TileNameIDPacket: nameToIdMap was NULL");
				return null;
			}
			TileApiImpl api = (TileApiImpl) AtlasAPI.getTileAPI();
			String name;
			int biomeID;
			for (Entry<String, Integer> entry : msg.nameToIdMap.entrySet()) {
				name = entry.getKey();
				biomeID = entry.getValue();
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
			return null;
		}

		@Override
		public IMessage handleServerMessage(EntityPlayer player, TileNameIDPacket msg, MessageContext ctx) {
			return null;
		}
	}
}
