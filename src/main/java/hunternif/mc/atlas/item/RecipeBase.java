package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;

abstract class RecipeBase implements Recipe {
//    @Override
//    public boolean isHidden() {
//        return true;
//    }

    @Override
    public String getGroup() {
        return AntiqueAtlasMod.ID + ":atlas";
    }
}
