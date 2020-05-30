package hunternif.mc.atlas.item;

import java.util.Collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.TileInfo;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.DimensionUpdatePacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

public class ItemAtlas extends Item {
	static final String WORLD_ATLAS_DATA_ID = "aAtlas";

	public ItemAtlas(Item.Properties properties) {
		super(properties);
	}

	public int getAtlasID(ItemStack stack) {
		return stack.getOrCreateTag().getInt("atlasID");
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return super.getDisplayName(stack).appendText(" #" + getAtlasID(stack));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			AntiqueAtlasMod.proxy.openAtlasGUI(stack);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isEquipped) {
		AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
		if (data == null || !(entity instanceof PlayerEntity)) return;

		int atlasId = ((ItemAtlas) stack.getItem()).getAtlasID(stack);

		// On the first run send the map from the server to the client:
		PlayerEntity player = (PlayerEntity) entity;
		if (!world.isRemote && !data.isSyncedOnPlayer(player) && !data.isEmpty()) {
			data.syncOnPlayer(atlasId, player);
		}

		// Same thing with the local markers:
		MarkersData markers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
		if (!world.isRemote && !markers.isSyncedOnPlayer(player) && !markers.isEmpty()) {
			markers.syncOnPlayer(atlasId, player);
		}

		// Updating map around player
		Collection<TileInfo> newTiles = data.updateMapAroundPlayer(player);
		
		if (!world.isRemote) {
			if (!newTiles.isEmpty()) {
				DimensionUpdatePacket packet = new DimensionUpdatePacket(atlasId, player.dimension, newTiles);
				for (TileInfo t : newTiles) {
					packet.addTile(t.x, t.z, t.biome);
				}
				PacketDispatcher.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
			}
		}
	}

}
