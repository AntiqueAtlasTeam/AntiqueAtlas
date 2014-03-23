package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.util.NetworkUtil;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

/**
 * Same as {@link MarkersPacket}, but the markers will appear in all atlases.
 * @author Hunternif
 */
public class GlobalMarkersPacket extends MarkersPacket {
	public GlobalMarkersPacket() {}
	
	public GlobalMarkersPacket(int dimension, Marker... markers) {
		super(0, dimension, markers);
	}
	
	@Override
	public void execute(EntityPlayer player, Side side) throws ProtocolException {
		for (Marker marker : markersByType.values()) {
			AntiqueAtlasMod.globalMarkersData.getData().putMarker(dimension, marker);
		}
		if (side.isServer()) {
			// If these are a manually set markers sent from the client, forward
			// them to other players. Including the original sender, because he
			// waits on the server to verify his marker.
			NetworkUtil.sendPacketToAllPlayersInWorld(player.worldObj, this.makePacket());
		}
	}
}
