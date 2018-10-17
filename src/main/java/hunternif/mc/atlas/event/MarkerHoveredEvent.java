package hunternif.mc.atlas.event;


import hunternif.mc.atlas.marker.Marker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MarkerHoveredEvent extends Event {

    public EntityPlayer player;
    public Marker marker;

    public MarkerHoveredEvent(EntityPlayer player, Marker marker) {
        this.player = player;
        this.marker = marker;
    }
}
