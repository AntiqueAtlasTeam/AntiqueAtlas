package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.ZipUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

/**
 * @author credits to diesieben07
 */
public abstract class CustomPacket {
	private static final BiMap<Integer, Class<? extends CustomPacket>> idMap;
		
	static {
		ImmutableBiMap.Builder<Integer, Class<? extends CustomPacket>> builder = ImmutableBiMap.builder();
		
		builder.put(Integer.valueOf(0), MapDataPacket.class);
		builder.put(Integer.valueOf(1), TilePacket.class);
		
		idMap = builder.build();
	}

	public static CustomPacket constructPacket(int packetId)
			throws ProtocolException, InstantiationException, IllegalAccessException {
		Class<? extends CustomPacket> clazz = idMap.get(Integer.valueOf(packetId));
		if (clazz == null) {
			throw new ProtocolException("Unknown Packet Id!");
		} else {
			return clazz.newInstance();
		}
	}

	public static class ProtocolException extends Exception {
		public ProtocolException() {
		}
		public ProtocolException(String message, Throwable cause) {
			super(message, cause);
		}
		public ProtocolException(String message) {
			super(message);
		}
		public ProtocolException(Throwable cause) {
			super(cause);
		}
	}

	public final int getPacketId() {
		if (idMap.inverse().containsKey(getClass())) {
			return idMap.inverse().get(getClass()).intValue();
		} else {
			throw new RuntimeException("Packet " + getClass().getSimpleName() + " is missing a mapping!");
		}
	}
	
	public final Packet makePacket() {
		ByteArrayDataOutput dataOut = ByteStreams.newDataOutput();
		write(dataOut);
		byte[] data = dataOut.toByteArray();
		if (isCompressed()) {
			data = ZipUtil.compressByteArray(data);
		}
		ByteArrayDataOutput packetOut = ByteStreams.newDataOutput();
		packetOut.writeByte(getPacketId());
		packetOut.write(data);
		return PacketDispatcher.getPacket(AntiqueAtlasMod.CHANNEL, packetOut.toByteArray());
	}
	
	protected boolean isCompressed() {
		return false;
	}

	public abstract void write(ByteArrayDataOutput out);
	
	public abstract void read(ByteArrayDataInput in) throws ProtocolException;
	
	public abstract void execute(EntityPlayer player, Side side) throws ProtocolException;
}