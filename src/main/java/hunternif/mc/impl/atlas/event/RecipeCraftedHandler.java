package hunternif.mc.impl.atlas.event;

import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;


public class RecipeCraftedHandler implements RecipeCraftedCallback {

    @Override
    public InteractionResult onCrafted(Player player, Level world, Recipe<?> recipe, ItemStack result, Container ingredients) {
        if (world.isClientSide()) return InteractionResult.PASS;

        if (recipe instanceof RecipeAtlasCombining) {
            RecipeAtlasCombining combining_recipe = (RecipeAtlasCombining) recipe;

            combining_recipe.onCrafted(world, ingredients, result);
        }

        return InteractionResult.PASS;
    }
}
