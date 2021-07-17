package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RecipeAtlasCloning implements CraftingRecipe {
    public static final RecipeSerializer<?> SERIALIZER = new SpecialRecipeSerializer<>(RecipeAtlasCloning::new);
    private final Identifier id;

    public RecipeAtlasCloning(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public String getGroup() {
        return AntiqueAtlasMod.ID + ":atlas";
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        int i = 0; // number of empty atlases
        ItemStack filledAtlas = ItemStack.EMPTY;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getStack(j);

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
    public ItemStack craft(CraftingInventory inv) {
        int i = 0; // number of new copies
        ItemStack filledAtlas = ItemStack.EMPTY;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getStack(j);

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
            newAtlas.getOrCreateNbt().putInt("atlasID", AtlasItem.getAtlasID(filledAtlas));

            if (filledAtlas.hasCustomName()) {
                newAtlas.setCustomName(filledAtlas.getName());
            }

            return newAtlas;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
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
}
