package hunternif.mc.impl.atlas.event;

import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;


public class RecipeCraftedHandler implements RecipeCraftedCallback {

    @Override
    public ActionResult onCrafted(PlayerEntity player, World world, Recipe recipe, ItemStack result, Inventory ingredients) {
        if (world.isClient()) return ActionResult.PASS;

        if (recipe instanceof RecipeAtlasCombining) {
            RecipeAtlasCombining combining_recipe = (RecipeAtlasCombining) recipe;

            combining_recipe.onCrafted(world, ingredients, result);
        }

        return ActionResult.PASS;
    }
}
