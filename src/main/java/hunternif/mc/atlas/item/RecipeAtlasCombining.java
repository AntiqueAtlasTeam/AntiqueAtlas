package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

/**
 * 2 or more atlases combine into one with all biome and marker data copied.
 * All data is copied into a new atlas instance.
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
		List<Integer> atlasIds = new ArrayList<Integer>(9);
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
		if (world.isRemote) return;
		// Until the first update, on the client the returned atlas ID is the same as the first Atlas on the crafting grid.
		int atlasID = world.getUniqueDataId(ItemAtlas.WORLD_ATLAS_DATA_ID);
		
		AtlasData destBiomes = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
		destBiomes.markDirty();
		MarkersData destMarkers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
		destMarkers.markDirty();
		for (int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
			ItemStack stack = event.craftMatrix.getStackInSlot(i);
			if (stack == null) continue;
			AtlasData srcBiomes = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
			if (destBiomes != null && srcBiomes != null && destBiomes != srcBiomes) {
				for (int dim : srcBiomes.getVisitedDimensions()) {
					destBiomes.getSeenChunksInDimension(dim).putAll(srcBiomes.getSeenChunksInDimension(dim));
				}
			}
			MarkersData srcMarkers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
			if (destMarkers != null && srcMarkers != null && destMarkers != srcMarkers) {
				for (int dim : srcMarkers.getVisitedDimensions()) {
					for (Marker marker : srcMarkers.getMarkersDataInDimension(dim).getAllMarkers()) {
						destMarkers.createAndSaveMarker(marker.getType(), marker.getLabel(),
								dim, marker.getX(), marker.getZ(), marker.isVisibleAhead());
					}
				}
			}
		}
		
		// Set item damage last, because otherwise we wouldn't be able copy the
		// data from the atlas which was used as a placeholder for the result.
		event.crafting.setItemDamage(atlasID);
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];
		for (int i = 0; i < aitemstack.length; ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
		}
		return aitemstack;
	}
}
