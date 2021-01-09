package hunternif.mc.impl.atlas.event;

import hunternif.mc.impl.atlas.item.RecipeAtlasCombining;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;


public class RecipeCraftedHandler {

    public static ActionResultType onCrafted(PlayerEntity player, World world, IRecipe<?> recipe, ItemStack result, IInventory iInventory) {
    	if (world.isRemote()) return ActionResultType.PASS;

        if (recipe instanceof RecipeAtlasCombining) {
            RecipeAtlasCombining combining_recipe = (RecipeAtlasCombining) recipe;

            combining_recipe.onCrafted(world, iInventory, result);
        }

        return ActionResultType.PASS;
    }
}
