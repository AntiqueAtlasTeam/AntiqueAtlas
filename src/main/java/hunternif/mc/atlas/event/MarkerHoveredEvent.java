package hunternif.mc.atlas.event;


import hunternif.mc.atlas.marker.Marker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MarkerHoveredEvent extends Event {

    public PlayerEntity player;
    public Marker marker;

    public MarkerHoveredEvent(PlayerEntity player, Marker marker) {
        this.player = player;
        this.marker = marker;
    }
}
