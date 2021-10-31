package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

public class RecipeAtlasCloning implements CraftingRecipe {
    public static final RecipeSerializer<?> SERIALIZER = new SimpleRecipeSerializer<>(RecipeAtlasCloning::new);
    private final ResourceLocation id;

    public RecipeAtlasCloning(ResourceLocation identifier) {
        this.id = identifier;
    }

    @Override
    public String getGroup() {
        return AntiqueAtlasMod.ID + ":atlas";
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        int i = 0; // number of empty atlases
        ItemStack filledAtlas = ItemStack.EMPTY;

        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack stack = inv.getItem(j);

            if (!stack.isEmpty()) {
                if (stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                    if (!filledAtlas.isEmpty()) {
                        return false;
                    }
                    filledAtlas = stack;
                } else {
                    if (stack.getItem() != RegistrarAntiqueAtlas.EMPTY_ATLAS) {
                        return false;
                    }
                    i++;
                }
            }
        }

        return !filledAtlas.isEmpty() && i > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        int i = 0; // number of new copies
        ItemStack filledAtlas = ItemStack.EMPTY;

        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack stack = inv.getItem(j);

            if (!stack.isEmpty()) {
                if (stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                    if (!filledAtlas.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    filledAtlas = stack;
                } else {
                    if (stack.getItem() != RegistrarAntiqueAtlas.EMPTY_ATLAS) {
                        return ItemStack.EMPTY;
                    }
                    i++;
                }
            }
        }

        if (!filledAtlas.isEmpty() && i >= 1) {
            ItemStack newAtlas = new ItemStack(RegistrarAntiqueAtlas.ATLAS, i + 1);
            newAtlas.getOrCreateTag().putInt("atlasID", AtlasItem.getAtlasID(filledAtlas));

            if (filledAtlas.hasCustomHoverName()) {
                newAtlas.setHoverName(filledAtlas.getHoverName());
            }

            return newAtlas;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
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
}
