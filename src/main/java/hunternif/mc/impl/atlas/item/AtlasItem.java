package hunternif.mc.impl.atlas.item;

import java.util.Collection;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.TileInfo;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.network.packet.s2c.play.DimensionUpdateS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AtlasItem extends Item {

	public AtlasItem(Item.Properties settings) {
		super(settings);
	}

	public static int getAtlasID(ItemStack stack) {
		return stack.getOrCreateTag().getInt("atlasID");
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return new TranslationTextComponent(this.getTranslationKey(), getAtlasID(stack));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack stack = playerEntity.getHeldItem(hand);

		if (world.isRemote) {
			AntiqueAtlasModClient.openAtlasGUI(stack);
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
		AtlasData data = AntiqueAtlasMod.tileData.getData(stack, world);
		if (data == null || !(entity instanceof PlayerEntity)) return;

		int atlasId = getAtlasID(stack);

		// On the first run send the map from the server to the client:
		PlayerEntity player = (PlayerEntity) entity;
		if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
			data.syncOnPlayer(atlasId, player);
		}

		// Same thing with the local markers:
		MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
		if (!world.isRemote && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
			markers.syncOnPlayer(atlasId, (ServerPlayerEntity) player);
		}

		// Updating map around player
		Collection<TileInfo> newTiles = AntiqueAtlasMod.worldScanner.updateAtlasAroundPlayer(data,player);
		
		if (!world.isRemote) {
			if (!newTiles.isEmpty()) {
				new DimensionUpdateS2CPacket(atlasId, player.getEntityWorld().getDimensionKey(), newTiles).send((ServerWorld) world);
			}
		}
	}

}
