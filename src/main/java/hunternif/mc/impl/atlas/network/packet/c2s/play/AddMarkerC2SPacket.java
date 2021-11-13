package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;

import java.util.function.Supplier;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 * @author Hunternif
 * @author Haven King
 */
public class AddMarkerC2SPacket extends C2SPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "add");

	int atlasID; 
	ResourceLocation markerType; 
	int x, z; 
	boolean visibleBeforeDiscovery;
	Component label;


	public AddMarkerC2SPacket(int atlasID, ResourceLocation markerType, int x, int z, boolean visibleBeforeDiscovery, Component label) {
		this.atlasID = atlasID;
		this.markerType = markerType;
		this.x = x;
		this.z = z;
		this.visibleBeforeDiscovery = visibleBeforeDiscovery;
		this.label = label;
	}

	public static void encode(final AddMarkerC2SPacket msg, final FriendlyByteBuf packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeResourceLocation(msg.markerType);
		packetBuffer.writeVarInt(msg.x);
		packetBuffer.writeVarInt(msg.z);
		packetBuffer.writeBoolean(msg.visibleBeforeDiscovery);
		packetBuffer.writeComponent(msg.label);
	}

	public static AddMarkerC2SPacket decode(final FriendlyByteBuf packetBuffer) {
		return new AddMarkerC2SPacket(
				packetBuffer.readVarInt(), 
				packetBuffer.readResourceLocation(),
				packetBuffer.readVarInt(),
				packetBuffer.readVarInt(),
				packetBuffer.readBoolean(),
				packetBuffer.readComponent());
	}

	public static void handle(final AddMarkerC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayer sender = context.getSender();
			if (sender == null) {
				return;
			}
			ServerPlayer player = (ServerPlayer) context.getSender();
			if (!AtlasAPI.getPlayerAtlases(player).contains(msg.atlasID)) {
				AntiqueAtlasMod.LOG.warn(
								"Player {} attempted to put marker into someone else's Atlas #{}}",
						player.getName(), msg.atlasID);
				return;
			}

			AtlasAPI.getMarkerAPI().putMarker(player.level, msg.visibleBeforeDiscovery, msg.atlasID, msg.markerType, msg.label, msg.x,msg.z);
			});
		context.setPacketHandled(true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
