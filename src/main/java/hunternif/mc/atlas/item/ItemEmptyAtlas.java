package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemEmptyAtlas extends Item {

	public ItemEmptyAtlas() {
	    setUnlocalizedName("emptyAntiqueAtlas");
        setCreativeTab(CreativeTabs.TOOLS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player,
			EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote)
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		
		int atlasID = world.getUniqueDataId(ItemAtlas.WORLD_ATLAS_DATA_ID);
		ItemStack atlasStack = new ItemStack(RegistrarAntiqueAtlas.ATLAS, 1, atlasID);

        AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
        atlasData.getDimensionData(player.dimension).setBrowsingPosition(
                (int)Math.round(-player.posX * SettingsConfig.userInterface.defaultScale),
                (int)Math.round(-player.posZ * SettingsConfig.userInterface.defaultScale),
                SettingsConfig.userInterface.defaultScale);
        atlasData.markDirty();

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        markersData.markDirty();
		
		stack.shrink(1);
		if (stack.isEmpty()) {
			return new ActionResult<>(EnumActionResult.SUCCESS, atlasStack);
		} else {
			if (!player.inventory.addItemStackToInventory(atlasStack.copy())) {
				ForgeHooks.onPlayerTossEvent(player, atlasStack, false);
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
	}
}
