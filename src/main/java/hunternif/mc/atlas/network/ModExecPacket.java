package hunternif.mc.atlas.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ModExecPacket extends ModPacket {

	/**
	 * Decode the packet data from the ByteBuf stream.
	 * @param buffer the buffer to decode from
	 */
	public abstract void decodeFrom(ByteBuf buffer);

	/**
	 * Handle a packet on the client side. This happens after decoding has completed.
	 * @param player the player reference
	 */
	public abstract void handleClientSide(EntityPlayer player);

	/**
	 * Handle a packet on the server side. This happens after decoding has completed.
	 * @param player the player reference
	 */
	public abstract void handleServerSide(EntityPlayer player);
	
	@Override
	public void handleClientSide(EntityPlayer player, ByteBuf buffer) {
		decodeFrom(buffer);
		handleClientSide(player);
	}
	
	@Override
	public void handleServerSide(EntityPlayer player, ByteBuf buffer) {
		decodeFrom(buffer);
		handleServerSide(player);
	}
}
