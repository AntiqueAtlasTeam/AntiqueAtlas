package hunternif.mc.atlas.item;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

abstract class RecipeBase implements IRecipe {
    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] stacks = new ItemStack[inv.getSizeInventory()];
        for (int i = 0; i < stacks.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            stacks[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return stacks;
    }
}
