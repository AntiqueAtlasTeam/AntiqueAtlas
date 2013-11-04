package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.MapTileStitcher;
import hunternif.mc.atlas.core.AtlasData;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas(int id) {
		super(id);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(AntiqueAtlasMod.ID + ":" + getUnlocalizedName().substring("item.".length()));
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		int atlasID = world.getUniqueDataId(ItemAtlas.WORLD_DATA_ID);
		ItemStack atlasStack = new ItemStack(AntiqueAtlasMod.itemAtlas, 1, atlasID);
		
		String key = AntiqueAtlasMod.itemAtlas.getDataKey(atlasID);
		AtlasData data = new AtlasData(key);
		world.setItemData(key, data);
		if (world.isRemote) {
			data.setTileStitcher(MapTileStitcher.instance);
		}
		
		stack.stackSize--;
		if (stack.stackSize <= 0) {
			return atlasStack;
		} else {
			if (!player.inventory.addItemStackToInventory(atlasStack.copy())) {
				player.dropPlayerItem(atlasStack);
			}
			return stack;
		}
	}
}
