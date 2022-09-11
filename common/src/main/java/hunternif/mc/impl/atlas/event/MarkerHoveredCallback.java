package hunternif.mc.impl.atlas.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import hunternif.mc.impl.atlas.marker.Marker;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface MarkerHoveredCallback {
    Event<MarkerHoveredCallback> EVENT = EventFactory.createLoop();

    void onHovered(PlayerEntity player, Marker marker);
}
