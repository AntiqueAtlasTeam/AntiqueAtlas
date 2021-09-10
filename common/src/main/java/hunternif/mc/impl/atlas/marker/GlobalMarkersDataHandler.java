package hunternif.mc.impl.atlas.marker;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * Handles the world-saved data with global markers.
 * <p>
 * When in single player, this instance is shared between the client and the
 * server, so the packet-based synchronization becomes redundant.
 * </p>
 * <p>
 * When connecting to a remote server, data has to be reset, see
 * {@link #onClientConnectedToServer(boolean)}
 * </p>
 * @author Hunternif
 */
public class GlobalMarkersDataHandler {
	private static final String DATA_KEY = "aAtlasGlobalMarkers";

	private GlobalMarkersData data;

	public void onWorldLoad(ServerWorld world) {
		if (world.getRegistryKey() == World.OVERWORLD) {
			data = world.getPersistentStateManager().getOrCreate(() -> {
				GlobalMarkersData data = new GlobalMarkersData(DATA_KEY);
				data.markDirty();
				return data;
			}, DATA_KEY);
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
	public void onClientConnectedToServer(boolean isRemote) {
		if (isRemote) { // make sure it's not an integrated server
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
	public void onPlayerLogin(ServerPlayerEntity player) {
		data.syncOnPlayer(player);
	}

}
