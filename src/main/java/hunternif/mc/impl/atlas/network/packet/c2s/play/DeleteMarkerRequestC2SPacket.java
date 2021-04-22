package hunternif.mc.impl.atlas.network.packet.c2s.play;

import java.util.function.Supplier;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Deletes a marker. A client sends this packet to the server as a request,
 * and the server sends an  to all players as a response, including the
 * original sender.
 * @author Hunternif
 */
public class DeleteMarkerRequestC2SPacket extends C2SPacket {
	public static final ResourceLocation ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "delete");

	private static final int GLOBAL = -1;

	int atlasID, markerID;
	
	public DeleteMarkerRequestC2SPacket(int atlasID, int markerID) {
		this.atlasID = atlasID;
		this.markerID = markerID;
	}

	public static void encode(final DeleteMarkerRequestC2SPacket msg, final PacketBuffer packetBuffer) {
		packetBuffer.writeVarInt(msg.atlasID);
		packetBuffer.writeVarInt(msg.markerID);
	}

	public static DeleteMarkerRequestC2SPacket decode(final PacketBuffer packetBuffer) {
		return new DeleteMarkerRequestC2SPacket(
				packetBuffer.readVarInt(),
				packetBuffer.readVarInt());
	}

	public static void handle(final DeleteMarkerRequestC2SPacket msg, final Supplier<NetworkEvent.Context> contextSupplier) {
		final NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			final ServerPlayerEntity sender = context.getSender();
			if (sender == null) {
				return;
			}
			if (AntiqueAtlasConfig.itemNeeded.get() && !AtlasAPI.getPlayerAtlases(context.getSender()).contains(msg.atlasID)) {
				Log.warn("Player %s attempted to delete marker from someone else's Atlas #%d",
						context.getSender().getName(), msg.atlasID);
				return;
			}

			AtlasAPI.getMarkerAPI().deleteMarker(context.getSender().getEntityWorld(), msg.atlasID, msg.markerID);
		});
		context.setPacketHandled(true);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}
}
