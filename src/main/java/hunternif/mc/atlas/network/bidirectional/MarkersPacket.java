package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessageHandler;
import hunternif.mc.atlas.network.PacketDispatcher;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Sends markers set via API from server to client, sends player-defined
 * markers from client to server. Only one dimension per packet.
 * @author Hunternif
 */
public class MarkersPacket implements IMessage {
	protected int atlasID;
	protected int dimension;
	protected final ListMultimap<String, Marker> markersByType = ArrayListMultimap.create();
	
	public MarkersPacket() {}
	
	public MarkersPacket(int atlasID, int dimension, Marker... markers) {
		this.atlasID = atlasID;
		this.dimension = dimension;
		for (Marker marker : markers) {
			markersByType.put(marker.getType(), marker);
		}
	}
	
	public MarkersPacket putMarker(Marker marker) {
		markersByType.put(marker.getType(), marker);
		return this;
	}

	public boolean isEmpty() {
		return markersByType.isEmpty();
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		atlasID = buffer.readShort();
		dimension = buffer.readShort();
		int typesLength = buffer.readShort();
		for (int i = 0; i < typesLength; i++) {
			String type = ByteBufUtils.readUTF8String(buffer);
			int markersLength = buffer.readShort();
			for (int j = 0; j < markersLength; j++) {
				Marker marker = new Marker(type, ByteBufUtils.readUTF8String(buffer), buffer.readInt(), buffer.readInt(), buffer.readBoolean());
				markersByType.put(type, marker);
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeShort(atlasID);
		buffer.writeShort(dimension);
		Set<String> types = markersByType.keySet();
		buffer.writeShort(types.size());
		for (String type : types) {
			ByteBufUtils.writeUTF8String(buffer, type);
			List<Marker> markers = markersByType.get(type);
			buffer.writeShort(markers.size());
			for (Marker marker : markers) {
				ByteBufUtils.writeUTF8String(buffer, marker.getLabel());
				buffer.writeInt(marker.getX());
				buffer.writeInt(marker.getY());
				buffer.writeBoolean(marker.isVisibleAhead());
			}
		}
	}

	public static class Handler extends AbstractMessageHandler<MarkersPacket> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, MarkersPacket msg, MessageContext ctx) {
			MarkersData markersData = AntiqueAtlasMod.itemAtlas.getClientMarkersData(msg.atlasID);
			for (Marker marker : msg.markersByType.values()) {
				markersData.putMarker(msg.dimension, marker);
			}
			if (Minecraft.getMinecraft().currentScreen instanceof GuiAtlas) {
				((GuiAtlas) Minecraft.getMinecraft().currentScreen).updateMarkerData();
			}
			return null;
		}
		
		@Override
		public IMessage handleServerMessage(EntityPlayer player, MarkersPacket msg, MessageContext ctx) {
			MarkersData markersData = AntiqueAtlasMod.itemAtlas.getMarkersData(msg.atlasID, player.worldObj);
			for (Marker marker : msg.markersByType.values()) {
				markersData.putMarker(msg.dimension, marker);
			}
			markersData.markDirty();
			// If these are a manually set markers sent from the client, forward
			// them to other players. Including the original sender, because he
			// waits on the server to verify his marker.
			PacketDispatcher.sendToAll(msg);
			return null;
		}
	}
}
