package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Packet used to save the last browsing position for a dimension in an atlas.
 *
 * @author Hunternif
 */
public class BrowsingPositionPacket {
    public static final double ZOOM_SCALE_FACTOR = 1024;

    private int atlasID;
    private DimensionType dimension;
    private int x, y;
    private double zoom;

    public BrowsingPositionPacket() {
    }

    public BrowsingPositionPacket(int atlasID, DimensionType dimension, int x, int y, double zoom) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public static BrowsingPositionPacket read(PacketBuffer buffer) {
        return new BrowsingPositionPacket(buffer.readVarInt(), Registry.DIMENSION_TYPE.getByValue(buffer.readVarInt()), buffer.readVarInt(), buffer.readVarInt(), (double) buffer.readVarInt() / ZOOM_SCALE_FACTOR);
    }

    public static void write(BrowsingPositionPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeVarInt(Registry.DIMENSION_TYPE.getId(msg.dimension));
        buffer.writeVarInt(msg.x);
        buffer.writeVarInt(msg.y);
        buffer.writeVarInt((int) Math.round(msg.zoom * ZOOM_SCALE_FACTOR));
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            // Make sure it's this player's atlas :^)
            if (SettingsConfig.itemNeeded && !AtlasAPI.getPlayerAtlases(player).contains(atlasID)) {
                Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d",
                        player.getCommandSource().getName(), atlasID);
                return;
            }

            AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.getEntityWorld())
                    .getDimensionData(dimension).setBrowsingPosition(x, y, zoom);
        });
        ctx.get().setPacketHandled(true);
    }
}
