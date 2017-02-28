package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 */
public class TilesPacket extends AbstractClientMessage<TilesPacket> {
	/** Size of one entry in the map in bytes. */
	private static final int ENTRY_SIZE_BYTES = 2 + 2 + 2;

	private int dimension;
	private int tileCount;
	private ByteBuf tileData;

	public TilesPacket() {}

	public TilesPacket(int dimension) {
		this.dimension = dimension;
		tileCount = 0;
		tileData = Unpooled.buffer();
	}

	public TilesPacket addTile(int x, int y, int biomeID) {
		tileData.writeShort(x);
		tileData.writeShort(y);
		tileData.writeShort(biomeID);
		tileCount++;
		return this;
	}

	public boolean isEmpty() {
		return tileCount == 0;
	}

	@Override
	public void read(PacketBuffer buffer) throws IOException {
		dimension = buffer.readVarInt();
		tileCount = buffer.readVarInt();
		tileData = buffer.readBytes(tileCount * ENTRY_SIZE_BYTES);
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarInt(dimension);
		buffer.writeVarInt(tileCount);
		buffer.writeBytes(tileData);
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		for (int i = 0; i < tileCount; i++) {
			data.setBiomeIdAt(dimension, tileData.readShort(), tileData.readShort(), tileData.readShort());
		}
	}
}
