package hunternif.mc.atlas.event;

import hunternif.mc.atlas.registry.MarkerType;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * When the server wants to create an automatic marker in a player's atlas,
 * but it can be disabled by the player's client.
 * Cancel this event to prevent adding marker.
 */
public class OptionalMarkerEvent extends Event {
    public int atlasID;
    public int dimension;
    public MarkerType type;
    public String label;
    public int x, z;
    public boolean visibleAhead;

    public OptionalMarkerEvent() {}

    public OptionalMarkerEvent(
            int atlasID,
            int dimension,
            MarkerType type,
            String label,
            int x, int z,
            boolean visibleAhead) {
        this.atlasID = atlasID;
        this.dimension = dimension;
        this.type = type;
        this.label = label;
        this.x = x;
        this.z = z;
        this.visibleAhead = visibleAhead;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
