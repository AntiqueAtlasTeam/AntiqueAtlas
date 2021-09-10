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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Packet used to save the last browsing position for a dimension in an atlas.
 *
 * @author Hunternif
 * @author Haven King
 */
public class BrowsingPositionC2SPacket extends C2SPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "browsing_position");

    int atlasID;
    RegistryKey<World> world;
    int x;
    int y;
    double zoom;

    public BrowsingPositionC2SPacket(int atlasID, RegistryKey<World> world, int x, int y, double zoom) {
        this.atlasID = atlasID;
        this.world = world;
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public BrowsingPositionC2SPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        x = buf.readVarInt();
        y = buf.readVarInt();
        zoom = buf.readDouble();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.BROWSING_POSITION;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(atlasID);
        buf.writeIdentifier(world.getValue());
        buf.writeVarInt(x);
        buf.writeVarInt(y);
        buf.writeDouble(zoom);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
                Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d",
                        context.getPlayer().getCommandSource().getName(), atlasID);
                return;
            }

            AntiqueAtlasMod.tileData.getData(atlasID, context.getPlayer().getEntityWorld())
                    .getWorldData(world).setBrowsingPosition(x, y, zoom);
        });
    }
}
