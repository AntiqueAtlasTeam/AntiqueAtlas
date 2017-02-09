package hunternif.mc.atlas.marker;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

/**
 * Handles the world-saved data with global markers.
 * <p>
 * When in single player, this instance is shared between the client and the
 * server, so the packet-based synchronization becomes redundant.
 * </p>
 * <p>
 * When connecting to a remote server, data has to be reset, see
 * {@link #onClientConnectedToServer(ClientConnectedToServerEvent)}
 * </p>
 * @author Hunternif
 */
public class GlobalMarkersDataHandler {
	private static final String DATA_KEY = "aAtlasGlobalMarkers";
	
	private GlobalMarkersData data;
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
			data = (GlobalMarkersData) event.getWorld().loadData(GlobalMarkersData.class, DATA_KEY);
			if (data == null) {
				data = new GlobalMarkersData(DATA_KEY);
				data.markDirty();
				event.getWorld().setData(DATA_KEY, data);
			}
		}
	}
	
	/**
	 * This method sets {@link #data} to null when the client connects to a
	 * remote server. It is required in order that global markers data is not
	 * transferred from a previous world the client visited.
	 * <p>
	 * Using a "connect" event instead of "disconnect" because according to a
	 * form post, the latter event isn't actually fired on the client.
	 * </p>
	 */
	@SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
		if (!event.isLocal()) { // make sure it's not an integrated server
			data = null;
		}
	}
	
	public GlobalMarkersData getData() {
		if (data == null) { // This will happen on the client
			data = new GlobalMarkersData(DATA_KEY);
		}
		return data;
	}
	
	/** Synchronizes global markers with the connecting client. */
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		data.syncOnPlayer(event.player);
	}

}
