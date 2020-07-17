package hunternif.mc.impl.atlas.network.packet.s2c;

import hunternif.mc.impl.atlas.network.packet.AntiqueAtlasPacket;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class S2CPacket extends AntiqueAtlasPacket {
	public void send(ServerPlayerEntity playerEntity) {
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, this.getId(), this);
	}

	public void send(ServerWorld world) {
		for (ServerPlayerEntity playerEntity : world.getPlayers()) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, this.getId(), this);
		}
	}

	public void send(MinecraftServer server) {
		for (ServerPlayerEntity playerEntity : server.getPlayerManager().getPlayerList()) {
			ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, this.getId(), this);
		}
	}
}
