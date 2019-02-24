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
		if (!event.getWorld().G) {
			data = (ExtBiomeData) event.getWorld().a(ExtBiomeData.class, DATA_KEY);
			if (data == null) {
				data = new ExtBiomeData(DATA_KEY);
				data.markDirty();
				event.getWorld().a(DATA_KEY, data);
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
