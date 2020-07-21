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

import java.util.*;

/**
 * Used to send pairs (unique tile name)-(pseudo-biome ID) from the server
 * to clients.
 * @author Hunternif
 * @author Haven King
 */
public class TileNameS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "update");

	public TileNameS2CPacket(Collection<Identifier> tileIds) {
		this.writeVarInt(tileIds.size());

		for (Identifier id : tileIds) {
			this.writeIdentifier(id);
		}
	}

	public TileNameS2CPacket(Identifier id) {
		this.writeVarInt(1);
		this.writeIdentifier(id);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static void apply(PacketContext context, PacketByteBuf buf) {
		int size = buf.readVarInt();
		Collection<Identifier> tileIds = new HashSet<>();
		for (int i = 0; i < size; ++i) {
			tileIds.add(buf.readIdentifier());
		}

		context.getTaskQueue().execute(() -> {
			for (Identifier id: tileIds) {
				TextureSet texture = ExtTileTextureMap.instance().getTexture(id);
				BiomeTextureMap.instance().setTexture(id, texture);
			}

			TileIdRegisteredCallback.EVENT.invoker().onTileIDsReceived(tileIds);
		});
	}
}
