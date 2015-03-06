package hunternif.mc.atlas.network.client;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.relauncher.Side;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;

/**
 * Sent from server to client to remove a custom global tile.
 * @author Hunternif
 */
public class DeleteCustomGlobalTilePacket extends AbstractClientMessage<DeleteCustomGlobalTilePacket> {
	
	private int dimension, chunkX, chunkZ;

	public DeleteCustomGlobalTilePacket() {}
	
	public DeleteCustomGlobalTilePacket(int dimension, int chunkX, int chunkZ) {
		this.dimension = dimension;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}
	
	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		dimension = buffer.readInt();
		chunkX = buffer.readInt();
		chunkZ = buffer.readInt();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeInt(dimension);
		buffer.writeInt(chunkX);
		buffer.writeInt(chunkZ);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		data.removeBiomeAt(dimension, chunkX, chunkZ);
	}

}
