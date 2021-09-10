package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileInfo;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DimensionUpdateS2CPacket extends S2CPacket {
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "dimension", "update");

    int atlasID;
    RegistryKey<World> world;
    Collection<TileInfo> tiles;

    public DimensionUpdateS2CPacket(int atlasID, RegistryKey<World> world, Collection<TileInfo> tiles) {
        this.atlasID = atlasID;
        this.world = world;
        this.tiles = tiles;
    }

    public DimensionUpdateS2CPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        int tileCount = buf.readVarInt();


        tiles = new ArrayList<>();
        for (int i = 0; i < tileCount; ++i) {
            tiles.add(new TileInfo(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readIdentifier())
            );
        }
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.DIMENSION_UPDATE;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(atlasID);
        buf.writeIdentifier(world.getValue());
        buf.writeVarInt(tiles.size());

        for (TileInfo tile : tiles) {
            buf.writeVarInt(tile.x);
            buf.writeVarInt(tile.z);
            buf.writeIdentifier(tile.id);
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            AtlasData data = AntiqueAtlasMod.tileData.getData(atlasID, context.getPlayer().getEntityWorld());

            for (TileInfo info : tiles) {
                data.getWorldData(world).setTile(info.x, info.z, info.id);
            }
        });
    }
}
