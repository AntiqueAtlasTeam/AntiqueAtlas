package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.mixinhooks.CartographyTableHooks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

//TODO Verify if the methods in this yarn methods in this class actually equal the MCP versions with Linkie
@Mixin(targets = "net.minecraft.inventory.container.CartographyContainer$4")
class MixinCartographyContainerSlot {

    @Inject(method = "isItemValid"/*"canInsert"*/, at = @At("RETURN"), cancellable = true)
    void antiqueatlas_canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(stack.getItem() == AtlasAPI.getAtlasItem() || info.getReturnValueZ());
    }
}

@Mixin(CartographyContainer.class)
public abstract class MixinCartographyContainer extends Container {
    @Shadow
    CraftResultInventory field_217001_f;

    protected MixinCartographyContainer(@Nullable ContainerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "func_216996_a", at = @At("HEAD"), cancellable = true)
    void antiqueatlas_call(ItemStack atlas, ItemStack map, ItemStack result, World world, BlockPos pos, CallbackInfo info) {
        if (atlas.getItem() == AtlasAPI.getAtlasItem() && map.getItem() == Items.FILLED_MAP) {
            field_217001_f.setInventorySlotContents(2, atlas.copy());

            this.detectAndSendChanges();

            info.cancel();
        }
    }

    @Inject(method = "transferStackInSlot", at = @At("HEAD"), cancellable = true)
    void antiqueatlas_transferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> info) {
        if (index >= 0 && index <= 2) return;

        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();

            if (stack.getItem() != AtlasAPI.getAtlasItem()) return;

            boolean result = this.mergeItemStack(stack, 0, 2, false);

            if (!result) {
                info.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

}

@Mixin(targets = "net.minecraft.inventory.container.CartographyContainer$5")
class MixinCartographyContainerResultSlot {

    CartographyContainer antiqueatlas_handler;

    @Inject(method = "<init>", at = @At("TAIL"))
    void antiqueatlas_init(CartographyContainer handler, IInventory inv, int a, int b, int c, IWorldPosCallable context, CallbackInfo info) {
        antiqueatlas_handler = handler;
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    void antiqueatlas_onTakeItem(PlayerEntity player, ItemStack atlas, CallbackInfoReturnable<ItemStack> info) {
        if (atlas.getItem() == AtlasAPI.getAtlasItem()) {
            ItemStack map = antiqueatlas_handler.inventorySlots.get(0).getStack();

            CartographyTableHooks.onTakeItem(player, map, atlas);
        }
    }
}