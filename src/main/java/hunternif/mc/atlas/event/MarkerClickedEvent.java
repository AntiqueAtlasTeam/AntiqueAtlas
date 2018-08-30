package hunternif.mc.atlas.event;

import hunternif.mc.atlas.marker.Marker;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MarkerClickedEvent extends Event {

    public Marker marker;

    public MarkerClickedEvent(Marker marker) {
        this.marker = marker;
    }
}
