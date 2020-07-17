package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.BiomeTextureMap;
import hunternif.mc.impl.atlas.client.TextureSet;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.ext.ExtTileTextureMap;
import hunternif.mc.impl.atlas.ext.TileIdRegisteredCallback;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 * @author Hunternif
 * @author Haven King
 */
public class TileNameS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "update");

	public TileNameS2CPacket(Map<Identifier, Integer> tileIdToBiomeIdMap) {
		this.writeVarInt(tileIdToBiomeIdMap.size());

		for (Map.Entry<Identifier, Integer> entry : tileIdToBiomeIdMap.entrySet()) {
			this.writeIdentifier(entry.getKey());
			this.writeVarInt(entry.getValue());
		}
	}

	public TileNameS2CPacket(Identifier id, int biomeId) {
		this.writeVarInt(1);
		this.writeIdentifier(id);
		this.writeVarInt(biomeId);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static void apply(PacketContext context, PacketByteBuf buf) {
		int size = buf.readVarInt();
		Map<Identifier, Integer> tileIdToBiomeIdMap = new HashMap<>();
		for (int i = 0; i < size; ++i) {
			tileIdToBiomeIdMap.put(buf.readIdentifier(), buf.readVarInt());
		}

		context.getTaskQueue().execute(() -> {
			for (Map.Entry<Identifier, Integer> entry : tileIdToBiomeIdMap.entrySet()) {
				int oldID = ExtTileIdMap.INSTANCE.getPseudoBiomeID(entry.getKey());

				if (oldID != ExtTileIdMap.NOT_FOUND && oldID != entry.getValue() && Registry.BIOME.containsId(oldID)) {
					BiomeTextureMap.instance().setTexture(Registry.BIOME.get(oldID), null);
				}

				ExtTileIdMap.INSTANCE.setPseudoBiomeID(entry.getKey(), entry.getValue());
				TextureSet texture = ExtTileTextureMap.instance().getTexture(entry.getKey());
				BiomeTextureMap.instance().setTexture(entry.getValue(), texture);
			}

			TileIdRegisteredCallback.EVENT.invoker().onTileIDsReceived(tileIdToBiomeIdMap);
		});
	}
}
