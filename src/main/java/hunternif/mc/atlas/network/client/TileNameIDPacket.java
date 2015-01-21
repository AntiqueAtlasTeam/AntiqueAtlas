package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.impl.TileApiImpl;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 * @author Hunternif
 */
public class TileNameIDPacket extends AbstractClientMessage
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
	public void read(PacketBuffer buffer) throws IOException {
		int size = buffer.readShort();
		for (int i = 0; i < size; i++) {
			String name = ByteBufUtils.readUTF8String(buffer);
			int biomeID = buffer.readShort();
			nameToIdMap.put(name, biomeID);
		}
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeShort(nameToIdMap.size());
		for (Entry<String, Integer> entry : nameToIdMap.entrySet()) {
			ByteBufUtils.writeUTF8String(buffer, entry.getKey());
			buffer.writeShort(entry.getValue());
		}
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		if (nameToIdMap == null) {
			AntiqueAtlasMod.logger.error("Exception handling TileNameIDPacket: nameToIdMap was NULL");
			return;
		}
		TileApiImpl api = (TileApiImpl) AtlasAPI.getTileAPI();
		String name;
		int biomeID;
		for (Entry<String, Integer> entry : nameToIdMap.entrySet()) {
			name = entry.getKey();
			biomeID = entry.getValue();
			ExtTileIdMap.instance().setPseudoBiomeID(name, biomeID);
			// Register pending textures:
			TextureSet pending = api.pendingTextures.remove(name);
			if (pending != null) {
				BiomeTextureMap.instance().setTexture(biomeID, pending);
			}
		}
	}
}
