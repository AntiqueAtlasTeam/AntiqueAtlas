package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAtlas extends Item {
	static final String WORLD_ATLAS_DATA_ID = "aAtlas";

	public ItemAtlas() {
		setHasSubtypes(true);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return super.getItemStackDisplayName(stack) + " #" + stack.getItemDamage();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn,
			EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);

		if (world.isRemote) {
			AntiqueAtlasMod.proxy.openAtlasGUI(stack);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
		AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
		if (data == null || !(entity instanceof EntityPlayer)) return;

		// On the first run send the map from the server to the client:
		EntityPlayer player = (EntityPlayer) entity;
		if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
			data.syncOnPlayer(stack.getItemDamage(), player);
		}

		// Same thing with the local markers:
		MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
		if (!world.isRemote && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
			markers.syncOnPlayer(stack.getItemDamage(), player);
		}

		// Updating map around player
		data.updateMapAroundPlayer(player);
	}

}
