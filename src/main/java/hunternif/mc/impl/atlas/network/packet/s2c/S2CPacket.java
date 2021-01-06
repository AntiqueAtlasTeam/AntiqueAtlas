package hunternif.mc.impl.atlas.network.packet.s2c;


import java.util.function.Supplier;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.AntiqueAtlasPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class S2CPacket extends AntiqueAtlasPacket {
	@SuppressWarnings("deprecation")
	public void message(final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		if (shouldRun()) {
			context.enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
				context.setPacketHandled(handle(Minecraft.getInstance().player));
			}));
		}
	}

	public boolean shouldRun() {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public abstract boolean handle(ClientPlayerEntity player);

	public void send(ServerPlayerEntity playerEntity) {
		//		ServerSidePacketRegistry.INSTANCE.sendToPlayer(playerEntity, this.getId(), this);
		AntiqueAtlasMod.MOD_CHANNEL.sendTo(this, playerEntity.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public void send(ServerWorld world) {
		for (ServerPlayerEntity playerEntity : world.getPlayers()) {
			send(playerEntity);
		}
	}

	public void send(MinecraftServer server) {
		for (ServerPlayerEntity playerEntity : server.getPlayerList().getPlayers()) {
			send(playerEntity);
		}
	}
}
