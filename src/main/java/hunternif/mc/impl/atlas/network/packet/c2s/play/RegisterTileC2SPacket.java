//package hunternif.mc.impl.atlas.network.packet.c2s.play;
//
//import java.util.function.Supplier;
//
//import hunternif.mc.impl.atlas.AntiqueAtlasMod;
//import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
//import hunternif.mc.impl.atlas.network.packet.s2c.play.TileNameS2CPacket;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.fml.network.NetworkEvent;
//
///**
// * Sent from client to server to register a new
// * (unique tile name)-(pseudo-biome ID) pair.
// * @author Hunternif
// * @author Haven King
// */
//public class RegisterTileC2SPacket extends C2SPacket {
//	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "register");
//
//	ResourceLocation id;
//	
//	public RegisterTileC2SPacket(ResourceLocation id) {
//		this.id = id;
//	}
//
//	public static void encode(final RegisterTileC2SPacket msg, final PacketBuffer packetBuffer) {
//		packetBuffer.writeResourceLocation(msg.id);
//	}
//
//	public static RegisterTileC2SPacket decode(final PacketBuffer packetBuffer) {
//		return new RegisterTileC2SPacket(packetBuffer.readResourceLocation());
//	}
//
//	public static void handle(final RegisterTileC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
//		final NetworkEvent.Context context = contextSupplier.get();
//		context.enqueueWork(() -> {
//			final ServerPlayerEntity sender = context.getSender();
//			if (sender == null) {
//				return;
//			}
//			for (ServerPlayerEntity playerEntity : context.getSender().getServer().getPlayerList().getPlayers()) {
//				new TileNameS2CPacket(msg.id).send(playerEntity);
//			}
//		});
//		context.setPacketHandled(true);
//	}
//
//	@Override
//	public ResourceLocation getId() {
//		return ID;
//	}
//
////	public static void apply(PacketContext context, PacketBuffer buf) {
////		ResourceLocation id = buf.readIdentifier();
////
////		context.getTaskQueue().execute(() -> {
////			for (ServerPlayerEntity playerEntity : context.getPlayer().getServer().getPlayerManager().getPlayerList()) {
////				new TileNameS2CPacket(id).send(playerEntity);
////			}
////		});
////	}
//}
