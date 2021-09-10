package hunternif.mc.impl.atlas.event;

import hunternif.mc.impl.atlas.marker.Marker;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface MarkerHoveredCallback {
    Event<MarkerHoveredCallback> EVENT = EventFactory.createLoop();

    void onHovered(PlayerEntity player, Marker marker);
}
