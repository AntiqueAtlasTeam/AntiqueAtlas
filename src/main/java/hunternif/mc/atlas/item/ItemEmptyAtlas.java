package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas() {
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(AntiqueAtlasMod.ID + ":" + getUnlocalizedName().substring("item.".length()));
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) return stack;
		
		int atlasID = world.getUniqueDataId(ItemAtlas.WORLD_ATLAS_DATA_ID);
		ItemStack atlasStack = new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID);
		
		String atlasKey = AntiqueAtlasMod.itemAtlas.getAtlasDataKey(atlasID);
		AtlasData atlasData = new AtlasData(atlasKey);
		world.setItemData(atlasKey, atlasData);
		
		String markersKey = AntiqueAtlasMod.itemAtlas.getMarkersDataKey(atlasID);
		MarkersData markersData = new MarkersData(markersKey);
		world.setItemData(markersKey, markersData);
		
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
