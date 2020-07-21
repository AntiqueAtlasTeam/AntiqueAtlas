package hunternif.mc.atlas.item;

import java.util.Collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.TileInfo;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.DimensionUpdatePacket;

public class ItemAtlas extends Item {
	static final String WORLD_ATLAS_DATA_ID = "aAtlas";

	public ItemAtlas(Item.Settings settings) {
		super(settings);
	}

	public int getAtlasID(ItemStack stack) {
		return stack.getOrCreateTag().getInt("atlasID");
	}

	@Override
	public Text getName(ItemStack stack) {
		return ((MutableText)super.getName(stack)).append(" #" + getAtlasID(stack));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerIn,
			Hand hand) {
		ItemStack stack = playerIn.getStackInHand(hand);

		if (world.isClient) {
			AntiqueAtlasMod.proxy.openAtlasGUI(stack);
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
		AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
		if (data == null || !(entity instanceof PlayerEntity)) return;

		int atlasId = ((ItemAtlas) stack.getItem()).getAtlasID(stack);

		// On the first run send the map from the server to the client:
		PlayerEntity player = (PlayerEntity) entity;
		if (!world.isClient && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
			data.syncOnPlayer(atlasId, player);
		}

		// Same thing with the local markers:
		MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
		if (!world.isClient && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
			markers.syncOnPlayer(atlasId, player);
		}

		// Updating map around player
		Collection<TileInfo> newTiles = data.updateMapAroundPlayer(player);
		
		if (!world.isClient) {
			if (!newTiles.isEmpty()) {
				DimensionUpdatePacket packet = new DimensionUpdatePacket(atlasId, player.world.getDimensionRegistryKey(),
																		 newTiles);
				PacketDispatcher.sendToAll(((ServerWorld) world).getServer(), packet);
			}
		}
	}

}
