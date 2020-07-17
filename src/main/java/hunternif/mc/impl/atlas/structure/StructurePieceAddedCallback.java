package hunternif.mc.impl.atlas.structure;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.structure.StructurePiece;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface StructurePieceAddedCallback {
	Event<StructurePieceAddedCallback> EVENT = EventFactory.createArrayBacked(StructurePieceAddedCallback.class,
					(invokers) -> (world, structurePiece) -> {
						for (StructurePieceAddedCallback callback : invokers) {
							callback.onStructurePieceAdded(world, structurePiece);
						}
					});

	void onStructurePieceAdded(World world, StructurePiece structurePiece);
}
