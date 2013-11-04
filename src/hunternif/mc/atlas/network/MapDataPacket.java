package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

public class MapDataPacket extends CustomPacket {
	public static final int ENTRY_SIZE_BYTES = 2 + 2 + 1;
	public static final int MAX_SIZE_BYTES = 32000;
	
	private int atlasID;
	private int dimension;
	private Map<ShortVec2, MapTile> data;

	public MapDataPacket() {
		data = new HashMap<ShortVec2, MapTile>();
	}
	
	public MapDataPacket(int atlasID, int dimension, Map<ShortVec2, MapTile> data) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.data = data;
	}
	
	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeShort(atlasID);
		out.writeShort(dimension);
		out.writeShort(data.size());
		for (Entry<ShortVec2, MapTile> entry : data.entrySet()) {
			out.writeShort(entry.getKey().x);
			out.writeShort(entry.getKey().y);
			out.writeByte(entry.getValue().biomeID);
		}
	}

	@Override
	public void read(ByteArrayDataInput in) throws ProtocolException {
		atlasID = in.readShort();
		dimension = in.readShort();
		int length = in.readShort();
		for (int i = 0; i < length; i++) {
			ShortVec2 coords = new ShortVec2(in.readShort(), in.readShort());
			byte biomeID = in.readByte();
			data.put(coords, new MapTile(biomeID));
		}
	}
	
	@Override
	protected boolean isCompressed() {
		return true;
	}

	@Override
	public void execute(EntityPlayer player, Side side) throws ProtocolException {
		if (side.isClient()) {
			AtlasData atlasData = AntiqueAtlasMod.itemAtlas.getClientAtlasData(atlasID);
			Map<ShortVec2, MapTile> clientSeenChunks = atlasData
					.getSeenChunksInDimension(dimension);
			for (Entry<ShortVec2, MapTile> entry : data.entrySet()) {
				atlasData.putTile(clientSeenChunks, entry.getKey(), entry.getValue());
			}
		} else {
			throw new ProtocolException("Cannot send this packet to the server!");
		}
	}

}
