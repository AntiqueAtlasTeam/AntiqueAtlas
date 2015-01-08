package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.AbstractMessageHandler;
import hunternif.mc.atlas.util.ShortVec2;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Used to sync atlas data from server to client.
 * @author Hunternif
 */
public class MapDataPacket implements IMessage {
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
	public void fromBytes(ByteBuf buffer) {
		atlasID = buffer.readShort();
		dimension = buffer.readShort();
		int length = buffer.readShort();
		for (int i = 0; i < length; i++) {
			int x = buffer.readShort();
			int y = buffer.readShort();
			int biomeID = buffer.readShort();
			Tile tile = new Tile(biomeID);
			tile.randomizeTexture();
			data.put(new ShortVec2(x, y), tile);
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeShort(atlasID);
		buffer.writeShort(dimension);
		buffer.writeShort(data.size());
		for (Entry<ShortVec2, Tile> entry : data.entrySet()) {
			buffer.writeShort(entry.getKey().x);
			buffer.writeShort(entry.getKey().y);
			buffer.writeShort(entry.getValue().biomeID);
		}
	}

	public static class Handler extends AbstractMessageHandler<MapDataPacket> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, MapDataPacket msg, MessageContext ctx) {
			AtlasData atlasData = AntiqueAtlasMod.itemAtlas.getClientAtlasData(msg.atlasID);
			for (Entry<ShortVec2, Tile> entry : msg.data.entrySet()) {
				ShortVec2 v = entry.getKey();
				atlasData.setTile(msg.dimension, v.x, v.y, entry.getValue());
			}
			return null;
		}

		@Override
		public IMessage handleServerMessage(EntityPlayer player, MapDataPacket msg, MessageContext ctx) {
			return null;
		}
	}
}
