package hunternif.mc.impl.atlas.event;

import hunternif.mc.impl.atlas.marker.Marker;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import me.shedaniel.architectury.event.EventResult;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface MarkerClickedCallback {
    Event<MarkerClickedCallback> EVENT = EventFactory.createEventResult();

    EventResult onClicked(PlayerEntity player, Marker marker, int mouseState);
}
