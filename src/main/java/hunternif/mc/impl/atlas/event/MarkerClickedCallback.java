package hunternif.mc.impl.atlas.event;

import java.util.function.Consumer;

import hunternif.mc.impl.atlas.marker.Marker;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

@FunctionalInterface
public interface MarkerClickedCallback {
    
    public class TheEvent extends PlayerEvent {
        Marker marker; 
        int mouseState;
        
        public TheEvent(Player player, Marker marker, int mouseState) {
        	super(player);
        	this.marker = marker;
        	this.mouseState = mouseState;
        }
        
        public Marker getMarker() {
    		return marker;
    	}
        
        public int getMouseState() {
    		return mouseState;
    	}
        
        @Override
        public boolean isCancelable() {
        	return true;
        }
    }
    
    boolean onClicked(Player player, Marker marker, int mouseState);
    
	public static void register(MarkerClickedCallback consumer) {
		MinecraftForge.EVENT_BUS.addListener((Consumer<TheEvent>)event->consumer.onClicked(event.getPlayer(), event.getMarker(), event.getMouseState()));
	}
}
