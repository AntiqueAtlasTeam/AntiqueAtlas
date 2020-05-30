package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote)
			return new ActionResult<>(ActionResultType.SUCCESS, stack);

		int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();
		ItemStack atlasStack = new ItemStack(RegistrarAntiqueAtlas.ATLAS);

		atlasStack.getOrCreateTag().putInt("atlasID", atlasID);

		AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
		atlasData.getDimensionData(player.dimension).setBrowsingPosition(
				(int)Math.round(-player.getPosX() * SettingsConfig.defaultScale),
				(int)Math.round(-player.getPosZ() * SettingsConfig.defaultScale),
				SettingsConfig.defaultScale);
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
