package hunternif.mc.impl.atlas.forge.event;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class TileIdRegisteredEvent extends Event {
	private final Collection<ResourceLocation> tileIds;
	
	public TileIdRegisteredEvent(Collection<ResourceLocation> tileIds) {
		this.tileIds = tileIds;
	}

	public Collection<ResourceLocation> getTileIds() {
		return tileIds;
	}
	
}
