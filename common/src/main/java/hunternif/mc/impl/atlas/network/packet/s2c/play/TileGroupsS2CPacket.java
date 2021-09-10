package hunternif.mc.impl.atlas.network.packet.s2c.play;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileGroup;
import hunternif.mc.impl.atlas.core.WorldData;
import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import hunternif.mc.impl.atlas.network.packet.s2c.S2CPacket;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


/**
 * Syncs tile groups to the client.
 *
 * @author Hunternif
 * @author Haven King
 */
public class TileGroupsS2CPacket extends S2CPacket {
    public static final int TILE_GROUPS_PER_PACKET = 100;
    public static final Identifier ID = AntiqueAtlasMod.id("packet", "s2c", "tile", "groups");

    int atlasID;
    RegistryKey<World> world;
    List<TileGroup> tileGroups;

    public TileGroupsS2CPacket(int atlasID, RegistryKey<World> world, List<TileGroup> tileGroups) {
        this.atlasID = atlasID;
        this.world = world;
        this.tileGroups = tileGroups;
    }

    public TileGroupsS2CPacket(PacketByteBuf buf) {
        atlasID = buf.readVarInt();
        world = RegistryKey.of(Registry.DIMENSION, buf.readIdentifier());
        int length = buf.readVarInt();
        tileGroups = new ArrayList<>(length);

        for (int i = 0; i < length; ++i) {
            CompoundTag tag = buf.readCompoundTag();

            if (tag != null) {
                tileGroups.add(new TileGroup().readFromNBT(tag));
            }
        }
    }

    @Override
    public MessageType getType() {
        return AntiqueAtlasNetworking.TILE_GROUPS;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(atlasID);
        buf.writeIdentifier(world.getValue());
        buf.writeVarInt(tileGroups.size());

        for (TileGroup tileGroup : tileGroups) {
            buf.writeCompoundTag(tileGroup.writeToNBT(new CompoundTag()));
        }
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, context.getPlayer().world);
            WorldData dimData = atlasData.getWorldData(world);
            for (TileGroup t : tileGroups) {
                dimData.putTileGroup(t);
            }
        });
    }
}
