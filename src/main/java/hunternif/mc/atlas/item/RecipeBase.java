package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

abstract class RecipeBase extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> aitemstack = NonNullList.create();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack.add(net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }
        return aitemstack;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public String getGroup() {
        return AntiqueAtlasMod.ID + ":atlas";
    }
}
