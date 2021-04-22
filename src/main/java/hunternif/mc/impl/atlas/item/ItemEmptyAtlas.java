package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas(Item.Properties settings) {
		super(settings);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			world.playSound(player, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1F, 1F);
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}

		int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();
		ItemStack atlasStack = new ItemStack(RegistrarAntiqueAtlas.ATLAS);

		atlasStack.getOrCreateTag().putInt("atlasID", atlasID);

        AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, world);
        atlasData.getWorldData(player.getEntityWorld().getDimensionKey()).setBrowsingPositionTo(player);
        atlasData.markDirty();

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        markersData.markDirty();

		stack.shrink(1);
		if (stack.isEmpty()) {
			return new ActionResult<>(ActionResultType.SUCCESS, atlasStack);
		} else {
			if (!player.inventory.addItemStackToInventory(atlasStack.copy())) {
				player.dropItem(atlasStack, true);
			}

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
	}
}
