package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.ext.TileIdRegisteredCallback;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 * @author Hunternif
 */
public class TileNameIDPacket extends AbstractClientMessage<TileNameIDPacket>
{
	private final Map<Identifier, Integer> nameToIdMap;

	public TileNameIDPacket() {
		nameToIdMap = new HashMap<>();
	}

	public TileNameIDPacket(Map<Identifier, Integer> nameToIdMap) {
		this.nameToIdMap = nameToIdMap;
	}

	public TileNameIDPacket put(Identifier name, int biomeID) {
		nameToIdMap.put(name, biomeID);
		return this;
	}

	@Override
	public void read(PacketByteBuf buffer) throws IOException {
		int size = buffer.readVarInt();
		for (int i = 0; i < size; i++) {
			String name = buffer.readString(512);
			// Reading negative value to save on traffic, because custom biome
			// IDs are always negative.
			int biomeID = -buffer.readVarInt();
			nameToIdMap.put(new Identifier(name), biomeID);
		}
	}

	@Override
	public void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(nameToIdMap.size());
		for (Entry<Identifier, Integer> entry : nameToIdMap.entrySet()) {
			buffer.writeString(entry.getKey().toString());
			// Writing negative value to save on traffic, because custom biome
			// IDs are always negative.
			buffer.writeVarInt(-entry.getValue());
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void process(PlayerEntity player, EnvType side) {
		for (Entry<Identifier, Integer> entry : nameToIdMap.entrySet()) {
			Identifier tileName = entry.getKey();
			int id = entry.getValue();
			// Remove old texture mapping
			int oldID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
			if (oldID != ExtTileIdMap.NOT_FOUND && oldID != id) {
				BiomeTextureMap.instance().setTexture(Registry.BIOME.get(oldID), null);
			}
			ExtTileIdMap.instance().setPseudoBiomeID(tileName, id);
			// Register new texture mapping
			TextureSet texture = ExtTileTextureMap.instance().getTexture(tileName);
			BiomeTextureMap.instance().setTexture(id, texture);
		}
		TileIdRegisteredCallback.EVENT.invoker().onTileIDsReceived(nameToIdMap);
	}
}
