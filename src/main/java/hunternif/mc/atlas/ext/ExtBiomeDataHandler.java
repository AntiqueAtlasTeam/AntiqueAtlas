package hunternif.mc.atlas.ext;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ExtBiomeDataHandler {
	private static final String DATA_KEY = "aAtlasExtTiles";
	
	private ExtBiomeData data;
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote) {
			data = (ExtBiomeData) event.getWorld().loadData(ExtBiomeData.class, DATA_KEY);
			if (data == null) {
				data = new ExtBiomeData(DATA_KEY);
				data.markDirty();
				event.getWorld().setData(DATA_KEY, data);
			}
		}
	}
	
	public ExtBiomeData getData() {
		if (data == null) { // This will happen on the client
			data = new ExtBiomeData(DATA_KEY);
		}
		return data;
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		ExtTileIdMap.instance().syncOnPlayer(event.player);
		data.syncOnPlayer(event.player);
	}

}
