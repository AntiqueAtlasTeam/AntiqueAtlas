package hunternif.mc.atlas.event;


import hunternif.mc.atlas.marker.Marker;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MarkerHoveredEvent extends Event {

    public Marker marker;

    public MarkerHoveredEvent(Marker marker) {
        this.marker = marker;
    }
}
