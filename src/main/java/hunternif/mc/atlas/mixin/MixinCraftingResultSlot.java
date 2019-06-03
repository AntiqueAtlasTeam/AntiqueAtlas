package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.item.RecipeAtlasCombining;
import net.minecraft.container.CraftingResultSlot;
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
public class MixinCraftingResultSlot {

    @Shadow
    CraftingInventory craftingInv;

    @Shadow
    PlayerEntity player;

    @Inject(at = @At("HEAD"), method = "onCrafted(Lnet/minecraft/item/ItemStack;)V")
    protected void onCrafted(ItemStack stack, final CallbackInfo info) {
        if(this.player.world.isClient()) return;

        Inventory resultInv = ((CraftingResultSlot)((Object)this)).inventory;

        if(resultInv instanceof RecipeUnlocker && ((RecipeUnlocker)(resultInv)).getLastRecipe() instanceof RecipeAtlasCombining) {
            RecipeAtlasCombining recipe = (RecipeAtlasCombining) ((RecipeUnlocker)(resultInv)).getLastRecipe();

            recipe.onCrafted(this.player.world, craftingInv, stack);
        }
    }

}