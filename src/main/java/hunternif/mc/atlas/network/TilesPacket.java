package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.util.ShortVec2;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 */
public class TilesPacket extends ModPacket {
	/** Size of ine entry in the map in bytes. */
	public static final int ENTRY_SIZE_BYTES = 2 + 2 + 2;

	private int dimension;
	private final Map<ShortVec2, Integer> biomeMap = new HashMap<ShortVec2, Integer>();
	
	public TilesPacket() {}
	
	public TilesPacket(int dimension) {
		this.dimension = dimension;
	}
	
	public TilesPacket addTile(ShortVec2 coords, int biomeID) {
		biomeMap.put(coords, biomeID);
		return this;
	}
	public boolean isEmpty() {
		return biomeMap.isEmpty();
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeShort(dimension);
		buffer.writeShort(biomeMap.size());
		for (Entry<ShortVec2, Integer> entry : biomeMap.entrySet()) {
			buffer.writeShort(entry.getKey().x);
			buffer.writeShort(entry.getKey().y);
			buffer.writeShort(entry.getValue());
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		dimension = buffer.readShort();
		int length = buffer.readShort();
		for (int i = 0; i < length; i++) {
			ShortVec2 coords = new ShortVec2(buffer.readShort(), buffer.readShort());
			biomeMap.put(coords, Integer.valueOf(buffer.readShort()));
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {}
	
	@Override
	public void handleClientSide(EntityPlayer player) {
		ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
		for (Entry<ShortVec2, Integer> entry : biomeMap.entrySet()) {
			data.setBiomeIdAt(dimension, entry.getValue(), entry.getKey());
		}
	}
	
	@Override
	protected boolean isCompressed() {
		return true;
	}

}
