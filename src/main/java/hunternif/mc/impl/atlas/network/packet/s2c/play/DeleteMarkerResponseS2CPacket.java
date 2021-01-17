package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Deletes a marker. A client sends a {@link hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket}
 * to the server as a request, and the server sends this back to all players as a response, including the
 * original sender.
 * @author Hunternif
 * @author Haven King
 */
public class DeleteMarkerResponseS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "marker", "delete");

	private static final int GLOBAL = -1;

	public DeleteMarkerResponseS2CPacket(int atlasID, int markerID) {
		this.writeVarInt(atlasID);
		this.writeVarInt(markerID);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int atlasID = buf.readVarInt();
		int markerID = buf.readVarInt();

		client.execute(() -> {
			MarkersData data = atlasID == GLOBAL ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.markersData.getMarkersData(atlasID, client.player.getEntityWorld());
			data.removeMarker(markerID);
		});
	}
}
