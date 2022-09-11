package hunternif.mc.impl.atlas.network.packet.s2c;

import dev.architectury.networking.NetworkManager;
import hunternif.mc.impl.atlas.network.packet.AntiqueAtlasPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class S2CPacket extends AntiqueAtlasPacket {
	public void send(ServerPlayerEntity playerEntity) {
		NetworkManager.sendToPlayer(playerEntity, this.getId(), this);
	}

	public void send(ServerWorld world) {
		NetworkManager.sendToPlayers(world.getPlayers(), this.getId(), this);
	}

	public void send(MinecraftServer server) {
		NetworkManager.sendToPlayers(server.getPlayerManager().getPlayerList(), this.getId(), this);
	}
}
