package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
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
 * Sent from server to client to remove a custom global tile.
 *
 * @author Hunternif
 * @author Haven King
 */
public class DeleteCustomGlobalTileS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "c2s", "tile", "delete");

    RegistryKey<World> world;
    int chunkX;
    int chunkZ;

    public DeleteCustomGlobalTileS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public DeleteCustomGlobalTileS2CPacket(PacketByteBuf buf) {
        world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        chunkX = buf.readVarInt();
        chunkZ = buf.readVarInt();
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.DELETE_CUSTOM_GLOBAL_TILE;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(world.getValue());
        buf.writeVarInt(chunkX);
        buf.writeVarInt(chunkZ);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
            // TODO why is this scheduled? The map is threadsafe.
            data.removeTile(chunkX, chunkZ);
        });
    }
}
