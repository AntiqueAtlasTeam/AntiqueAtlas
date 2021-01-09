package hunternif.mc.impl.atlas.forge.event;

import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class StructureAddedEvent extends Event {
	private final StructureStart<?> structureStart;
	private final ServerWorld world;
	
	public StructureAddedEvent(StructureStart<?> structureStart, ServerWorld world) {
		this.structureStart = structureStart;
		this.world = world;
	}

	public StructureStart<?> getStructureStart() {
		return structureStart;
	}

	public ServerWorld getWorld() {
		return world;
	}
	
}
