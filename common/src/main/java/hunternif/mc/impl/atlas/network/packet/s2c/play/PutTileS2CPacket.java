package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Puts biome tile into one atlas.
 *
 * @author Hunternif
 * @author Haven King
 */
public class PutTileS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "tile", "put");

    int atlasID;
    RegistryKey<World> world;
    int x;
    int z;
    Identifier tile;

    public PutTileS2CPacket(int atlasID, RegistryKey<World> world, int x, int z, Identifier tile) {
        this.atlasID = atlasID;
        this.world = world;
        this.x = x;
        this.z = z;
        this.tile = tile;
    }

    public PutTileS2CPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        x = buf.readVarInt();
        z = buf.readVarInt();
        tile = buf.readIdentifier();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.PUT_TILE;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(atlasID);
        buf.writeIdentifier(world.getValue());
        buf.writeVarInt(x);
        buf.writeVarInt(z);
        buf.writeIdentifier(tile);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, context.getPlayer().getEntityWorld());
            data.setTile(world, x, z, tile);
        });
    }
}
