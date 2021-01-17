package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.play.TileNameS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
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

	public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		Identifier id = buf.readIdentifier();

		server.execute(() -> {
			new TileNameS2CPacket(id).send(server);
		});
	}
}
