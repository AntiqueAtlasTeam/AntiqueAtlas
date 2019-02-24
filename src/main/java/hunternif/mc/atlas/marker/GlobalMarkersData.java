package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.registry.MarkerType;
import net.minecraft.entity.player.PlayerEntity;

/** Holds global markers, i.e. ones that appear in all atlases. */
public class GlobalMarkersData extends MarkersData {

	public GlobalMarkersData(String key) {
		super(key);
	}
	
	@Override
	public Marker createAndSaveMarker(String type, String label, int dimension, int x, int y, boolean visibleAhead) {
		return super.createAndSaveMarker(type, label, dimension, x, y, visibleAhead).setGlobal(true);
	}
	
	@Override
	public Marker loadMarker(Marker marker) {
		return super.loadMarker(marker).setGlobal(true);
	}
	
	/** Send all data to the player in several packets. */
    void syncOnPlayer(PlayerEntity player) {
		syncOnPlayer(-1, player);
	}
	
	@Override
	protected MarkersPacket newMarkersPacket(int atlasID, int dimension) {
		return new MarkersPacket(dimension);
	}

}
