package hunternif.mc.impl.atlas.item;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileInfo;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.s2c.play.DimensionUpdateS2CPacket;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Collection;

public class AtlasItem extends Item {

    public AtlasItem(Item.Settings settings) {
        super(settings);
    }

    public static int getAtlasID(ItemStack stack) {
        return stack.getOrCreateTag().getInt("atlasID");
    }

    @Override
    public Text getName(ItemStack stack) {
        return new TranslatableText(this.getTranslationKey(), getAtlasID(stack));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);

        if (world.isClient) {
            AntiqueAtlasModClient.openAtlasGUI(stack);
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            return super.useOnBlock(context);
        }

        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        if (blockState.isIn(BlockTags.BANNERS)) {
            AntiqueAtlasModClient.openAtlasGUI(context.getStack());
            MapBannerMarker mapBannerMarker = MapBannerMarker.fromWorldBlock(context.getWorld(), context.getBlockPos());
            AntiqueAtlasModClient.getAtlasGUI().openMarkerFinalizer(mapBannerMarker.getName());
            context.getWorld().playSound(context.getPlayer(), context.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1f, 1f);

            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
        AtlasData data = AntiqueAtlasMod.tileData.getData(stack, world);
        if (data == null || !(entity instanceof PlayerEntity)) return;

        int atlasId = getAtlasID(stack);

        // On the first run send the map from the server to the client:
        PlayerEntity player = (PlayerEntity) entity;
        if (!world.isClient && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
            data.syncOnPlayer(atlasId, player);
        }

        // Same thing with the local markers:
        MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
        if (!world.isClient && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
            markers.syncOnPlayer(atlasId, (ServerPlayerEntity) player);
        }

        // Updating map around player
        Collection<TileInfo> newTiles = AntiqueAtlasMod.worldScanner.updateAtlasAroundPlayer(data, player);

        if (!world.isClient) {
            if (!newTiles.isEmpty()) {
                new DimensionUpdateS2CPacket(atlasId, player.getEntityWorld().getRegistryKey(), newTiles).send((ServerWorld) world);
            }
        }
    }

}
