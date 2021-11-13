package hunternif.mc.impl.atlas.structure;

import java.util.function.Consumer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public interface StructurePieceAddedCallback {
	/**
	 * @author Stereowalker
	 */
	public static class TheEvent extends Event {
		private final StructurePiece structurePiece;
		private final ServerLevel world;
		
		public TheEvent(StructurePiece structurePiece, ServerLevel world) {
			this.structurePiece = structurePiece;
			this.world = world;
		}

		public StructurePiece getStructurePiece() {
			return structurePiece;
		}
		
		public ServerLevel getWorld() {
			return world;
		}
	}

	void onStructurePieceAdded(StructurePiece structurePiece, ServerLevel world);
	
	public static void register(StructurePieceAddedCallback consumer) {
		MinecraftForge.EVENT_BUS.addListener((Consumer<TheEvent>)event->consumer.onStructurePieceAdded(event.getStructurePiece(), event.getWorld()));
	}
}
