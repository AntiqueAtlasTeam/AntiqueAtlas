package hunternif.mc.impl.atlas.event;

import java.util.function.Consumer;

import hunternif.mc.impl.atlas.marker.Marker;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

@FunctionalInterface
public interface MarkerHoveredCallback {
    public class TheEvent extends PlayerEvent {
    	Marker marker;

        public TheEvent (Player player, Marker marker) {
        	super(player);
        	this.marker = marker;
        }
        
        public Marker getMarker() {
    		return marker;
    	}
    }
    
    void onHovered(Player player, Marker marker);
    
	public static void register(MarkerHoveredCallback consumer) {
		MinecraftForge.EVENT_BUS.addListener((Consumer<TheEvent>)event->consumer.onHovered(event.getPlayer(), event.getMarker()));
	}
}
