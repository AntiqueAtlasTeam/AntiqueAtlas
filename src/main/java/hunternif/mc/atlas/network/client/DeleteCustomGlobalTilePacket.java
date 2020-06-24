package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;

import java.io.IOException;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;


/**
 * Sent from server to client to remove a custom global tile.
 * @author Hunternif
 */
public class DeleteCustomGlobalTilePacket extends AbstractClientMessage<DeleteCustomGlobalTilePacket> {
	
	private DimensionType dimension;
	private int chunkX, chunkZ;

	public DeleteCustomGlobalTilePacket() {}
	
	public DeleteCustomGlobalTilePacket(DimensionType dimension, int chunkX, int chunkZ) {
		this.dimension = dimension;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	@Override
	protected void read(PacketByteBuf buffer) throws IOException {
		dimension = Registry.DIMENSION_TYPE.get(buffer.readVarInt());
		chunkX = buffer.readInt();
		chunkZ = buffer.readInt();
	}

	@Override
	protected void write(PacketByteBuf buffer) throws IOException {
		buffer.writeVarInt(Registry.DIMENSION_TYPE.getRawId(dimension));
		buffer.writeInt(chunkX);
		buffer.writeInt(chunkZ);
	}

	@Override
	protected void process(PlayerEntity player, EnvType side) {
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		data.removeBiomeAt(dimension, chunkX, chunkZ);
	}

}
