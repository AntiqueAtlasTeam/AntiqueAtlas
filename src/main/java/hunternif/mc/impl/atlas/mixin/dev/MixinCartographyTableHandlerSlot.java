package hunternif.mc.impl.atlas.mixin.dev;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.mixinhooks.CartographyTableHooks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.CartographyTableScreenHandler$4")
class MixinCartographyTableScreenHandlerSlot {

    @Inject(method = "canInsert", at = @At("RETURN"), cancellable = true)
    void antiqueatlas_canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(stack.getItem() == AtlasAPI.getAtlasItem() || info.getReturnValueZ());
    }
}

@Mixin(targets = "net.minecraft.screen.CartographyTableScreenHandler$5")
class MixinCartographyTableScreenHandlerResultSlot {

    CartographyTableScreenHandler antiqueatlas_handler;

    @Inject(method = "<init>", at = @At("TAIL"))
    void antiqueatlas_init(CartographyTableScreenHandler handler, Inventory inv, int a, int b, int c, ScreenHandlerContext context, CallbackInfo info) {
        antiqueatlas_handler = handler;
    }

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    void antiqueatlas_onTakeItem(PlayerEntity player, ItemStack atlas, CallbackInfoReturnable<ItemStack> info) {
        if (atlas.getItem() == AtlasAPI.getAtlasItem()) {
            ItemStack map = antiqueatlas_handler.slots.get(0).getStack();

            CartographyTableHooks.onTakeItem(player, map, atlas);
        }
    }
}
