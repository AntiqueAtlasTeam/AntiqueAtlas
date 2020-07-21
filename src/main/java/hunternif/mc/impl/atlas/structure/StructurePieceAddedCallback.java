package hunternif.mc.impl.atlas.structure;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface StructurePieceAddedCallback {
	Event<StructurePieceAddedCallback> EVENT = EventFactory.createArrayBacked(StructurePieceAddedCallback.class,
					(invokers) -> (structurePiece, world) -> {
						for (StructurePieceAddedCallback callback : invokers) {
							callback.onStructurePieceAdded(structurePiece, world);
						}
					});

	void onStructurePieceAdded(StructurePiece structurePiece, ServerWorld world);
}
