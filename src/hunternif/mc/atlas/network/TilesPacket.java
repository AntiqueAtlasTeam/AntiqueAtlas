package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

/**
 * Used to sync custom tiles from server to client.
 * @author Hunternif
 */
public class TilesPacket extends CustomPacket {
	/** Size of ine entry in the map in bytes. */
	public static final int ENTRY_SIZE_BYTES = 2 + 2 + 2;

	private int dimension;
	private Map<ShortVec2, Integer> biomeMap = new HashMap<ShortVec2, Integer>();
	
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
	public void write(ByteArrayDataOutput out) {
		out.writeShort(dimension);
		out.writeShort(biomeMap.size());
		for (Entry<ShortVec2, Integer> entry : biomeMap.entrySet()) {
			out.writeShort(entry.getKey().x);
			out.writeShort(entry.getKey().y);
			out.writeShort(entry.getValue());
		}
	}

	@Override
	public void read(ByteArrayDataInput in) throws ProtocolException {
		dimension = in.readShort();
		int length = in.readShort();
		for (int i = 0; i < length; i++) {
			ShortVec2 coords = new ShortVec2(in.readShort(), in.readShort());
			biomeMap.put(coords, Integer.valueOf(in.readShort()));
		}
	}

	@Override
	public void execute(EntityPlayer player, Side side) throws ProtocolException {
		if (side.isClient()) {
			ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
			for (Entry<ShortVec2, Integer> entry : biomeMap.entrySet()) {
				data.setBiomeIdAt(dimension, entry.getValue(), entry.getKey());
			}
		} else {
			throw new ProtocolException("Cannot send TilePacket to the server!");
		}
	}
	
	@Override
	protected boolean isCompressed() {
		return true;
	}

}
