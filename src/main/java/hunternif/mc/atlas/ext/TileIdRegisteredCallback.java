package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.network.client.TileNameIDPacket;

import java.util.Map;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

/**
 * This event is dispatched on the client when it receives a
 * {@link TileNameIDPacket} with registered tile ids.
 * @author Hunternif
 */
public interface TileIdRegisteredCallback {
	Event<TileIdRegisteredCallback> EVENT = EventFactory.createArrayBacked(TileIdRegisteredCallback.class,
			(invokers) -> (nameToIdMap) -> {
				for (TileIdRegisteredCallback callback : invokers) {
					callback.onTileIDsReceived(nameToIdMap);
				}
			});

	void onTileIDsReceived(Map<Identifier, Integer> nameToIdMap);
}
