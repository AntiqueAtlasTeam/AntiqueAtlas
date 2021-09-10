package hunternif.mc.impl.atlas.network.packet.s2c;

import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class S2CPacket extends BaseS2CMessage {
	public void send(ServerPlayerEntity playerEntity) {
		sendTo(playerEntity);
	}

	public void send(ServerWorld world) {
		sendToLevel(world);
	}

	public void send(MinecraftServer server) {
		sendToAll(server);
	}
}
