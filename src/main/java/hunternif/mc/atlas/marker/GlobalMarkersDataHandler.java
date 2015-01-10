package hunternif.mc.atlas.marker;

import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * Handles the world-saved data with global markers.
 * @author Hunternif
 */
public class GlobalMarkersDataHandler {
	private static final String DATA_KEY = "aAtlasGlobalMarkers";
	
	private GlobalMarkersData data;
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote) {
			data = (GlobalMarkersData) event.world.loadItemData(GlobalMarkersData.class, DATA_KEY);
			if (data == null) {
				data = new GlobalMarkersData(DATA_KEY);
				data.markDirty();
				event.world.setItemData(DATA_KEY, data);
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		data = null;
	}
	
	public GlobalMarkersData getData() {
		if (data == null) { // This will happen on the client
			data = new GlobalMarkersData(DATA_KEY);
		}
		return data;
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		data.syncOnPlayer(event.player);
	}

}
