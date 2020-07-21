package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.s2c.play.AtlasCreateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
        atlasData.getWorldData(player.getEntityWorld().getRegistryKey()).setBrowsingPositionTo(player);
        atlasData.markDirty();

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        markersData.markDirty();

		new AtlasCreateS2CPacket().send((ServerPlayerEntity) player);

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
