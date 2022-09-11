package hunternif.mc.impl.atlas.marker;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.mixinhooks.EntityHooksAA;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Identifies when a player teleports in or out of the nether and puts a portal
 * marker in the atlases he is carrying.
 * @author Hunternif
 */
public class NetherPortalWatcher {
	/**
	 * When a player teleports, he is removed from the source dimension, where
	 * portal detection works well, and his ID is placed in this set.
	 * Then the player is spawned in the destination dimension, where portal
	 * detection doesn't work for some reason. But we can detect his arrival
	 * by checking if this set contains the player's ID!
	 */
	private final Map<Integer, DimensionType> teleportingPlayersOrigin = new ConcurrentHashMap<>();

	// TODO FABRIC
	/* @SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().G) {
			event.getWorld().a(this);
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if (!event.getWorld().G) {
			event.getWorld().b(this);
		}
	}

	@Override
	public void a(Entity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (teleportingPlayersOrigin.containsKey(entity.Q())) {
				int origin = teleportingPlayersOrigin.remove(entity.Q());
				Log.info("Entering");
				// player.dimension is the destination dimension
				DimensionType dimension = player.dimension;

				// Only look for portals into and out of the Nether
				if(origin == bnu.b.a() || dimension == bnu.b.a()) {
					Log.info("Player %s teleported to the %s", player.getCommandSource().getName(),
							 bnu.a(dimension).b());
					addPortalMarkerIfNone(player, dimension);
				}
			}
		}
	}

	@Override
	public void b(Entity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (isEntityInPortal(entity)) {
				Log.info("Exiting");
				// player.worldObj.provider.dimensionId is the dimension of origin
				DimensionType originDimension = player.getEntityWorld().getDimension().getType();
				Log.info("Player %s left the %s", player.getCommandSource().getName(),
						Registry.DIMENSION_TYPE.getId(originDimension));
				teleportingPlayersOrigin.put(entity.getEntityId(), originDimension);
				// TODO Check what is the target dimension
				addPortalMarkerIfNone(player, originDimension);
			}
		}
	} */

	/** Put the Portal marker at the player's current coordinates into all
	 * atlases that he is carrying, if the same marker is not already there. */
	private void addPortalMarkerIfNone(PlayerEntity player) {
		if (!AntiqueAtlasMod.CONFIG.autoNetherPortalMarkers || player.getEntityWorld().isClient) {
			return;
		}

		// Due to switching dimensions this player entity's worldObj is lagging.
		// We need the very specific dimension each time.
		World world = player.getEntityWorld();

		if (!AntiqueAtlasMod.CONFIG.itemNeeded) {
			addPortalMarkerIfNone(player, world, player.getUuid().hashCode());
			return;
		}

		for (ItemStack stack : player.getInventory().main) {
			if (stack == null || !(stack.getItem() instanceof AtlasItem)) continue;

			addPortalMarkerIfNone(player, world, AtlasItem.getAtlasID(stack));
		}
	}

	private void addPortalMarkerIfNone(PlayerEntity player, World world, int atlasID) {
		MarkerType netherPortalType = MarkerType.REGISTRY.get(AntiqueAtlasMod.id("nether_portal"));
		if (netherPortalType == null) {
			return;
		}

		// Can't use entity.dimension here, because its value has already been updated!
		DimensionMarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world)
				.getMarkersDataInWorld(world.getRegistryKey());

		int x = (int)player.getX();
		int z = (int)player.getZ();

		// Check if the marker already exists:
		List<Marker> markers = data.getMarkersAtChunk((x >> 4) / MarkersData.CHUNK_STEP, (z >> 4) / MarkersData.CHUNK_STEP);
		if (markers != null) {
			for (Marker marker : markers) {
				if (marker.getType().equals("antiqueatlas:nether_portal")) {
					// Found the marker.
					return;
				}
			}
		}

		// Marker not found, place new one:
		AtlasAPI.getMarkerAPI().putMarker(world, false, atlasID, MarkerType.REGISTRY.getId(netherPortalType), new TranslatableText("gui.antiqueatlas.marker.netherPortal"), x, z);
	}

	private static boolean isEntityInPortal(Entity entity) {
		return ((EntityHooksAA) entity).antiqueAtlas_isInPortal();
	}
}
