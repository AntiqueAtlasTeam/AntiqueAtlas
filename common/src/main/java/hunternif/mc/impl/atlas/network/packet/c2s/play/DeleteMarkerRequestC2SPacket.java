package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import hunternif.mc.impl.atlas.util.Log;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Deletes a marker. A client sends this packet to the server as a request,
 * and the server sends an  to all players as a response, including the
 * original sender.
 *
 * @author Hunternif
 */
public class DeleteMarkerRequestC2SPacket extends C2SPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "delete");

    private static final int GLOBAL = -1;

    int atlasID;
    int markerID;

    public DeleteMarkerRequestC2SPacket(int atlasID, int markerID) {
        this.atlasID = atlasID;
        this.markerID = markerID;
    }

    public DeleteMarkerRequestC2SPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        markerID = buf.readVarInt();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.DELETE_MARKER_REQUEST;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(atlasID);
        buf.writeVarInt(markerID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
                Log.warn("Player %s attempted to delete marker from someone else's Atlas #%d",
                        context.getPlayer().getName(), atlasID);
                return;
            }

            AtlasAPI.getMarkerAPI().deleteMarker(context.getPlayer().getEntityWorld(), atlasID, markerID);
        });
    }
}
