package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Deletes a marker. A client sends a {@link hunternif.mc.impl.atlas.network.packet.c2s.play.DeleteMarkerRequestC2SPacket}
 * to the server as a request, and the server sends this back to all players as a response, including the
 * original sender.
 *
 * @author Hunternif
 * @author Haven King
 */
public class DeleteMarkerResponseS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "marker", "delete");

    private static final int GLOBAL = -1;

    int atlasID;
    int markerID;

    public DeleteMarkerResponseS2CPacket(int atlasID, int markerID) {
        this.atlasID = atlasID;
        this.markerID = markerID;
    }

    public DeleteMarkerResponseS2CPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        markerID = buf.readVarInt();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.DELETE_MARKER_RESPONSE;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(atlasID);
        buf.writeVarInt(markerID);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            MarkersData data = atlasID == GLOBAL ?
                    AntiqueAtlasMod.globalMarkersData.getData() :
                    AntiqueAtlasMod.markersData.getMarkersData(atlasID, context.getPlayer().getEntityWorld());
            data.removeMarker(markerID);

            AntiqueAtlasModClient.getAtlasGUI().updateBookmarkerList();
        });
    }
}
