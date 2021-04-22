package hunternif.mc.impl.atlas.item;

import java.util.ArrayList;
import java.util.List;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * 2 or more atlases combine into one with all biome and marker data copied.
 * All data is copied into a new atlas instance.
 * @author Hunternif
 */
public class RecipeAtlasCombining implements ICraftingRecipe {
	public static final IRecipeSerializer<RecipeAtlasCombining> SERIALIZER = new SpecialRecipeSerializer<>(RecipeAtlasCombining::new);
	private final ResourceLocation id;

    public RecipeAtlasCombining(ResourceLocation id) {
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
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
					atlasesFound++;
				}
			}
		}
		return atlasesFound > 1;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		ItemStack firstAtlas = ItemStack.EMPTY;
		List<Integer> atlasIds = new ArrayList<>(9);
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof AtlasItem) {
					if (firstAtlas.isEmpty()) {
						firstAtlas = stack;
					} else {
						atlasIds.add(AtlasItem.getAtlasID(stack));
					}
				}
			}
		}
		return atlasIds.size() < 1 ? ItemStack.EMPTY : firstAtlas.copy();
	}

	@Override
    public boolean canFit(int width, int height) {
        return true;
    }

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(RegistrarAntiqueAtlas.ATLAS);
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public IRecipeType<?> getType() {
		return IRecipeType.CRAFTING;
	}

	public ItemStack onCrafted(World world, IInventory iInventory, ItemStack result) {
		if (world.isRemote) return result;
		// Until the first update, on the client the returned atlas ID is the same as the first Atlas on the crafting grid.
		int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();

		AtlasData destBiomes = AntiqueAtlasMod.tileData.getData(atlasID, world);
		destBiomes.markDirty();
		MarkersData destMarkers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
		destMarkers.markDirty();
		for (int i = 0; i < iInventory.getSizeInventory(); ++i) {
			ItemStack stack = iInventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			AtlasData srcBiomes = AntiqueAtlasMod.tileData.getData(stack, world);
			if (destBiomes != null && srcBiomes != null && destBiomes != srcBiomes) {
				for (RegistryKey<World> worldRegistryKey : srcBiomes.getVisitedWorlds()) {
					destBiomes.getWorldData(worldRegistryKey).addData(srcBiomes.getWorldData(worldRegistryKey));
				}
			}
			MarkersData srcMarkers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
			if (destMarkers != null && srcMarkers != null && destMarkers != srcMarkers) {
				for (RegistryKey<World> worldRegistryKey : srcMarkers.getVisitedDimensions()) {
					for (Marker marker : srcMarkers.getMarkersDataInWorld(worldRegistryKey).getAllMarkers()) {
						destMarkers.createAndSaveMarker(marker.getType(),
								worldRegistryKey, marker.getX(), marker.getZ(), marker.isVisibleAhead(), marker.getLabel());
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
