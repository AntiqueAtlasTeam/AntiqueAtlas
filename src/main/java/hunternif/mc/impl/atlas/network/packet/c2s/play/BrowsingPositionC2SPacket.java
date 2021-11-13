package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;

import java.util.function.Supplier;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * Packet used to save the last browsing position for a dimension in an atlas.
 * @author Hunternif
 * @author Haven King
 */
public class BrowsingPositionC2SPacket extends C2SPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "browsing_position");

	int atlasID;
	ResourceKey<Level> world;
	int x;
	int y;
	double zoom;
	
	public BrowsingPositionC2SPacket(int atlasID, ResourceKey<Level> world, int x, int y, double zoom) {
		this.atlasID = atlasID;
		this.world = world;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
	}

	public static void encode(final BrowsingPositionC2SPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.world.location());
		packetBuffer.writeVarInt(msg.x);
		packetBuffer.writeVarInt(msg.y);
		packetBuffer.writeDouble(msg.zoom);
	}

	public static BrowsingPositionC2SPacket decode(final FriendlyByteBuf packetBuffer) {
		return new BrowsingPositionC2SPacket(
				packetBuffer.readVarInt(),
				ResourceKey.create(Registry.DIMENSION_REGISTRY, packetBuffer.readResourceLocation()),
				packetBuffer.readVarInt(),
				packetBuffer.readVarInt(),
				packetBuffer.readDouble());
	}

	public static void handle(final BrowsingPositionC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}
			if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(sender).contains(msg.atlasID)) {
				Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d",
						sender.createCommandSourceStack().getTextName(), msg.atlasID);
				return;
			}

			AntiqueAtlasMod.tileData.getData(msg.atlasID, sender.getCommandSenderWorld())
					.getWorldData(msg.world).setBrowsingPosition(msg.x, msg.y, msg.zoom);
		});
		context.setPacketHandled(true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
