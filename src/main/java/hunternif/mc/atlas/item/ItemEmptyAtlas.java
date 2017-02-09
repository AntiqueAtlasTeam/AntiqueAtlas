package hunternif.mc.atlas.item;

import net.minecraftforge.common.ForgeHooks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas() {
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player,
			EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote)
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		
		int atlasID = world.getUniqueDataId(ItemAtlas.WORLD_ATLAS_DATA_ID);
		ItemStack atlasStack = new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID);
		
		AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
		atlasData.getDimensionData(player.dimension).setBrowsingPosition(
				(int)Math.round(-player.posX * AntiqueAtlasMod.settings.defaultScale),
				(int)Math.round(-player.posZ * AntiqueAtlasMod.settings.defaultScale),
				AntiqueAtlasMod.settings.defaultScale);
		atlasData.markDirty();
		
		MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
		markersData.markDirty();
		
		stack.shrink(1);
		if (stack.isEmpty()) {
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, atlasStack);
		} else {
			if (!player.inventory.addItemStackToInventory(atlasStack.copy())) {
				ForgeHooks.onPlayerTossEvent(player, atlasStack, false);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
	}
}
