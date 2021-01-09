package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeAtlasCloning implements ICraftingRecipe {
	public static final IRecipeSerializer<?> SERIALIZER = new SpecialRecipeSerializer<>(RecipeAtlasCloning::new);
	private final ResourceLocation id;

	public RecipeAtlasCloning(ResourceLocation identifier) {
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

		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			ItemStack stack = inv.getStackInSlot(j);

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
	public ItemStack getCraftingResult(CraftingInventory inv) {
		int i = 0; // number of new copies
		ItemStack filledAtlas = ItemStack.EMPTY;

		for (int j = 0; j < inv.getSizeInventory(); ++j) {
			ItemStack stack = inv.getStackInSlot(j);

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

			if (filledAtlas.hasDisplayName()) {
				newAtlas.setDisplayName(filledAtlas.getDisplayName());
			}

			return newAtlas;
		} else {
			return ItemStack.EMPTY;
		}
	}

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public IRecipeType<?> getType() {
		return IRecipeType.CRAFTING;
	}
}
