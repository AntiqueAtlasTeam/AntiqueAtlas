package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.TileGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;


public class TileGroupsPacket {

    public int atlasID;
    public DimensionType dimension;
    public ArrayList<TileGroup> tileGroups;

    public static final int TILE_GROUPS_PER_PACKET = 100;

    public TileGroupsPacket(ArrayList<TileGroup> tileGroups, int atlasID, DimensionType dimension) {
        this.tileGroups = tileGroups;
        this.atlasID = atlasID;
        this.dimension = dimension;
    }

    public static TileGroupsPacket read(PacketBuffer buffer) {
        int atlasID = buffer.readVarInt();
        DimensionType dimensionId = Registry.DIMENSION_TYPE.getByValue(buffer.readVarInt());
        int length = buffer.readVarInt();
        ArrayList<TileGroup> tileGroups = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            TileGroup newbie = new TileGroup(0, 0);
            newbie.readFromNBT(buffer.readCompoundTag());
            tileGroups.add(newbie);
        }
        return new TileGroupsPacket(tileGroups, atlasID, dimensionId);
    }

    public static void write(TileGroupsPacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeVarInt(Registry.DIMENSION_TYPE.getId(msg.dimension));
        buffer.writeVarInt(msg.tileGroups.size());
        for (TileGroup t : msg.tileGroups) {
            CompoundNBT me = new CompoundNBT();
            t.writeToNBT(me);
            buffer.writeCompoundTag(me);
        }
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //ServerPlayerEntity player = ctx.get().getSender();
            AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, Minecraft.getInstance().player.world);
            DimensionData dimData = atlasData.getDimensionData(dimension);
            for (TileGroup t : tileGroups) {
                dimData.putTileGroup(t);
            }
            ctx.get().setPacketHandled(true);
        });
    }
}
