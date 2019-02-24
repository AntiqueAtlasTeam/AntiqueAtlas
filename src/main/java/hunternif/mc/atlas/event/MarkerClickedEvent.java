package hunternif.mc.atlas.event;

import hunternif.mc.atlas.marker.Marker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MarkerClickedEvent extends Event {

    public PlayerEntity player;
    public Marker marker;
    public int mouseState;

    public MarkerClickedEvent(PlayerEntity player, Marker marker, int mouseState) {
        this.player = player;
        this.marker = marker;
        this.mouseState = mouseState;
    }
}
