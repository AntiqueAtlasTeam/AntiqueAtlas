package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.util.ShortVec2;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

/**
 * User to sync atlas data from server to client.
 * TODO: proper multi-part packets.
 * @author Hunternif
 */
public class MapDataPacket extends ModPacket {
	/** Size of ine entry in the map in bytes. */
	public static final int ENTRY_SIZE_BYTES = 2 + 2 + 2;
	
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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeShort(atlasID);
		buffer.writeShort(dimension);
		buffer.writeShort(data.size());
		for (Entry<ShortVec2, MapTile> entry : data.entrySet()) {
			buffer.writeShort(entry.getKey().x);
			buffer.writeShort(entry.getKey().y);
			buffer.writeShort(entry.getValue().biomeID);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		atlasID = buffer.readShort();
		dimension = buffer.readShort();
		int length = buffer.readShort();
		for (int i = 0; i < length; i++) {
			ShortVec2 coords = new ShortVec2(buffer.readShort(), buffer.readShort());
			int biomeID = buffer.readShort();
			MapTile tile = new MapTile(biomeID);
			tile.randomizeTexture();
			data.put(coords, tile);
		}
	}
	
	@Override
	protected boolean isCompressed() {
		return true;
	}

	@Override
	public void handleServerSide(EntityPlayer player) {}
	
	@Override
	public void handleClientSide(EntityPlayer player) {
		AtlasData atlasData = AntiqueAtlasMod.itemAtlas.getClientAtlasData(atlasID);
		for (Entry<ShortVec2, MapTile> entry : data.entrySet()) {
			atlasData.putTile(dimension, entry.getKey(), entry.getValue());
		}
	}

}
