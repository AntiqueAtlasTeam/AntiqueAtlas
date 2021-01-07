package hunternif.mc.impl.atlas.forge.event;

import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class StructurePieceAddedEvent extends Event {
	private final StructurePiece structurePiece;
	private final ServerWorld world;
	
	public StructurePieceAddedEvent(StructurePiece structurePiece, ServerWorld world) {
		this.structurePiece = structurePiece;
		this.world = world;
	}

	public StructurePiece getStructurePiece() {
		return structurePiece;
	}
	
	public ServerWorld getWorld() {
		return world;
	}

	
}
