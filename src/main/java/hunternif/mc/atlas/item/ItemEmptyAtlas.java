package hunternif.mc.atlas.item;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.MarkersData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemEmptyAtlas extends Item {
	public ItemEmptyAtlas(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player,
			Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (world.isClient)
			return new TypedActionResult<>(ActionResult.SUCCESS, stack);

		int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();
		ItemStack atlasStack = new ItemStack(RegistrarAntiqueAtlas.ATLAS);

		atlasStack.getOrCreateTag().putInt("atlasID", atlasID);

        AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
        atlasData.getDimensionData(player.getEntityWorld().getDimensionRegistryKey()).setBrowsingPosition(
                (int)Math.round(-player.getX() * AntiqueAtlasMod.CONFIG.userInterface.defaultScale),
                (int)Math.round(-player.getZ() * AntiqueAtlasMod.CONFIG.userInterface.defaultScale),
								AntiqueAtlasMod.CONFIG.userInterface.defaultScale);
        atlasData.markDirty();

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        markersData.markDirty();
		
		stack.decrement(1);
		if (stack.isEmpty()) {
			return new TypedActionResult<>(ActionResult.SUCCESS, atlasStack);
		} else {
			if (!player.inventory.insertStack(atlasStack.copy())) {
				player.dropItem(atlasStack, true);
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, stack);
		}
	}
}
