package hunternif.mc.impl.atlas.network.packet.c2s.play;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.c2s.C2SPacket;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * A request from a client to create a new marker. In order to prevent griefing,
 * the marker has to be local.
 *
 * @author Hunternif
 * @author Haven King
 */
public class AddMarkerC2SPacket extends C2SPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "marker", "add");

    int atlasID;
    Identifier markerType;
    int x;
    int z;
    boolean visibleBeforeDiscovery;
    Text label;

    public AddMarkerC2SPacket(int atlasID, Identifier markerType, int x, int z, boolean visibleBeforeDiscovery, Text label) {
        this.atlasID = atlasID;
        this.markerType = markerType;
        this.x = x;
        this.z = z;
        this.visibleBeforeDiscovery = visibleBeforeDiscovery;
        this.label = label;
    }

    public AddMarkerC2SPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        markerType = buf.readIdentifier();
        x = buf.readVarInt();
        z = buf.readVarInt();
        visibleBeforeDiscovery = buf.readBoolean();
        label = buf.readText();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.ADD_MARKER;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(atlasID);
        buf.writeIdentifier(markerType);
        buf.writeVarInt(x);
        buf.writeVarInt(z);
        buf.writeBoolean(visibleBeforeDiscovery);
        buf.writeText(label);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (!AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
                AntiqueAtlasMod.LOG.warn(
                        "Player {} attempted to put marker into someone else's Atlas #{}}",
                        context.getPlayer().getName(), atlasID);
                return;
            }

            AtlasAPI.getMarkerAPI().putMarker(context.getPlayer().world, visibleBeforeDiscovery, atlasID, markerType, label, x, z);
        });
    }
}
