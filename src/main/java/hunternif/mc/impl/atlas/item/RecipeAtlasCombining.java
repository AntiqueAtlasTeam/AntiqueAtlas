package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * 2 or more atlases combine into one with all biome and marker data copied.
 * All data is copied into a new atlas instance.
 *
 * @author Hunternif
 */
public class RecipeAtlasCombining implements CraftingRecipe {
    public static final RecipeSerializer<RecipeAtlasCombining> SERIALIZER = new SimpleRecipeSerializer<>(RecipeAtlasCombining::new);
    private final ResourceLocation id;

    public RecipeAtlasCombining(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public String getGroup() {
        return AntiqueAtlasMod.ID + ":atlas_combine";
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        return matches(inv);
    }

    private boolean matches(CraftingContainer inv) {
        int atlasesFound = 0;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                    atlasesFound++;
                }
            }
        }
        return atlasesFound > 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack firstAtlas = ItemStack.EMPTY;
        List<Integer> atlasIds = new ArrayList<>(9);
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
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
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return new ItemStack(RegistrarAntiqueAtlas.ATLAS);
    }

    @Override
    public ResourceLocation getId() {
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

    public ItemStack onCrafted(Level world, Container inventory, ItemStack result) {
        if (world.isClientSide) return result;
        // Until the first update, on the client the returned atlas ID is the same as the first Atlas on the crafting grid.
        int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();

        AtlasData destBiomes = AntiqueAtlasMod.tileData.getData(atlasID, world);
        destBiomes.setDirty();
        MarkersData destMarkers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        destMarkers.setDirty();
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            AtlasData srcBiomes = AntiqueAtlasMod.tileData.getData(stack, world);
            if (destBiomes != null && srcBiomes != null && destBiomes != srcBiomes) {
                for (ResourceKey<Level> worldRegistryKey : srcBiomes.getVisitedWorlds()) {
                    destBiomes.getWorldData(worldRegistryKey).addData(srcBiomes.getWorldData(worldRegistryKey));
                }
            }
            MarkersData srcMarkers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
            if (destMarkers != null && srcMarkers != null && destMarkers != srcMarkers) {
                for (ResourceKey<Level> worldRegistryKey : srcMarkers.getVisitedDimensions()) {
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
