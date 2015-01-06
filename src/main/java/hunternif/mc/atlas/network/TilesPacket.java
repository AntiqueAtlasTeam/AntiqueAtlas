package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 */
public class TilesPacket extends ModPacket {
	/** Size of ine entry in the map in bytes. */
	public static final int ENTRY_SIZE_BYTES = 2 + 2 + 2;

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
	public void encodeInto(ByteBuf buffer) {
		buffer.writeShort(dimension);
		buffer.writeShort(tileCount);
		buffer.writeBytes(tileData);
	}

	@Override
	public void handleServerSide(EntityPlayer player, ByteBuf buffer) {}
	
	@Override
	public void handleClientSide(EntityPlayer player, ByteBuf buffer) {
		dimension = buffer.readShort();
		int length = buffer.readShort();
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		for (int i = 0; i < length; i++) {
			data.setBiomeIdAt(dimension, buffer.readShort(), buffer.readShort(), buffer.readShort());
		}
	}
	
	@Override
	protected boolean isCompressed() {
		return true;
	}

}
