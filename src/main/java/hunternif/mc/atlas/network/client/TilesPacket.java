package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 */
public class TilesPacket {
	/** Size of one entry in the map in bytes. */
	private static final int ENTRY_SIZE_BYTES = 2 + 2 + 4;

	private DimensionType dimension;
	private int tileCount;
	private ByteBuf tileData;

	public TilesPacket() {}

	public TilesPacket(DimensionType dimension) {
		this.dimension = dimension;
		tileCount = 0;
		tileData = Unpooled.buffer();
	}

	public TilesPacket(DimensionType dimension, int tileCount, ByteBuf tileData) {
		this.dimension = dimension;
		this.tileCount = tileCount;
		this.tileData = tileData;
	}

	public TilesPacket addTile(int x, int y, int biomeID) {
		tileData.writeInt(x);
		tileData.writeInt(y);
		tileData.writeInt(biomeID);
		tileCount++;
		return this;
	}

	public boolean isEmpty() {
		return tileCount == 0;
	}

	public static TilesPacket read(PacketBuffer buffer) {
		DimensionType dimension = Registry.DIMENSION_TYPE.getByValue(buffer.readVarInt());
		int tileCount = buffer.readVarInt();
		ByteBuf tileData = buffer.readBytes(tileCount * ENTRY_SIZE_BYTES);
		return new TilesPacket(dimension, tileCount, tileData);
	}

	public static void write(TilesPacket msg, PacketBuffer buffer)  {
		buffer.writeVarInt(Registry.DIMENSION_TYPE.getId(msg.dimension));
		buffer.writeVarInt(msg.tileCount);
		buffer.writeBytes(msg.tileData);
	}

	public void process(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (dimension == null) {
				// TODO FABRIC
				return;
			}

			ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
			for (int i = 0; i < tileCount; i++) {
				data.setBiomeAt(dimension, tileData.readInt(), tileData.readInt(), tileData.readInt());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
