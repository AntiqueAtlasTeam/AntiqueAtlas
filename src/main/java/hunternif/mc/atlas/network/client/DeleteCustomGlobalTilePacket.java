package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


/**
 * Sent from server to client to remove a custom global tile.
 * @author Hunternif
 */
public class DeleteCustomGlobalTilePacket {
	
	private DimensionType dimension;
	private int chunkX, chunkZ;

	public DeleteCustomGlobalTilePacket() {}
	
	public DeleteCustomGlobalTilePacket(DimensionType dimension, int chunkX, int chunkZ) {
		this.dimension = dimension;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public static DeleteCustomGlobalTilePacket read(PacketBuffer buffer) {
		return new DeleteCustomGlobalTilePacket(Registry.DIMENSION_TYPE.getByValue(buffer.readVarInt()), buffer.readInt(), buffer.readInt());
	}

	public static void write(DeleteCustomGlobalTilePacket msg, PacketBuffer buffer)  {
		buffer.writeVarInt(Registry.DIMENSION_TYPE.getId(msg.dimension));
		buffer.writeInt(msg.chunkX);
		buffer.writeInt(msg.chunkZ);
	}

	public void process(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
			data.removeBiomeAt(dimension, chunkX, chunkZ);
		});
		ctx.get().setPacketHandled(true);
	}

}
