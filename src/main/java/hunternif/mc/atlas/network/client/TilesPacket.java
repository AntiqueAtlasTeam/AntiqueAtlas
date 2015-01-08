package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 */
public class TilesPacket implements IMessage {
	/** Size of one entry in the map in bytes. */
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
	public void fromBytes(ByteBuf buffer) {
		dimension = buffer.readShort();
		tileCount = buffer.readShort();
		tileData = buffer.readBytes(tileCount * ENTRY_SIZE_BYTES);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeShort(dimension);
		buffer.writeShort(tileCount);
		buffer.writeBytes(tileData);
	}

	public static class Handler extends AbstractMessageHandler<TilesPacket> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, TilesPacket msg, MessageContext ctx) {
			ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
			for (int i = 0; i < msg.tileCount; i++) {
				data.setBiomeIdAt(msg.dimension, msg.tileData.readShort(), msg.tileData.readShort(), msg.tileData.readShort());
			}
			return null;
		}

		@Override
		public IMessage handleServerMessage(EntityPlayer player, TilesPacket msg, MessageContext ctx) {
			return null;
		}
	}
}
