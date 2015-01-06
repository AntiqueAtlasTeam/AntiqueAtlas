package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.util.ShortVec2;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Used to sync atlas data from server to client.
 * @author Hunternif
 */
public class MapDataPacket extends ModPacket {
	/** Size of one entry in the map in bytes. */
	public static final int ENTRY_SIZE_BYTES = 2 + 2 + 2;
	
	private int atlasID;
	private int dimension;
	private Map<ShortVec2, Tile> data;

	public MapDataPacket() {
		data = new HashMap<ShortVec2, Tile>();
	}
	
	public MapDataPacket(int atlasID, int dimension, Map<ShortVec2, Tile> data) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.data = data;
	}
	
	@Override
	public void encodeInto(ByteBuf buffer) {
		buffer.writeShort(atlasID);
		buffer.writeShort(dimension);
		buffer.writeShort(data.size());
		for (Entry<ShortVec2, Tile> entry : data.entrySet()) {
			buffer.writeShort(entry.getKey().x);
			buffer.writeShort(entry.getKey().y);
			buffer.writeShort(entry.getValue().biomeID);
		}
	}
	
	@Override
	protected boolean isCompressed() {
		return true;
	}

	@Override
	public void handleServerSide(EntityPlayer player, ByteBuf buffer) {}
	
	@Override
	public void handleClientSide(EntityPlayer player, ByteBuf buffer) {
		atlasID = buffer.readShort();
		dimension = buffer.readShort();
		int length = buffer.readShort();
		AtlasData atlasData = AntiqueAtlasMod.itemAtlas.getClientAtlasData(atlasID);
		for (int i = 0; i < length; i++) {
			int x = buffer.readShort();
			int y = buffer.readShort();
			int biomeID = buffer.readShort();
			Tile tile = new Tile(biomeID);
			tile.randomizeTexture();
			atlasData.setTile(dimension, x, y, tile);
		}
	}

}
