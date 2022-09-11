package hunternif.mc.impl.atlas.structure;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;

public interface StructureAddedCallback {
	Event<StructureAddedCallback> EVENT = EventFactory.createLoop();

	void onStructureAdded(StructureStart structureStart, ServerWorld world);
}
