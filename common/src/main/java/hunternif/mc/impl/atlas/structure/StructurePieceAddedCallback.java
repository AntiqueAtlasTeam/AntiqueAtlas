package hunternif.mc.impl.atlas.structure;

import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;

public interface StructurePieceAddedCallback {
	Event<StructurePieceAddedCallback> EVENT = EventFactory.createLoop();

	void onStructurePieceAdded(StructurePiece structurePiece, ServerWorld world);
}
