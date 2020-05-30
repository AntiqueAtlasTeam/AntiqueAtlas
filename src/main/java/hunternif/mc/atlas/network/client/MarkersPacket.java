package hunternif.mc.atlas.network.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Sends markers set via API from server to client.
 * Only one dimension per packet.
 * The markers in one packet are either all global or all local.
 *
 * @author Hunternif
 */
public class MarkersPacket {
    /**
     * Used in place of atlasID to signify that the marker is global.
     */
    private static final int GLOBAL = -1;
    private int atlasID;
    private DimensionType dimension;
    private final ListMultimap<String, Marker> markersByType = ArrayListMultimap.create();

    public MarkersPacket() {
    }

    /**
     * Use this constructor when creating a <b>local</b> marker.
     */
    public MarkersPacket(int atlasID, DimensionType dimension, Marker... markers) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        for (Marker marker : markers) {
            markersByType.put(marker.getType(), marker);
        }
    }

    /**
     * Use this constructor when creating a <b>global</b> marker.
     */
    public MarkersPacket(DimensionType dimension, Marker... markers) {
        this(GLOBAL, dimension, markers);
    }

    public MarkersPacket putMarker(Marker marker) {
        markersByType.put(marker.getType(), marker);
        return this;
    }

    private boolean isGlobal() {
        return atlasID == GLOBAL;
    }

    public static MarkersPacket read(PacketBuffer buffer) {
        int atlasID = buffer.readVarInt();
        DimensionType dimension = Registry.DIMENSION_TYPE.getByValue(buffer.readVarInt());
        MarkersPacket packet = new MarkersPacket(atlasID, dimension);
        int typesLength = buffer.readVarInt();
        for (int i = 0; i < typesLength; i++) {
            String type = buffer.readString(512);
            int markersLength = buffer.readVarInt();
            for (int j = 0; j < markersLength; j++) {
                Marker marker = new Marker(buffer.readVarInt(),
                        type, buffer.readString(512),
                        dimension, buffer.readInt(), buffer.readInt(),
                        buffer.readBoolean());
                packet.markersByType.put(type, marker);
            }
        }
        return packet;
    }

    public static void write(MarkersPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeVarInt(Registry.DIMENSION_TYPE.getId(msg.dimension));
        Set<String> types = msg.markersByType.keySet();
        buffer.writeVarInt(types.size());
        for (String type : types) {
            buffer.writeString(type);
            List<Marker> markers = msg.markersByType.get(type);
            buffer.writeVarInt(markers.size());
            for (Marker marker : markers) {
                buffer.writeVarInt(marker.getId());
                buffer.writeString(marker.getLabel());
                buffer.writeInt(marker.getX());
                buffer.writeInt(marker.getZ());
                buffer.writeBoolean(marker.isVisibleAhead());
            }
        }
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //ServerPlayerEntity player = ctx.get().getSender();
            MarkersData markersData = isGlobal() ?
                    AntiqueAtlasMod.globalMarkersData.getData() :
                    AntiqueAtlasMod.markersData.getMarkersData(atlasID, Minecraft.getInstance().player.getEntityWorld());
            for (Marker marker : markersByType.values()) {
                markersData.loadMarker(marker);
            }
            ctx.get().setPacketHandled(true);
        });
    }
}
