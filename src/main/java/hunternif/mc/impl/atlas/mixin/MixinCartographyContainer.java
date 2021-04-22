package hunternif.mc.impl.atlas.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hunternif.mc.api.AtlasAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.storage.MapData;

@Mixin(CartographyContainer.class)
public abstract class MixinCartographyContainer extends Container {
	@Shadow
	CraftResultInventory field_217001_f;
	@Shadow
	IWorldPosCallable worldPosCallable;

	protected MixinCartographyContainer(@Nullable ContainerType<?> type, int syncId) {
		super(type, syncId);
	}

	//TODO: Figure out why this is not loading on forge
	//    @Inject(method = "func_216996_a", at = @At("HEAD"), cancellable = true)
	//    void antiqueatlas_call(ItemStack atlas, ItemStack map, ItemStack result, World world, BlockPos pos, CallbackInfo info) {
	//        if (atlas.getItem() == AtlasAPI.getAtlasItem() && map.getItem() == Items.FILLED_MAP) {
	//            field_217001_f.setInventorySlotContents(2, atlas.copy());
	//
	//            this.detectAndSendChanges();
	//
	//            info.cancel();
	//        }
	//    }

	//I know this is not proper, but this is a working alternative. On my honor i must make the function above work so that i can remove this
	@Overwrite
	private void func_216993_a(ItemStack stack, ItemStack p_216993_2_, ItemStack p_216993_3_) {
		this.worldPosCallable.consume((p_216996_4_, p_216996_5_) -> {
			if (p_216993_2_.getItem() == AtlasAPI.getAtlasItem() && stack.getItem() == Items.FILLED_MAP) {
				field_217001_f.setInventorySlotContents(2, p_216993_2_.copy());
				this.detectAndSendChanges();
			} else {
				Item item = p_216993_2_.getItem();
				MapData mapdata = FilledMapItem.getData(stack, p_216996_4_);
				if (mapdata != null) {
					ItemStack itemstack;
					if (item == Items.PAPER && !mapdata.locked && mapdata.scale < 4) {
						itemstack = stack.copy();
						itemstack.setCount(1);
						itemstack.getOrCreateTag().putInt("map_scale_direction", 1);
						this.detectAndSendChanges();
					} else if (item == Items.GLASS_PANE && !mapdata.locked) {
						itemstack = stack.copy();
						itemstack.setCount(1);
						itemstack.getOrCreateTag().putBoolean("map_to_lock", true);
						this.detectAndSendChanges();
					} else {
						if (item != Items.MAP) {
							this.field_217001_f.removeStackFromSlot(2);
							this.detectAndSendChanges();
							return;
						}

						itemstack = stack.copy();
						itemstack.setCount(2);
						this.detectAndSendChanges();
					}

					if (!ItemStack.areItemStacksEqual(itemstack, p_216993_3_)) {
						this.field_217001_f.setInventorySlotContents(2, itemstack);
						this.detectAndSendChanges();
					}

				}
			}
		});
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