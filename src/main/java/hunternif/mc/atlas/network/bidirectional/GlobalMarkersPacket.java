package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.network.AbstractMessageHandler;
import hunternif.mc.atlas.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Same as {@link MarkersPacket}, but the markers will appear in all atlases.
 * @author Hunternif
 */
public class GlobalMarkersPacket extends MarkersPacket {
	public GlobalMarkersPacket() {}

	public GlobalMarkersPacket(int dimension, Marker... markers) {
		super(0, dimension, markers);
	}

	public static class Handler extends AbstractMessageHandler<GlobalMarkersPacket> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage handleClientMessage(EntityPlayer player, GlobalMarkersPacket msg, MessageContext ctx) {
			for (Marker marker : msg.markersByType.values()) {
				AntiqueAtlasMod.globalMarkersData.getData().putMarker(msg.dimension, marker);
			}
			if (Minecraft.getMinecraft().currentScreen instanceof GuiAtlas) {
				((GuiAtlas) Minecraft.getMinecraft().currentScreen).updateMarkerData();
			}
			return null;
		}

		@Override
		public IMessage handleServerMessage(EntityPlayer player, GlobalMarkersPacket msg, MessageContext ctx) {
			for (Marker marker : msg.markersByType.values()) {
				AntiqueAtlasMod.globalMarkersData.getData().putMarker(msg.dimension, marker);
			}
			// If these are a manually set markers sent from the client, forward
			// them to other players. Including the original sender, because he
			// waits on the server to verify his marker.
			PacketDispatcher.sendToDimension(msg, player.worldObj.provider.dimensionId);
			return null;
		}
	}
}
