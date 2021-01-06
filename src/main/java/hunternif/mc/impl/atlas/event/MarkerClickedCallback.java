//package hunternif.mc.impl.atlas.event;
//
//import hunternif.mc.impl.atlas.marker.Marker;
//import net.fabricmc.fabric.api.event.Event;
//import net.fabricmc.fabric.api.event.EventFactory;
//import net.minecraft.entity.player.PlayerEntity;
//
//@FunctionalInterface
//public interface MarkerClickedCallback {
//    FIXME Event<MarkerClickedCallback> EVENT = EventFactory.createArrayBacked(MarkerClickedCallback.class,
//            (invokers) -> (player, marker, mouseState) -> {
//                for (MarkerClickedCallback callback : invokers) {
//                    if (callback.onClicked(player, marker, mouseState)) {
//                        return true;
//                    }
//                }
//
//                return false;
//            });
//
//    boolean onClicked(PlayerEntity player, Marker marker, int mouseState);
//}
