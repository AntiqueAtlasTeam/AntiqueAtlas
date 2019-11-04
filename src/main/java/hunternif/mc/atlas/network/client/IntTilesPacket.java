package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

/**
 * Used to sync custom tiles from server to client.
 *
 * Why do we have a "Short" and an "Int" version?
 * In the beginning there was only short. Then it turned out,
 * people actually do travel beyond coordinates 32000. Hence
 * Int became necessary. But we also keep short to support
 * slight version mismatch between client and server.
 */
public class IntTilesPacket extends AbstractClientMessage<IntTilesPacket> implements TilesPacket {
	/** Size of one entry in the map in bytes. */
	private static final int ENTRY_SIZE_BYTES = 4 + 4 + 2;

	private int dimension;
	private int tileCount;
	private ByteBuf tileData;

	public IntTilesPacket() {}

	public IntTilesPacket(int dimension) {
		this.dimension = dimension;
		tileCount = 0;
		tileData = Unpooled.buffer();
	}

	public void addTile(int x, int y, int biomeID) {
		tileData.writeInt(x);
		tileData.writeInt(y);
		tileData.writeShort(biomeID);
		tileCount++;
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
			data.setBiomeIdAt(dimension, tileData.readInt(), tileData.readInt(), tileData.readShort());
		}
	}
}
