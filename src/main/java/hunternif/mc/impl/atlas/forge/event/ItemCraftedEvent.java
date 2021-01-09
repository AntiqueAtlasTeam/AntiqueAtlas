package hunternif.mc.impl.atlas.forge.event;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This Event also takes the CraftingResultSlot
 * @author Stereowalker
 *
 */
public class ItemCraftedEvent extends PlayerEvent {
    @Nonnull
    private final ItemStack craftingStack;
    private final IInventory craftMatrix;
    private final CraftingResultSlot resultSlot;
    public ItemCraftedEvent(PlayerEntity player, @Nonnull ItemStack crafting, IInventory craftMatrix, CraftingResultSlot slot)
    {
        super(player);
        this.craftingStack = crafting;
        this.craftMatrix = craftMatrix;
        this.resultSlot = slot;
    }

    @Nonnull
    public ItemStack getCraftedStack()
    {
        return this.craftingStack;
    }

    public IInventory getCraftingMatrix()
    {
        return this.craftMatrix;
    }

	public CraftingResultSlot getResultSlot() {
		return resultSlot;
	}
}
