package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Deletes a marker. A client sends this packet to the server as a request,
 * and the server sends an  to all players as a response, including the
 * original sender.
 * @author Hunternif
 */
public class DeleteMarkerRequestC2SPacket extends C2SPacket {
	public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "delete");

	private static final int GLOBAL = -1;

	public DeleteMarkerRequestC2SPacket(int atlasID, int markerID) {
		this.writeVarInt(atlasID);
		this.writeVarInt(markerID);
	}

	@Override
	public Identifier getId() {
		return ID;
	}
	public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int atlasID = buf.readVarInt();
		int markerID = buf.readVarInt();

		server.execute(() -> {
			if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(player).contains(atlasID)) {
				Log.warn("Player %s attempted to delete marker from someone else's Atlas #%d",
						player.getName(), atlasID);
				return;
			}

			if (markerID == GLOBAL) {
				AtlasAPI.markers.deleteGlobalMarker(player.getEntityWorld(), markerID);
			} else {
				AtlasAPI.markers.deleteMarker(player.getEntityWorld(), atlasID, markerID);
			}
		});
	}
}
