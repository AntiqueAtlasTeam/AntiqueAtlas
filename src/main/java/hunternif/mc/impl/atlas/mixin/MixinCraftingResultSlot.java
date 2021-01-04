package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.event.RecipeCraftedCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot extends Slot {
    @Final
    @Shadow
    private CraftingInventory input;
    @Final
    @Shadow
    private PlayerEntity player;

    public MixinCraftingResultSlot(Inventory inventory_1, int int_1, int int_2, int int_3) {
        super(inventory_1, int_1, int_2, int_3);
    }

    @Inject(at = @At("HEAD"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
    protected void onCrafted(ItemStack stack, final CallbackInfo info) {
        if (inventory instanceof RecipeUnlocker) {
            RecipeCraftedCallback.EVENT.invoker().onCrafted(this.player, this.player.world, ((RecipeUnlocker) (inventory)).getLastRecipe(), stack, input);
        }
    }
}
