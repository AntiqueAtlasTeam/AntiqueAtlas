package hunternif.mc.atlas.marker;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.DummyWorldAccess;
import hunternif.mc.atlas.util.Log;

/**
 * Identifies when a player teleports in or out of the nether and puts a portal
 * marker in the atlases he is carrying.
 * @author Hunternif
 */
public class NetherPortalWatcher extends DummyWorldAccess {
	private static final String[] inPortalFieldNames = {"inPortal", "field_71087_bX", "bX"};

	/**
	 * When a player teleports, he is removed from the source dimension, where
	 * portal detection works well, and his ID is placed in this set.
	 * Then the player is spawned in the destination dimension, where portal
	 * detection doesn't work for some reason. But we can detect his arrival
	 * by checking if this set contains the player's ID!
	 */
	private final Set<Integer> teleportingPlayerIDs = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.getWorld().isRemote) {
			event.getWorld().addEventListener(this);
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if (!event.getWorld().isRemote) {
			event.getWorld().removeEventListener(this);
		}
	}
	
	@Override
	public void onEntityAdded(Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (teleportingPlayerIDs.remove(entity.getEntityId())) {
				Log.info("Entering");
				// player.dimension is the destination dimension
				int dimension = player.dimension;
				Log.info("Player %s teleported to the %s", player.getGameProfile().getName(),
						dimension == 0 ? "Overworld" : "Nether");
				addPortalMarkerIfNone(player, dimension);
			}
		}
	}
	
	@Override
	public void onEntityRemoved(Entity entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (isEntityInPortal(entity)) {
				Log.info("Exiting");
				// player.worldObj.provider.dimensionId is the dimension of origin
				int dimension = player.getEntityWorld().provider.getDimension();
				Log.info("Player %s left the %s", player.getGameProfile().getName(),
						dimension == 0 ? "Overworld" : "Nether");
				teleportingPlayerIDs.add(entity.getEntityId());
				addPortalMarkerIfNone(player, dimension);
			}
		}
	}
	
	/** Put the Portal marker at the player's current coordinates into all
	 * atlases that he is carrying, if the same marker is not already there. */
	private void addPortalMarkerIfNone(EntityPlayer player, int dimension) {
		// Due to switching dimensions this player entity's worldObj is lagging.
		// We need the very specific dimension each time.
		World world = AntiqueAtlasMod.proxy.getServer().worldServerForDimension(dimension);
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack == null || stack.getItem() != AntiqueAtlasMod.itemAtlas) continue;
			// Can't use entity.dimension here, because its value has already been updated!
			DimensionMarkersData data = AntiqueAtlasMod.markersData.getMarkersData(stack, world)
					.getMarkersDataInDimension(dimension);
			int x = (int)player.posX;
			int z = (int)player.posZ;
			// Check if the marker already exists:
			List<Marker> markers = data.getMarkersAtChunk((x >> 4) / MarkersData.CHUNK_STEP, (z >> 4) / MarkersData.CHUNK_STEP);
			if (markers != null) {
				for (Marker marker : markers) {
					if (marker.getType().equals(MarkerTypes.NETHER_PORTAL)) {
						// Found the marker.
						return;
					}
				}
			}
			// Marker not found, place new one:
			AtlasAPI.markers.putMarker(world, false, stack.getItemDamage(), MarkerTypes.NETHER_PORTAL, "gui.antiqueatlas.marker.netherPortal", x, z);
		}
	}
	
	private static boolean isEntityInPortal(Entity entity) {
		return ObfuscationReflectionHelper.getPrivateValue(Entity.class, entity, inPortalFieldNames);
	}
}
