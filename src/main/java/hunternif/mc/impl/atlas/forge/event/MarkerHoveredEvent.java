package hunternif.mc.impl.atlas.forge.event;

import hunternif.mc.impl.atlas.marker.Marker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class MarkerHoveredEvent extends Event {
	private final PlayerEntity player; 
	private final Marker marker; 
	
	public MarkerHoveredEvent(PlayerEntity player, Marker marker) {
		this.player = player;
		this.marker = marker;
	}
	
	public PlayerEntity getPlayer() {
		return player;
	}
	
	public Marker getMarker() {
		return marker;
	}
	
}
