package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileDataStorage;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

/**
 * Used to sync custom tiles from server to client.
 *
 * @author Hunternif
 * @author Haven King
 */
public class CustomTileInfoS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "custom_tile", "info");

    RegistryKey<World> world;
    List<Map.Entry<ChunkPos, Identifier>> tiles;
    int chunkX;
    int chunkZ;
    Identifier tileId;

    public CustomTileInfoS2CPacket(RegistryKey<World> world, List<Map.Entry<ChunkPos, Identifier>> tiles) {
        this.world = world;
        this.tiles = tiles;
    }

    public CustomTileInfoS2CPacket(RegistryKey<World> world, int chunkX, int chunkZ, Identifier tileId) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.tileId = tileId;
    }

    public CustomTileInfoS2CPacket(PacketByteBuf buf) {
        world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        int tileCount = buf.readVarInt();

        TileDataStorage data = AntiqueAtlasMod.globalTileData.getData(world);
        for (int i = 0; i < tileCount; ++i) {
            data.setTile(buf.readVarInt(), buf.readVarInt(), buf.readIdentifier());
        }
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.CUSTOM_TILE_INFO;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(world.getValue());

        if (tiles != null) {
            buf.writeVarInt(tiles.size());

            for (Map.Entry<ChunkPos, Identifier> entry : tiles) {
                buf.writeVarInt(entry.getKey().x);
                buf.writeVarInt(entry.getKey().z);
                buf.writeIdentifier(entry.getValue());
            }
        } else {
        	buf.writeVarInt(1);
            buf.writeVarInt(chunkX);
            buf.writeVarInt(chunkZ);
            buf.writeIdentifier(tileId);
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        // Intentionally left blank. No synchronize required for this one.
    }
}
