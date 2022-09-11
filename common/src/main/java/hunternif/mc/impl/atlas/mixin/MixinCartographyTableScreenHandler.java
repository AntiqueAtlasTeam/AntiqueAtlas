package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.api.AtlasAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CartographyTableScreenHandler.class)
public abstract class MixinCartographyTableScreenHandler extends ScreenHandler {

    @Final
    @Shadow
    private CraftingResultInventory resultInventory;

    protected MixinCartographyTableScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    // inject into lambda inside CartographyTableScreenHandler::updateResult
    @Inject(method = {"lambda$setupResultSlot$0", "method_17382", "m_39166_"}, at = @At("HEAD"), cancellable = true)
    void antiqueatlas_call(ItemStack map, ItemStack atlas, ItemStack result, World world, BlockPos pos, CallbackInfo info) {
        if (atlas.getItem() == AtlasAPI.getAtlasItem() && map.getItem() == Items.FILLED_MAP) {
            this.resultInventory.setStack(CartographyTableScreenHandler.RESULT_SLOT_INDEX, atlas.copy());

            this.sendContentUpdates();

            info.cancel();
        }
    }

    @Inject(method = "transferSlot", at = @At("HEAD"), cancellable = true)
    void antiqueatlas_transferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> info) {
        if (index >= 0 && index <= 2) return;

        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack stack = slot.getStack();

            if (stack.getItem() != AtlasAPI.getAtlasItem()) return;

            boolean result = this.insertItem(stack, 0, 2, false);

            if (!result) {
                info.setReturnValue(ItemStack.EMPTY);
            }
        }
    }

}
