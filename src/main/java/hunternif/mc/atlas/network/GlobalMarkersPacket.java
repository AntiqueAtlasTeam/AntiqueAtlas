package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.marker.Marker;
import net.minecraft.entity.player.EntityPlayer;

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
	public void handleClientSide(EntityPlayer player) {
		for (Marker marker : markersByType.values()) {
			AntiqueAtlasMod.globalMarkersData.getData().putMarker(dimension, marker);
		}
	}
	
	@Override
	public void handleServerSide(EntityPlayer player) {
		for (Marker marker : markersByType.values()) {
			AntiqueAtlasMod.globalMarkersData.getData().putMarker(dimension, marker);
		}
		// If these are a manually set markers sent from the client, forward
		// them to other players. Including the original sender, because he
		// waits on the server to verify his marker.
		AntiqueAtlasMod.packetPipeline.sendToWorld(this, player.worldObj);
	}
}
