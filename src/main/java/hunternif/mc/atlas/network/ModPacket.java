package hunternif.mc.atlas.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Base class for all mod packets.
 * @author sirgingalot
 * @author Hunternif
 */
public abstract class ModPacket {
	/** This isn't a strict limitation, there is quite a bit of margin left. */
	public static final int MAX_SIZE_BYTES = 500000;
	
	/** Whether to zip this packet before sending. */
	protected boolean isCompressed() {
		return false;
	}
	
	/**
	 * Encode the packet data into the ByteBuf stream.
	 * @param buffer the buffer to encode into
	 */
	public abstract void encodeInto(ByteBuf buffer);

	/**
	 * Decode and handle a packet on the client side.
	 * @param player the player reference
	 * @param buffer the buffer to encode into
	 */
	public abstract void handleClientSide(EntityPlayer player, ByteBuf buffer);

	/**
	 * Decode and handle a packet on the server side.
	 * @param player the player reference
	 * @param buffer the buffer to decode from
	 */
	public abstract void handleServerSide(EntityPlayer player, ByteBuf buffer);
}