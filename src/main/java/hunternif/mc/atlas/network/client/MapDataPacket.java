package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.AbstractMessage.AbstractClientMessage;
import hunternif.mc.atlas.util.ShortVec2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.relauncher.Side;

/**
 * Used to sync atlas data from server to client.
 * @author Hunternif
 */
public class MapDataPacket extends AbstractClientMessage<MapDataPacket> {
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
	public void read(PacketBuffer buffer) throws IOException {
		atlasID = buffer.readVarIntFromBuffer();
		dimension = buffer.readVarIntFromBuffer();
		int length = buffer.readVarIntFromBuffer();
		for (int i = 0; i < length; i++) {
			int x = buffer.readShort();
			int y = buffer.readShort();
			int biomeID = buffer.readVarIntFromBuffer();
			Tile tile = new Tile(biomeID);
			tile.randomizeTexture();
			data.put(new ShortVec2(x, y), tile);
		}
	}

	@Override
	public void write(PacketBuffer buffer) throws IOException {
		buffer.writeVarIntToBuffer(atlasID);
		buffer.writeVarIntToBuffer(dimension);
		buffer.writeVarIntToBuffer(data.size());
		for (Entry<ShortVec2, Tile> entry : data.entrySet()) {
			buffer.writeShort(entry.getKey().x);
			buffer.writeShort(entry.getKey().y);
			buffer.writeVarIntToBuffer(entry.getValue().biomeID);
		}
	}

	@Override
	protected void process(EntityPlayer player, Side side) {
		AtlasData atlasData = AntiqueAtlasMod.itemAtlas.getAtlasData(atlasID, player.worldObj);
		for (Entry<ShortVec2, Tile> entry : data.entrySet()) {
			ShortVec2 v = entry.getKey();
			atlasData.setTile(dimension, v.x, v.y, entry.getValue());
		}
	}
}
