package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.TileKind;
import hunternif.mc.atlas.core.TileKindFactory;
import hunternif.mc.atlas.util.Log;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Puts biome tile into one atlas. When sent to server, forwards it to every
 * client that has this atlas' data synced.
 *
 * @author Hunternif
 */
public class PutTilePacket {
    private int atlasID, x, z;
    private DimensionType dimension;
    private TileKind kind;

    public PutTilePacket() {
    }

    public PutTilePacket(int atlasID, DimensionType dimension, int x, int z, TileKind kind) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.x = x;
        this.z = z;
        this.kind = kind;
    }

    public static PutTilePacket read(PacketBuffer buffer) {
        return new PutTilePacket(buffer.readVarInt(), Registry.DIMENSION_TYPE.getByValue(buffer.readVarInt()), buffer.readVarInt(), buffer.readVarInt(), TileKindFactory.get(buffer.readVarInt()));
    }

    public static void write(PutTilePacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeVarInt(Registry.DIMENSION_TYPE.getId(msg.dimension));
        buffer.writeVarInt(msg.x);
        buffer.writeVarInt(msg.z);
        buffer.writeVarInt(msg.kind.getId());
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Dist side = FMLEnvironment.dist;
            ServerPlayerEntity player = ctx.get().getSender();
            if (side == Dist.DEDICATED_SERVER) {
                // Make sure it's this player's atlas :^)
                if (SettingsConfig.itemNeeded && !AtlasAPI.getPlayerAtlases(player).contains(atlasID)) {
                    Log.warn("Player %s attempted to modify someone else's Atlas #%d",
                            player.getCommandSource().getName(), atlasID);
                    return;
                }
                if (kind.getId() >= 0) {
                    AtlasAPI.tiles.putBiomeTile(player.getEntityWorld(), atlasID, kind.getBiome(), x, z);
                } else {
                    AtlasAPI.tiles.putCustomTile(player.getEntityWorld(), atlasID, kind.getExtTile(), x, z);
                }
            } else {
                AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, player.getEntityWorld());
                data.setTile(dimension, x, z, kind);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
