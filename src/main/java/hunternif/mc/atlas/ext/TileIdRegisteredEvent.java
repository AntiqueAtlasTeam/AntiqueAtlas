package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.network.client.TileNameIDPacket;

import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is dispatched on the client when it receives a
 * {@link TileNameIDPacket} with registered tile ids.
 * @author Hunternif
 */
public class TileIdRegisteredEvent extends Event {
	private final Map<ResourceLocation, Integer> nameToIdMap;

	public Map<ResourceLocation, Integer> getNameToIdMap() {
		return nameToIdMap;
	}

	public TileIdRegisteredEvent(Map<ResourceLocation, Integer> nameToIdMap) {
		this.nameToIdMap = nameToIdMap;
	}
//Event<TileIdRegisteredEvent> EVENT = EventFactory.createArrayBacked(TileIdRegisteredEvent.class,
	//		(invokers) -> (nameToIdMap) -> {
	//			for (TileIdRegisteredEvent callback : invokers) {
	//				callback.onTileIDsReceived(nameToIdMap);
	//			}
	//		});
	//
	//void onTileIDsReceived(Map<ResourceLocation, Integer> nameToIdMap);
}
