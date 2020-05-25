package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Deletes a marker. A client sends this packet to the server as a request,
 * and the server sends it back to all players as a response, including the
 * original sender.
 *
 * @author Hunternif
 */
public class DeleteMarkerPacket {
    /**
     * Used in place of atlasID to signify that the marker is global.
     */
    private static final int GLOBAL = -1;
    private int atlasID;
    private int markerID;

    public DeleteMarkerPacket() {
    }

    /**
     * Use this constructor when deleting a <b>local</b> marker.
     */
    public DeleteMarkerPacket(int atlasID, int markerID) {
        this.atlasID = atlasID;
        this.markerID = markerID;
    }

    /**
     * Use this constructor when deleting a <b>global</b> marker.
     */
    public DeleteMarkerPacket(int markerID) {
        this(GLOBAL, markerID);
    }

    public static DeleteMarkerPacket read(PacketBuffer buffer) {
        return new DeleteMarkerPacket(buffer.readVarInt(), buffer.readVarInt());
    }

    public static void write(DeleteMarkerPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeVarInt(msg.markerID);
    }

    private boolean isGlobal() {
        return atlasID == GLOBAL;
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Dist side = FMLEnvironment.dist;
            ServerPlayerEntity player = ctx.get().getSender();
            // do stuff
            if (side == Dist.DEDICATED_SERVER) {
                // Make sure it's this player's atlas :^)
                if (SettingsConfig.itemNeeded && !AtlasAPI.getPlayerAtlases(player).contains(atlasID)) {
                    Log.warn("Player %s attempted to delete marker from someone else's Atlas #%d",
                            player.getCommandSource().getName(), atlasID);
                    return;
                }
                if (isGlobal()) {
                    AtlasAPI.markers.deleteGlobalMarker(player.getEntityWorld(), markerID);
                } else {
                    AtlasAPI.markers.deleteMarker(player.getEntityWorld(), atlasID, markerID);
                }
            } else {
                MarkersData data = isGlobal() ?
                        AntiqueAtlasMod.globalMarkersData.getData() :
                        AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getEntityWorld());
                data.removeMarker(markerID);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
