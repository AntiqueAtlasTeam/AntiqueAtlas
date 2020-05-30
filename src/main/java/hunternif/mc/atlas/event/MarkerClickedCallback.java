package hunternif.mc.atlas.event;

import hunternif.mc.atlas.marker.Marker;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface MarkerClickedCallback {
//    Event<MarkerClickedCallback> EVENT = EventFactory.createArrayBacked(MarkerClickedCallback.class,
//            (invokers) -> (player, marker, mouseState) -> {
//                for (MarkerClickedCallback callback : invokers) {
//                    if (callback.onClicked(player, marker, mouseState)) {
//                        return true;
//                    }
//                }
//
//                return false;
//            });

    boolean onClicked(PlayerEntity player, Marker marker, int mouseState);
}
