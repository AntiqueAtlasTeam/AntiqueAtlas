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
 * Puts biome tile into one atlas. When sent to server, forwards it to every
 * client that has this atlas' data synced.
 *
 * @author Hunternif
 * @author Haven King
 */
public class PutTileC2SPacket extends C2SPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "put");

    int atlasID;
    int x;
    int z;
    Identifier tile;

    public PutTileC2SPacket(int atlasID, int x, int z, Identifier tile) {
        this.atlasID = atlasID;
        this.x = x;
        this.z = z;
        this.tile = tile;
    }

    public PutTileC2SPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        x = buf.readVarInt();
        z = buf.readVarInt();
        tile = buf.readIdentifier();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.PUT_TILE_C2S;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(atlasID);
        buf.writeVarInt(x);
        buf.writeVarInt(z);
        buf.writeIdentifier(tile);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (AntiqueAtlasMod.CONFIG.itemNeeded && !AtlasAPI.getPlayerAtlases(context.getPlayer()).contains(atlasID)) {
                Log.warn("Player %s attempted to modify someone else's Atlas #%d",
                        context.getPlayer().getName(), atlasID);
                return;
            }

            AtlasAPI.getTileAPI().putTile(context.getPlayer().getEntityWorld(), atlasID, tile, x, z);
        });
    }
}
