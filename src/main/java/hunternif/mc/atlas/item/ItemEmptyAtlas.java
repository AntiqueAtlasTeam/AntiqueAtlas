package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas() {
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) return stack;
		
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
		
		stack.stackSize--;
		if (stack.stackSize <= 0) {
			return atlasStack;
		} else {
			if (!player.inventory.addItemStackToInventory(atlasStack.copy())) {
				ForgeHooks.onPlayerTossEvent(player, atlasStack, false);
			}
			return stack;
		}
	}
}
