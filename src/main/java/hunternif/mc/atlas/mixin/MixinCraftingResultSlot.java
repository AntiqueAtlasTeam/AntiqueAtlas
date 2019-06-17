package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.event.RecipeCraftedCallback;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeUnlocker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot extends Slot {
    @Shadow
    CraftingInventory craftingInv;

    @Shadow
    PlayerEntity player;

    public MixinCraftingResultSlot(Inventory inventory_1, int int_1, int int_2, int int_3) {
        super(inventory_1, int_1, int_2, int_3);
    }

    @Inject(at = @At("HEAD"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
    protected void onCrafted(ItemStack stack, final CallbackInfo info) {
        if (inventory instanceof RecipeUnlocker) {
            RecipeCraftedCallback.EVENT.invoker().onCrafted(this.player, this.player.world, ((RecipeUnlocker) (inventory)).getLastRecipe(), stack, craftingInv);
        }
    }
}