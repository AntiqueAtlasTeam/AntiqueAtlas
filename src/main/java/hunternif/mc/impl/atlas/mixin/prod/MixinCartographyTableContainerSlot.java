package hunternif.mc.impl.atlas.mixin.prod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.mixinhooks.CartographyTableHooks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

@Mixin(targets = "net.minecraft.inventory.container.CartographyContainer$4")
class MixinCartographyTableContainerSlot {

    @Inject(method = "func_75214_a", at = @At("RETURN"), cancellable = true)
    void antiqueatlas_canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(stack.getItem() == AtlasAPI.getAtlasItem() || info.getReturnValueZ());
    }
}

@Mixin(targets = "net.minecraft.inventory.container.CartographyContainer$5")
class MixinCartographyTableContainerResultSlot {

    CartographyContainer antiqueatlas_handler;

    @Inject(method = "<init>", at = @At("TAIL"))
    void antiqueatlas_init(CartographyContainer handler, IInventory inv, int a, int b, int c, IWorldPosCallable context, CallbackInfo info) {
        antiqueatlas_handler = handler;
    }

    @Inject(method = "func_190901_a", at = @At("HEAD"))
    void antiqueatlas_onTakeItem(PlayerEntity player, ItemStack atlas, CallbackInfoReturnable<ItemStack> info) {
        if (atlas.getItem() == AtlasAPI.getAtlasItem()) {
            ItemStack map = antiqueatlas_handler.inventorySlots.get(0).getStack();

            CartographyTableHooks.onTake(player, map, atlas);
        }
    }
}
