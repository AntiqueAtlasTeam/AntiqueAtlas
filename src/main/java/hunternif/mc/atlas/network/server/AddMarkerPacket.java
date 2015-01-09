package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessageHandler;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A request from a client to create a new marker.
 * @author Hunternif
 */
public class AddMarkerPacket implements IMessage {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;
	private int atlasID;
	private int dimension;
	private String type;
	private String label;
	private int x, y;
	private boolean visibleAhead;
	
	public AddMarkerPacket() {}
	
	/** Use this constructor when creating a <b>local</b> marker. */
	public AddMarkerPacket(int atlasID, int dimension, String type, String label, int x, int y, boolean visibleAhead) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		this.type = type;
		this.label = label;
		this.x = x;
		this.y = y;
		this.visibleAhead = visibleAhead;
	}
	/** Use this constructor when creating a <b>global</b> marker. */
	public AddMarkerPacket(int dimension, String type, String label, int x, int y, boolean visibleAhead) {
		this(GLOBAL, dimension, type, label, x, y, visibleAhead);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		atlasID = buffer.readShort();
		dimension = buffer.readShort();
		type = ByteBufUtils.readUTF8String(buffer);
		label = ByteBufUtils.readUTF8String(buffer);
		x = buffer.readInt();
		y = buffer.readInt();
		visibleAhead = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeShort(atlasID);
		buffer.writeShort(dimension);
		ByteBufUtils.writeUTF8String(buffer, type);
		ByteBufUtils.writeUTF8String(buffer, label);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeBoolean(visibleAhead);
	}

	public static class Handler extends AbstractMessageHandler<AddMarkerPacket> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, AddMarkerPacket msg, MessageContext ctx) {
			return null;
		}
		
		@Override
		public IMessage handleServerMessage(EntityPlayer player, AddMarkerPacket msg, MessageContext ctx) {
			MarkersData markersData = msg.atlasID == GLOBAL ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.itemAtlas.getMarkersData(msg.atlasID, player.worldObj);
			Marker marker = markersData.addMarker(msg.type, msg.label, msg.dimension, msg.x, msg.y, msg.visibleAhead);
			// If these are a manually set markers sent from the client, forward
			// them to other players. Including the original sender, because he
			// waits on the server to verify his marker.
			MarkersPacket packetForClients = msg.atlasID == GLOBAL ?
					new MarkersPacket(msg.dimension, marker) :
					new MarkersPacket(msg.atlasID, msg.dimension, marker);
			PacketDispatcher.sendToAll(packetForClients);
			return null;
		}
	}
}
