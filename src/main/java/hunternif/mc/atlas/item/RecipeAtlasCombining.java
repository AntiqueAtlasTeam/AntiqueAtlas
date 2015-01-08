package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

/**
 * 2 or more atlases combine into one with all biome and marker data copied.
 * All data is copied into the first atlas in the grid.
 * @author Hunternif
 */
public class RecipeAtlasCombining implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		return matches(inv);
	}
	
	private boolean matches(IInventory inv) {
		int atlasesFound = 0;
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
					atlasesFound++;
				}
			}
		}
		return atlasesFound > 1;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack firstAtlas = null;
		List<Integer> atlasIds = new ArrayList<Integer>(2);
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
					if (firstAtlas == null) {
						firstAtlas = stack;
					} else {
						atlasIds.add(stack.getItemDamage());
					}
				}
			}
		}
		if (atlasIds.size() < 1) return null;
		return firstAtlas;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}
	
	@SubscribeEvent
	public void onCrafted(ItemCraftedEvent event) {
		// Make sure it's the same recipe:
		if (event.crafting.getItem() != AntiqueAtlasMod.itemAtlas || !matches(event.craftMatrix)) {
			return;
		}
		World world = event.player.worldObj;
		AtlasData destBiomes = AntiqueAtlasMod.itemAtlas.getAtlasData(event.crafting, world);
		MarkersData destMarkers = AntiqueAtlasMod.itemAtlas.getMarkersData(event.crafting, world);
		for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
			ItemStack stack = event.craftMatrix.getStackInSlot(i);
			if (stack == null) continue;
			AtlasData srcBiomes = AntiqueAtlasMod.itemAtlas.getAtlasData(stack, world);
			if (destBiomes != null && srcBiomes != null && destBiomes != srcBiomes) {
				for (int dim : srcBiomes.getVisitedDimensions()) {
					destBiomes.getSeenChunksInDimension(dim).putAll(srcBiomes.getSeenChunksInDimension(dim));
				}
				destBiomes.markDirty();
			}
			MarkersData srcMarkers = AntiqueAtlasMod.itemAtlas.getMarkersData(stack, world);
			if (destMarkers != null && srcMarkers != null && destMarkers != srcMarkers) {
				for (int dim : srcMarkers.getVisitedDimensions()) {
					destMarkers.getMarkersDataInDimension(dim).putAll(srcMarkers.getMarkersDataInDimension(dim));
				}
				destMarkers.markDirty();
			}
		}
	}

}
