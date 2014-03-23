package hunternif.mc.atlas.marker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.IPlayerTracker;

/**
 * Handles the world-saved data with global markers.
 * @author Hunternif
 */
public class GlobalMarkersDataHandler implements IPlayerTracker {
	private static final String DATA_KEY = "aAtlasGlobalMarkers";
	
	private GlobalMarkersData data;
	
	@ForgeSubscribe
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
	
	public GlobalMarkersData getData() {
		if (data == null) { // This will happen on the client
			data = new GlobalMarkersData(DATA_KEY);
		}
		return data;
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		data.syncOnPlayer(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {}

}
