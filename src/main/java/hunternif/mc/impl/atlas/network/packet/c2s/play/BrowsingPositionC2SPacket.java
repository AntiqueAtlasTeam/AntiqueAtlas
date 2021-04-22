package hunternif.mc.impl.atlas.network.packet.c2s.play;

import java.util.function.Supplier;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet used to save the last browsing position for a dimension in an atlas.
 * @author Hunternif
 * @author Haven King
 */
public class BrowsingPositionC2SPacket extends C2SPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "browsing_position");

	int atlasID;
	RegistryKey<World> world;
	int x;
	int y;
	double zoom;
	
	public BrowsingPositionC2SPacket(int atlasID, RegistryKey<World> world, int x, int y, double zoom) {
		this.atlasID = atlasID;
		this.world = world;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}

	public static void encode(final BrowsingPositionC2SPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.world.getLocation());
		packetBuffer.writeVarInt(msg.x);
		packetBuffer.writeVarInt(msg.y);
		packetBuffer.writeDouble(msg.zoom);
	}

	public static BrowsingPositionC2SPacket decode(final PacketBuffer packetBuffer) {
		return new BrowsingPositionC2SPacket(
				packetBuffer.readVarInt(),
				RegistryKey.getOrCreateKey(Registry.WORLD_KEY, packetBuffer.readResourceLocation()),
				packetBuffer.readVarInt(),
				packetBuffer.readVarInt(),
				packetBuffer.readDouble());
	}

	public static void handle(final BrowsingPositionC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayerEntity sender = context.getSender();
			if (sender == null) {
				return;
			}
			if (AntiqueAtlasConfig.itemNeeded.get() && !AtlasAPI.getPlayerAtlases(context.getSender()).contains(msg.atlasID)) {
				Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d",
						context.getSender().getCommandSource().getName(), msg.atlasID);
				return;
			}

			AntiqueAtlasMod.tileData.getData(msg.atlasID, context.getSender().getEntityWorld())
					.getWorldData(msg.world).setBrowsingPosition(msg.x, msg.y, msg.zoom);
		});
		context.setPacketHandled(true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

//	public static void apply(PacketContext context, PacketByteBuf buf) {
//		int atlasID = buf.readVarInt();
//		RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, buf.readR());
//		int x = buf.readVarInt();
//		int y = buf.readVarInt();
//		double zoom = buf.readDouble();
//
//		context.getTaskQueue().execute(() -> {
//			if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
//				Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d",
//						context.getPlayer().getCommandSource().getName(), atlasID);
//				return;
//			}
//
//			AntiqueAtlasMod.atlasData.getAtlasData(atlasID, context.getPlayer().getEntityWorld())
//					.getWorldData(world).setBrowsingPosition(x, y, zoom);
//		});
//	}
}
