package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.network.bidirectional.GlobalMarkersPacket;
import hunternif.mc.atlas.network.bidirectional.MarkersPacket;
import net.minecraft.entity.player.EntityPlayer;

/** Holds global markers, i.e. ones that appear in all atlases. */
public class GlobalMarkersData extends MarkersData {

	public GlobalMarkersData(String key) {
		super(key);
	}
	
	/** Send all data to the player in several packets. */
	protected void syncOnPlayer(EntityPlayer player) {
		syncOnPlayer(0, player);
	}
	
	@Override
	protected MarkersPacket newMarkersPacket(int atlasID, int dimension) {
		return new GlobalMarkersPacket(dimension);
	}

}
