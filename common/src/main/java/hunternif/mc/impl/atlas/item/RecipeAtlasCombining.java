package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * 2 or more atlases combine into one with all biome and marker data copied.
 * All data is copied into a new atlas instance.
 *
 * @author Hunternif
 */
public class RecipeAtlasCombining implements CraftingRecipe {
    public static final RecipeSerializer<RecipeAtlasCombining> SERIALIZER = new SpecialRecipeSerializer<>(RecipeAtlasCombining::new);
    private final Identifier id;

    public RecipeAtlasCombining(Identifier id, CraftingRecipeCategory craftingRecipeCategory) {
        this.id = id;
    }

    @Override
    public String getGroup() {
        return AntiqueAtlasMod.ID + ":atlas_combine";
    }

    @Override
    public boolean matches(RecipeInputInventory inv, World world) {
        return matches(inv);
    }

    private boolean matches(RecipeInputInventory inv) {
        int atlasesFound = 0;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == AntiqueAtlasItems.ATLAS.get()) {
                    atlasesFound++;
                }
            }
        }
        return atlasesFound > 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack firstAtlas = ItemStack.EMPTY;
        List<Integer> atlasIds = new ArrayList<>(9);
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getStack(i);
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
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager dynamicRegistryManager) {
        return new ItemStack(AntiqueAtlasItems.ATLAS.get());
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

    @Override
    public CraftingRecipeCategory getCategory() {
        return CraftingRecipeCategory.MISC;
    }

    public ItemStack onCrafted(World world, Inventory inventory, ItemStack result) {
        if (world.isClient) return result;
        // Until the first update, on the client the returned atlas ID is the same as the first Atlas on the crafting grid.
        int atlasID = AntiqueAtlasMod.getAtlasIdData(world).getNextAtlasId();

        AtlasData destBiomes = AntiqueAtlasMod.tileData.getData(atlasID, world);
        destBiomes.markDirty();
        MarkersData destMarkers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        destMarkers.markDirty();
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
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
        result.getOrCreateNbt().putInt("atlasID", atlasID);
        return result;
    }
}
