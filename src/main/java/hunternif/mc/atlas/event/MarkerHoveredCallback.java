package hunternif.mc.atlas.event;

import hunternif.mc.atlas.marker.Marker;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface MarkerHoveredCallback {
//    Event<MarkerHoveredCallback> EVENT = EventFactory.createArrayBacked(MarkerHoveredCallback.class,
//            (invokers) -> (player, marker) -> {
//                for (MarkerHoveredCallback callback : invokers) {
//                    callback.onHovered(player, marker);
//                }
//            });

    void onHovered(PlayerEntity player, Marker marker);
}
