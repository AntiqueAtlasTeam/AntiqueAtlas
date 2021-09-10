package hunternif.mc.impl.atlas.network.packet.s2c.play;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import hunternif.mc.impl.atlas.registry.MarkerType;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

/**
 * Sends markers set via API from server to client.
 * Only one dimension per packet.
 * The markers in one packet are either all global or all local.
 *
 * @author Hunternif
 * @author Haven King
 */
public class MarkersS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "marker", "info");

    private static final int GLOBAL = -1;

    int atlasID;
    RegistryKey<World> world;
    Collection<Marker> markers;

    ListMultimap<Identifier, Marker.Precursor> markersByType;

    public MarkersS2CPacket(int atlasID, RegistryKey<World> world, Collection<Marker> markers) {
        this.atlasID = atlasID;
        this.world = world;
        this.markers = markers;
    }

    public MarkersS2CPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        int typesLength = buf.readVarInt();

        markersByType = ArrayListMultimap.create();

        for (int i = 0; i < typesLength; ++i) {
            Identifier type = buf.readIdentifier();
            int markersLength = buf.readVarInt();
            for (int j = 0; j < markersLength; ++j) {
                markersByType.put(type, new Marker.Precursor(buf));
            }
        }
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.MARKERS;
    }

    @Override
    public void write(PacketByteBuf buf) {
        ListMultimap<Identifier, Marker> markersByType = ArrayListMultimap.create();
        for (Marker marker : markers) {
            markersByType.put(marker.getType(), marker);
        }

        buf.writeVarInt(atlasID);
        buf.writeIdentifier(world.getValue());
        buf.writeVarInt(markersByType.keySet().size());

        for (Identifier type : markersByType.keySet()) {
            buf.writeIdentifier(type);
            List<Marker> markerList = markersByType.get(type);
            buf.writeVarInt(markerList.size());
            for (Marker marker : markerList) {
                marker.write(buf);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            MarkersData markersData = atlasID == GLOBAL
                    ? AntiqueAtlasMod.globalMarkersData.getData()
                    : AntiqueAtlasMod.markersData.getMarkersData(atlasID, context.getPlayer().getEntityWorld());

            for (Identifier type : markersByType.keys()) {
                MarkerType markerType = MarkerType.REGISTRY.get(type);
                for (Marker.Precursor precursor : markersByType.get(type)) {
                    markersData.loadMarker(new Marker(MarkerType.REGISTRY.getId(markerType), world, precursor));
                }
            }

            AntiqueAtlasModClient.getAtlasGUI().updateBookmarkerList();
        });
    }
}
