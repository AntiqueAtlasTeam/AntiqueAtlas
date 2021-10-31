package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.marker.MarkersData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemEmptyAtlas extends Item {
    public ItemEmptyAtlas(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player,
                                            InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            world.playSound(player, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1F, 1F);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        int atlasID = AntiqueAtlasMod.getGlobalAtlasData(world).getNextAtlasId();
        ItemStack atlasStack = new ItemStack(RegistrarAntiqueAtlas.ATLAS);

        atlasStack.getOrCreateTag().putInt("atlasID", atlasID);

        AtlasData atlasData = AntiqueAtlasMod.tileData.getData(atlasID, world);
        atlasData.getWorldData(player.getCommandSenderWorld().dimension()).setBrowsingPositionTo(player);
        atlasData.setDirty();

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
        markersData.setDirty();

        stack.shrink(1);
        if (stack.isEmpty()) {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, atlasStack);
        } else {
            if (!player.getInventory().add(atlasStack.copy())) {
                player.drop(atlasStack, true);
            }

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
    }
}
