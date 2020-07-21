package hunternif.mc.impl.atlas.structure;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.World;

public interface StructureAddedCallback {
	Event<StructureAddedCallback> EVENT = EventFactory.createArrayBacked(StructureAddedCallback.class,
			(invokers) -> (structurePiece, world) -> {
				for (StructureAddedCallback callback : invokers) {
					callback.onStructureAdded(structurePiece, world);
				}
			});

	void onStructureAdded(StructureStart<?> structureStart, ServerWorld world);
}
