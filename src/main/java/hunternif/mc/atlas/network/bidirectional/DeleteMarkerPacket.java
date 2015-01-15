package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessageHandler;
import hunternif.mc.atlas.network.PacketDispatcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Deletes a marker. A client sends this packet to the server as a request,
 * and the server sends it back to all players as a response, including the
 * original sender.
 * @author Hunternif
 */
public class DeleteMarkerPacket implements IMessage {
	/** Used in place of atlasID to signify that the marker is global. */
	private static final int GLOBAL = -1;
	private int atlasID;
	private int markerID;
	
	public DeleteMarkerPacket() {}
	
	/** Use this constructor when deleting a <b>local</b> marker. */
	public DeleteMarkerPacket(int atlasID, int markerID) {
		this.atlasID = atlasID;
		this.markerID = markerID;
	}
	
	/** Use this constructor when deleting a <b>global</b> marker. */
	public DeleteMarkerPacket(int markerID) {
		this(GLOBAL, markerID);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer) {
		atlasID = buffer.readShort();
		markerID = buffer.readShort();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeShort(atlasID);
		buffer.writeShort(markerID);
	}
	
	public boolean isGlobal() {
		return atlasID == GLOBAL;
	}

	public static class Handler extends AbstractMessageHandler<DeleteMarkerPacket> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, DeleteMarkerPacket msg, MessageContext ctx) {
			MarkersData data = msg.isGlobal() ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.itemAtlas.getMarkersData(msg.atlasID, player.worldObj);
			data.removeMarker(msg.markerID);
			if (Minecraft.getMinecraft().currentScreen instanceof GuiAtlas) {
				((GuiAtlas) Minecraft.getMinecraft().currentScreen).updateMarkerData();
			}
			return null;
		}
		
		@Override
		public IMessage handleServerMessage(EntityPlayer player, DeleteMarkerPacket msg, MessageContext ctx) {
			// Make sure it's this player's atlas :^)
			if (!player.inventory.hasItemStack(new ItemStack(AntiqueAtlasMod.itemAtlas, 1, msg.atlasID))) {
				AntiqueAtlasMod.logger.warn(String.format("Player %s attempted to delete marker from someone else's Atlas #%d",
						player.getGameProfile().getName(), msg.atlasID));
				return null;
			}
			MarkersData data = msg.isGlobal() ?
					AntiqueAtlasMod.globalMarkersData.getData() :
					AntiqueAtlasMod.itemAtlas.getMarkersData(msg.atlasID, player.worldObj);
			data.removeMarker(msg.markerID);
			// If these are a manually set markers sent from the client, forward
			// them to other players. Including the original sender, because he
			// waits on the server to verify his marker.
			PacketDispatcher.sendToAll(msg);
			return null;
		}
	}
}
