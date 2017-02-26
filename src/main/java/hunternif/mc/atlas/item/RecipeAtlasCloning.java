package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RecipeAtlasCloning implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		int i = 0; // number of empty atlases
		ItemStack filledAtlas = null;

		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			ItemStack stack = inv.getStackInSlot(j);

			if (stack != null) {
				if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
					if (filledAtlas != null) {
						return false;
					}
					filledAtlas = stack;
				} else {
					if (stack.getItem() != AntiqueAtlasMod.itemEmptyAtlas) {
						return false;
					}
					i++;
				}
			}
		}

		return filledAtlas != null && i > 0;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int i = 0; // number of new copies
		ItemStack filledAtlas = null;

		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			ItemStack stack = inv.getStackInSlot(j);

			if (stack != null) {
				if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
					if (filledAtlas != null) {
						return  ItemStack.EMPTY;
					}
					filledAtlas = stack;
				} else {
					if (stack.getItem() != AntiqueAtlasMod.itemEmptyAtlas) {
						return  ItemStack.EMPTY;
					}
					i++;
				}
			}
		}

		if (filledAtlas != null && i >= 1) {
			ItemStack newAtlas = new ItemStack(AntiqueAtlasMod.itemAtlas, i + 1, filledAtlas.getItemDamage());

			if (filledAtlas.hasDisplayName()) {
				newAtlas.setStackDisplayName(filledAtlas.getDisplayName());
			}

			return newAtlas;
		}
		else
		{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> aitemstack = NonNullList.create();
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			aitemstack.add(net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
		}
		return aitemstack;
	}
}
