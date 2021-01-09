package hunternif.mc.impl.atlas.forge.hook;

import java.util.Collection;

import hunternif.mc.impl.atlas.forge.event.ItemCraftedEvent;
import hunternif.mc.impl.atlas.forge.event.MarkerHoveredEvent;
import hunternif.mc.impl.atlas.forge.event.StructureAddedEvent;
import hunternif.mc.impl.atlas.forge.event.StructurePieceAddedEvent;
import hunternif.mc.impl.atlas.forge.event.TileIdRegisteredEvent;
import hunternif.mc.impl.atlas.marker.Marker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class AntiqueAtlasHooks {

	public static void fireTileIdRegistered(Collection<ResourceLocation> tileIds )
	{
		MinecraftForge.EVENT_BUS.post(new TileIdRegisteredEvent(tileIds));
	}

	public static void fireStructureAdded(StructureStart<?> structureStart, ServerWorld world)
	{
		MinecraftForge.EVENT_BUS.post(new StructureAddedEvent(structureStart, world));
	}

	public static void fireStructurePieceAdded(StructurePiece structurePiece, ServerWorld world)
	{
		MinecraftForge.EVENT_BUS.post(new StructurePieceAddedEvent(structurePiece, world));
	}
	
	public static void firePlayerCraftingEvent(PlayerEntity player, ItemStack crafted, CraftingInventory craftMatrix, CraftingResultSlot slot)
    {
        MinecraftForge.EVENT_BUS.post(new ItemCraftedEvent(player, crafted, craftMatrix, slot));
    }
	
	public static void fireMarkerHovered(PlayerEntity player, Marker marker) {
		MinecraftForge.EVENT_BUS.post(new MarkerHoveredEvent(player, marker));
	}

	public static void onStructureAddedHook(StructureStart<?> structureStart, ISeedReader reader) {
		ServerWorld world;

		if (reader instanceof ServerWorld) {
			world = (ServerWorld) reader;
		} else {
			world = ((WorldGenRegion) reader).getWorld();
		}

		fireStructureAdded(structureStart, world);
	}
	
	public static boolean onStructurePieceAddedHook(boolean get, StructurePiece structurePiece, ISeedReader reader) {
		ServerWorld world;

		if (reader instanceof ServerWorld) {
			world = (ServerWorld) reader;
		} else {
			world = ((WorldGenRegion) reader).getWorld();
		}
		
		fireStructurePieceAdded(structurePiece, world);
		
		return get;
	}
}
