package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.api.AtlasAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CartographyTableMenu.class)
public abstract class MixinCartographyTableScreenHandler extends AbstractContainerMenu {

    @Shadow
    ResultContainer resultContainer;

    protected MixinCartographyTableScreenHandler(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "lambda$setupResultSlot$0", at = @At("HEAD"), cancellable = true)
    void antiqueatlas_call(ItemStack map, ItemStack atlas, ItemStack result, Level world, BlockPos pos, CallbackInfo info) {
        if (atlas.getItem() == AtlasAPI.getAtlasItem() && map.getItem() == Items.FILLED_MAP) {
            this.resultContainer.setItem(CartographyTableMenu.RESULT_SLOT, atlas.copy());

            this.broadcastChanges();

            info.cancel();
        }
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    void antiqueatlas_transferSlot(Player player, int index, CallbackInfoReturnable<ItemStack> info) {
        if (index >= 0 && index <= 2) return;

        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();

            if (stack.getItem() != AtlasAPI.getAtlasItem()) return;

            boolean result = this.moveItemStackTo(stack, 0, 2, false);

            if (!result) {
                info.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

}
