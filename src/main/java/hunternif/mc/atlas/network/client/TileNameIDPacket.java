package hunternif.mc.atlas.network.client;

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
public class TileNameIDPacket extends AbstractClientMessage<TileNameIDPacket>
{
	private Map<String, Integer> nameToIdMap;

	public TileNameIDPacket() {
		nameToIdMap = new HashMap<String, Integer>();
	}

	public TileNameIDPacket(Map<String, Integer> nameToIdMap) {
		this.nameToIdMap = nameToIdMap;
	}

	public TileNameIDPacket put(String name, int biomeID) {
		nameToIdMap.put(name, biomeID);
		return this;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		int size = buffer.readVarIntFromBuffer();
		for (int i = 0; i < size; i++) {
			String name = ByteBufUtils.readUTF8String(buffer);
			// Reading negative value to save on traffic, because custom biome
			// IDs are always negative.
			int biomeID = -buffer.readVarIntFromBuffer();
			nameToIdMap.put(name, biomeID);
		}
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarIntToBuffer(nameToIdMap.size());
		for (Entry<String, Integer> entry : nameToIdMap.entrySet()) {
			ByteBufUtils.writeUTF8String(buffer, entry.getKey());
			// Writing negative value to save on traffic, because custom biome
			// IDs are always negative.
			buffer.writeVarIntToBuffer(-entry.getValue());
		}
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
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
