package stereowalker.forge.impl.atlas.hook;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import stereowalker.forge.impl.atlas.event.StructureAddedEvent;
import stereowalker.forge.impl.atlas.event.StructurePieceAddedEvent;
import stereowalker.forge.impl.atlas.event.TileIdRegisteredEvent;

public class AntiqueAtlasHooks {

	public static void onTileIdRegistered(Collection<ResourceLocation> tileIds )
	{
		MinecraftForge.EVENT_BUS.post(new TileIdRegisteredEvent(tileIds));
	}

	public static void onStructureAdded(StructureStart<?> structureStart, ServerWorld world)
	{
		MinecraftForge.EVENT_BUS.post(new StructureAddedEvent(structureStart, world));
	}

	public static void onStructurePieceAdded(StructurePiece structurePiece, ServerWorld world)
	{
		MinecraftForge.EVENT_BUS.post(new StructurePieceAddedEvent(structurePiece, world));
	}

	public static void onStructureAddedHook(StructureStart<?> structureStart, ISeedReader reader) {
		ServerWorld world;

		if (reader instanceof ServerWorld) {
			world = (ServerWorld) reader;
		} else {
			world = ((WorldGenRegion) reader).getWorld();
		}

		onStructureAdded(structureStart, world);
	}
	
	public static boolean onStructurePieceAddedHook(boolean get, StructurePiece structurePiece, ISeedReader reader) {
		ServerWorld world;

		if (reader instanceof ServerWorld) {
			world = (ServerWorld) reader;
		} else {
			world = ((WorldGenRegion) reader).getWorld();
		}
		
		onStructurePieceAdded(structurePiece, world);
		
		return get;
	}
}
