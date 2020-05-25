package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.TileInfo;
import hunternif.mc.atlas.core.TileKind;
import hunternif.mc.atlas.core.TileKindFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Collection;
import java.util.function.Supplier;


public class DimensionUpdatePacket {
    /**
     * Size of one entry in the map in bytes.
     */
    private static final int ENTRY_SIZE_BYTES = 2 + 2 + 4;

    private int atlasID;
    private int dimensionId;
    private int tileCount;
    private ByteBuf tileData;

    public DimensionUpdatePacket() {
    }

    public DimensionUpdatePacket(int atlasID, DimensionType dimension) {
        this.atlasID = atlasID;
        this.dimensionId = Registry.DIMENSION_TYPE.getId(dimension);
        tileCount = 0;
        tileData = Unpooled.buffer();
    }

    public DimensionUpdatePacket(int atlasID, DimensionType dimension, Collection<TileInfo> tiles) {
        this(atlasID, dimension);
        //for (TileInfo i : tiles) {
        //    addTile(i.x, i.z, i.biome);
        //}
    }

    public DimensionUpdatePacket(int atlasID, int dimensionId, int tileCount, ByteBuf tileData) {
        this.atlasID = atlasID;
        this.dimensionId = dimensionId;
        this.tileCount = tileCount;
        this.tileData = tileData;
    }


    public DimensionUpdatePacket addTile(int x, int y, TileKind biomeID) {
        tileData.writeShort(x);
        tileData.writeShort(y);
        tileData.writeInt(biomeID.getId());
        tileCount++;
        return this;
    }

    public boolean isEmpty() {
        return tileCount == 0;
    }

    public static DimensionUpdatePacket read(PacketBuffer buffer) {
        int atlasID = buffer.readVarInt();
        int dimensionId = buffer.readVarInt();
        int tileCount = buffer.readVarInt();
        ByteBuf tileData = buffer.readBytes(tileCount * ENTRY_SIZE_BYTES);
        return new DimensionUpdatePacket(atlasID, dimensionId, tileCount, tileData);
    }

    public static void write(DimensionUpdatePacket msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.atlasID);
        buffer.writeVarInt(msg.dimensionId);
        buffer.writeVarInt(msg.tileCount);
        buffer.writeBytes(msg.tileData);
        // reset readerIndex, as this packet may gets send to multiple peers.
        msg.tileData.readerIndex(0);
    }

    public void process(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            //ServerPlayerEntity player = ctx.get().getSender();
            DimensionType dimension = Registry.DIMENSION_TYPE.getByValue(dimensionId);
            if (dimension == null) {
                // TODO FABRIC
                return;
            }

            AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, Minecraft.getInstance().player.world);
            for (int i = 0; i < tileCount; i++) {
                int x = tileData.readShort();
                int y = tileData.readShort();
                TileKind tile = TileKindFactory.get(tileData.readInt());
                data.getDimensionData(dimension).setTile(x, y, tile);

            }
            ctx.get().setPacketHandled(true);

        });
    }
}
