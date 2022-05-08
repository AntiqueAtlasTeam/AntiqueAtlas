package hunternif.mc.impl.atlas.mixin.prod;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.mixinhooks.CartographyTableHooks;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.CartographyTableMenu$4")
class MixinCartographyTableScreenHandlerSlot {

    @Inject(method = "m_5857_", at = @At("RETURN"), cancellable = true)
    void antiqueatlas_canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(stack.getItem() == AtlasAPI.getAtlasItem() || info.getReturnValueZ());
    }
}

@Mixin(targets = "net.minecraft.world.inventory.CartographyTableMenu$5")
class MixinCartographyTableScreenHandlerResultSlot {

    CartographyTableMenu antiqueatlas_handler;

    @Inject(method = "<init>", at = @At("TAIL"))
    void antiqueatlas_init(CartographyTableMenu handler, Container inv, int a, int b, int c, ContainerLevelAccess context, CallbackInfo info) {
        antiqueatlas_handler = handler;
    }

    @Inject(method = "m_142406_", at = @At("HEAD"))
    void antiqueatlas_onTakeItem(Player player, ItemStack atlas, CallbackInfo info) {
        if (atlas.getItem() == AtlasAPI.getAtlasItem()) {
            ItemStack map = antiqueatlas_handler.slots.get(0).getItem();

            CartographyTableHooks.onTakeItem(player, map, atlas);
        }
    }
}
