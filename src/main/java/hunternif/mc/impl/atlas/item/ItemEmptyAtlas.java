package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
        if (world.isClient) {
            world.playSound(player, player.getBlockPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1F, 1F);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }

        int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();
        ItemStack atlasStack = new ItemStack(RegistrarAntiqueAtlas.ATLAS);

        atlasStack.getOrCreateNbt().putInt("atlasID", atlasID);

        AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, world);
        atlasData.getWorldData(player.getEntityWorld().getRegistryKey()).setBrowsingPositionTo(player);
        atlasData.markDirty();

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        markersData.markDirty();

        stack.decrement(1);
        if (stack.isEmpty()) {
            return new TypedActionResult<>(ActionResult.SUCCESS, atlasStack);
        } else {
            if (!player.getInventory().insertStack(atlasStack.copy())) {
                player.dropItem(atlasStack, true);
            }

            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
    }
}
