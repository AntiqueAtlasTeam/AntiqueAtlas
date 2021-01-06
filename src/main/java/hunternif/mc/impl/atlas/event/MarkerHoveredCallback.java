//package hunternif.mc.impl.atlas.event;
//
//import hunternif.mc.impl.atlas.marker.Marker;
//import net.fabricmc.fabric.api.event.Event;
//import net.fabricmc.fabric.api.event.EventFactory;
//import net.minecraft.entity.player.PlayerEntity;
//
//@FunctionalInterface
//public interface MarkerHoveredCallback {
//    FIXME Event<MarkerHoveredCallback> EVENT = EventFactory.createArrayBacked(MarkerHoveredCallback.class,
//            (invokers) -> (player, marker) -> {
//                for (MarkerHoveredCallback callback : invokers) {
//                    callback.onHovered(player, marker);
//                }
//            });
//
//    void onHovered(PlayerEntity player, Marker marker);
//}
