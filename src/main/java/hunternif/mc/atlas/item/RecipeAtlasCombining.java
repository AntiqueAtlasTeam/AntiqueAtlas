package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import java.util.ArrayList;
import java.util.List;

/**
 * 2 or more atlases combine into one with all biome and marker data copied.
 * All data is copied into a new atlas instance.
 * @author Hunternif
 */
public class RecipeAtlasCombining implements CraftingRecipe {
	public static final RecipeSerializer<RecipeAtlasCombining> SERIALIZER = new SpecialRecipeSerializer<>(RecipeAtlasCombining::new);
	private final Identifier id;

    public RecipeAtlasCombining(Identifier id) {
    	this.id = id;
    }

	@Override
	public String getGroup() {
		return AntiqueAtlasMod.ID + ":atlas_combine";
	}

    @Override
	public boolean matches(CraftingInventory inv, World world) {
		return matches(inv);
	}

	private boolean matches(CraftingInventory inv) {
		int atlasesFound = 0;
		for (int i = 0; i < inv.size(); ++i) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
					atlasesFound++;
				}
			}
		}
		return atlasesFound > 1;
	}

	@Override
	public ItemStack craft(CraftingInventory inv) {
		ItemStack firstAtlas = ItemStack.EMPTY;
		List<Integer> atlasIds = new ArrayList<>(9);
		for (int i = 0; i < inv.size(); ++i) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ItemAtlas) {
					if (firstAtlas.isEmpty()) {
						firstAtlas = stack;
					} else {
						atlasIds.add(((ItemAtlas) stack.getItem()).getAtlasID(stack));
					}
				}
			}
		}
		return atlasIds.size() < 1 ? ItemStack.EMPTY : firstAtlas.copy();
	}

	@Override
    public boolean fits(int width, int height) {
        return true;
    }

	@Override
	public ItemStack getOutput() {
		return new ItemStack(RegistrarAntiqueAtlas.ATLAS);
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}

	public ItemStack onCrafted(World world, Inventory inventory, ItemStack result) {
		if (world.isClient) return result;
		// Until the first update, on the client the returned atlas ID is the same as the first Atlas on the crafting grid.
		int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();

		AtlasData destBiomes = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
		destBiomes.markDirty();
		MarkersData destMarkers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
		destMarkers.markDirty();
		for (int i = 0; i < inventory.size(); ++i) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isEmpty()) continue;
			AtlasData srcBiomes = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
			if (destBiomes != null && srcBiomes != null && destBiomes != srcBiomes) {
				for (RegistryKey<DimensionType> dim : srcBiomes.getVisitedDimensions()) {
					destBiomes.getDimensionData(dim).addData(srcBiomes.getDimensionData(dim));
				}
			}
			MarkersData srcMarkers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
			if (destMarkers != null && srcMarkers != null && destMarkers != srcMarkers) {
				for (RegistryKey<DimensionType> dim : srcMarkers.getVisitedDimensions()) {
					for (Marker marker : srcMarkers.getMarkersDataInDimension(dim).getAllMarkers()) {
						destMarkers.createAndSaveMarker(marker.getType(), marker.getLabel(),
								dim, marker.getX(), marker.getZ(), marker.isVisibleAhead());
					}
				}
			}
		}

		// Set atlas ID last, because otherwise we wouldn't be able copy the
		// data from the atlas which was used as a placeholder for the result.
		result.getOrCreateTag().putInt("atlasID", atlasID);
		return result;
	}
}
