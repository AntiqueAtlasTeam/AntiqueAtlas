//package hunternif.mc.impl.atlas.ext;
//
//import java.util.Collection;
//import java.util.Map;
//
//import net.fabricmc.fabric.api.event.Event;
//import net.fabricmc.fabric.api.event.EventFactory;
//import net.minecraft.util.ResourceLocation;
//
///**
// * This event is dispatched on the client when it receives a
// * {@link hunternif.mc.impl.atlas.network.packet.s2c.play.TileNameS2CPacket} with registered tile ids.
// * @author Hunternif
// */
//public interface TileIdRegisteredCallback {
//	FIXME Event<TileIdRegisteredCallback> EVENT = EventFactory.createArrayBacked(TileIdRegisteredCallback.class,
//			(invokers) -> (nameToIdMap) -> {
//				for (TileIdRegisteredCallback callback : invokers) {
//					callback.onTileIDsReceived(nameToIdMap);
//				}
//			});
//
//	void onTileIDsReceived(Collection<ResourceLocation> ids);
//}
