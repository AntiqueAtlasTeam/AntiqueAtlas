package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.TileNameS2CPacket;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Sent from client to server to register a new
 * (unique tile name)-(pseudo-biome ID) pair.
 * @author Hunternif
 * @author Haven King
 */
public class RegisterTileC2SPacket extends C2SPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "register");

	public RegisterTileC2SPacket(Identifier id) {
		this.writeIdentifier(id);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(PacketContext context, PacketByteBuf buf) {
		Identifier id = buf.readIdentifier();

		context.getTaskQueue().execute(() -> {
			for (ServerPlayerEntity playerEntity : context.getPlayer().getServer().getPlayerManager().getPlayerList()) {
				new TileNameS2CPacket(id).send(playerEntity);
			}
		});
	}
}
