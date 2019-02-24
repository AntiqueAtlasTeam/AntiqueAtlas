package hunternif.mc.atlas.marker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

/**
 * Provides access to {@link MarkersData}. Maintains a cache on the client side,
 * because WorldClient is reset along with all WorldSavedData when the player
 * changes dimension (fixes #67).
 * @author Hunternif
 */
public class MarkersDataHandler {
	private static final String MARKERS_DATA_PREFIX = "aaMarkers_";
	
	private final Map<String, MarkersData> markersDataClientCache = new ConcurrentHashMap<>();
	
	/** Loads data for the given atlas or creates a new one. */
	public MarkersData getMarkersData(ItemStack stack, World world) {
		if (stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
			return getMarkersData(stack.getDamage(), world);
		} else {
			return null;
		}
	}
	
	/** Loads data for the given atlas ID or creates a new one. */
	public MarkersData getMarkersData(int atlasID, World world) {
		String key = getMarkersDataKey(atlasID);
		if (world.isClient) {
			// Since atlas data doesn't really belong to a single world-dimension,
			// it can be cached. This should fix #67
			return markersDataClientCache.computeIfAbsent(key, MarkersData::new);
		} else {
			PersistentStateManager manager = ((ServerWorld) world).getPersistentStateManager();
			return manager.getOrCreate(() -> new MarkersData(key), key);
		}
	}
	
	private String getMarkersDataKey(int atlasID) {
		return MARKERS_DATA_PREFIX + atlasID;
	}
	
	/**
	 * This method resets the cache when the client loads a new world.
	 * It is required in order that old markers data is not
	 * transferred from a previous world the client visited.
	 * <p>
	 * Using a "connect" event instead of "disconnect" because according to a
	 * form post, the latter event isn't actually fired on the client.
	 * </p>
	 */
	// TODO FABRIC
	/* @SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
		markersDataClientCache.clear();
	} */
}
