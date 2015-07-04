package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

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
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int i = 0; // number of new copies
		ItemStack filledAtlas = null;

		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			ItemStack stack = inv.getStackInSlot(j);

			if (stack != null) {
				if (stack.getItem() == AntiqueAtlasMod.itemAtlas) {
					if (filledAtlas != null) {
						return null;
					}
					filledAtlas = stack;
				} else {
					if (stack.getItem() != AntiqueAtlasMod.itemEmptyAtlas) {
						return null;
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
			return null;
		}
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		return null;
	}
}
