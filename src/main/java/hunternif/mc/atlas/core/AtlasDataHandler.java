package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

/**
 * Provides access to {@link AtlasData}. Maintains a cache on the client side,
 * because WorldClient is reset along with all WorldSavedData when the player
 * changes dimension (fixes #67).
 * @author Hunternif
 */
public class AtlasDataHandler {
	private static final String ATLAS_DATA_PREFIX = "aAtlas_";
	
	private final Map<String, AtlasData> atlasDataClientCache = new ConcurrentHashMap<>();
	
	/** Loads data for the given atlas ID or creates a new one. */
	public AtlasData getAtlasData(ItemStack stack, World world) {
		if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
			return getAtlasData(stack.getItemDamage(), world);
		} else {
			return null;
		}
	}
	
	/** Loads data for the given atlas or creates a new one. */
	public AtlasData getAtlasData(int atlasID, World world) {
		String key = getAtlasDataKey(atlasID);
		AtlasData data = null;
		if (world.isRemote) {
			// Since atlas data doesn't really belong to a single world-dimension,
			// it can be cached. This should fix #67
			data = atlasDataClientCache.get(key);
		}
		if (data == null) {
			data = (AtlasData) world.loadData(AtlasData.class, key);
			if (data == null) {
				data = new AtlasData(key);
				world.setData(key, data);
			}
			if (world.isRemote) atlasDataClientCache.put(key, data);
		}
		return data;
	}
	
	private String getAtlasDataKey(int atlasID) {
		return ATLAS_DATA_PREFIX + atlasID;
	}
	
	/**
	 * This method resets the cache when the client loads a new world.
	 * It is required in order that old atlas data is not
	 * transferred from a previous world the client visited.
	 * <p>
	 * Using a "connect" event instead of "disconnect" because according to a
	 * form post, the latter event isn't actually fired on the client.
	 * </p>
	 */
	@SubscribeEvent
	public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
		atlasDataClientCache.clear();
	}
}
