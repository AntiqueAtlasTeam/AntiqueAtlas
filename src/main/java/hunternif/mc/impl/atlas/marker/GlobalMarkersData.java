package hunternif.mc.impl.atlas.marker;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

/** Holds global markers, i.e. ones that appear in all atlases. */
public class GlobalMarkersData extends MarkersData {

	public GlobalMarkersData(String key) {
		super(key);
	}
	
	@Override
	public Marker createAndSaveMarker(Identifier type, RegistryKey<World> world, int x, int y, boolean visibleAhead, Text label) {
		return super.createAndSaveMarker(type, world, x, y, visibleAhead, label).setGlobal(true);
	}
	
	@Override
	public Marker loadMarker(Marker marker) {
		return super.loadMarker(marker).setGlobal(true);
	}
	
	/** Send all data to the player in several packets. */
    void syncOnPlayer(ServerPlayerEntity player) {
		syncOnPlayer(-1, player);
	}
}
