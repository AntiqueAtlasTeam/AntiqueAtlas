package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 *
 * @author Hunternif
 */
public class AddMarkerPacket {
    private int atlasID;
    private DimensionType dimension;
    private String type;
    private String label;
    private int x, y;
    private boolean visibleAhead;

    public AddMarkerPacket() {
    }

    /**
     * Use this constructor when creating a <b>local</b> marker.
     */
    public AddMarkerPacket(int atlasID, DimensionType dimension, String type, String label, int x, int y, boolean visibleAhead) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.type = type;
        this.label = label;
        this.x = x;
        this.y = y;
        this.visibleAhead = visibleAhead;
    }

    public static AddMarkerPacket read(PacketBuffer buffer) {
        return new AddMarkerPacket(buffer.readVarInt(), Registry.DIMENSION_TYPE.getByValue(buffer.readVarInt()), buffer.readString(512), buffer.readString(512), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
    }

    public static void write(AddMarkerPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeVarInt(Registry.DIMENSION_TYPE.getId(msg.dimension));
        buffer.writeString(msg.type);
        buffer.writeString(msg.label);
        buffer.writeInt(msg.x);
        buffer.writeInt(msg.y);
        buffer.writeBoolean(msg.visibleAhead);
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            // Make sure it's this player's atlas :^)
            if (SettingsConfig.itemNeeded && !AtlasAPI.getPlayerAtlases(player).contains(atlasID)) {
                Log.warn("Player %s attempted to put marker into someone else's Atlas #%d",
                        player.getCommandSource().getName(), atlasID);
                return;
            }
            MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, player.getEntityWorld());
            Marker marker = markersData.createAndSaveMarker(type, label, dimension, x, y, visibleAhead);
            // If these are a manually set markers sent from the client, forward
            // them to other players. Including the original sender, because he
            // waits on the server to verify his marker.
            MarkersPacket packetForClients = new MarkersPacket(atlasID, dimension, marker);
            PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), packetForClients);
        });
        ctx.get().setPacketHandled(true);
    }
}
