package hunternif.mc.impl.atlas.network.packet.s2c.play;

import dev.architectury.networking.NetworkManager;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerC2SPacket;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.swing.text.html.parser.Entity;

/**
 * Deletes a marker. A client sends a {@link DeleteMarkerC2SPacket}
 * to the server as a request, and the server sends this back to all players as a response, including the
 * original sender.
 * @author Hunternif
 * @author Haven King
 */
public class DeleteMarkerS2CPacket extends S2CPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "marker", "delete");

	private static final int GLOBAL = -1;

	public DeleteMarkerS2CPacket(int atlasID, int markerID) {
		this.writeVarInt(atlasID);
		this.writeVarInt(markerID);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static void apply(PacketByteBuf buf, NetworkManager.PacketContext context) {
		int atlasID = buf.readVarInt();
		int markerID = buf.readVarInt();

		context.queue(() -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			assert player != null;
			MarkersData data = atlasID == GLOBAL ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getEntityWorld());
			data.removeMarker(markerID);

			AntiqueAtlasModClient.getAtlasGUI().updateBookmarkerList();
		});
	}
}
