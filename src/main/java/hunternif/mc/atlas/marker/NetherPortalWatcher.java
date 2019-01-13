package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.DummyWorldAccess;
import hunternif.mc.atlas.util.Log;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	private final Map<Integer, Integer> teleportingPlayersOrigin = new ConcurrentHashMap<Integer, Integer>();
	
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
			if (teleportingPlayersOrigin.containsKey(entity.getEntityId())) {
				int origin = teleportingPlayersOrigin.remove(entity.getEntityId());
				Log.info("Entering");
				// player.dimension is the destination dimension
				int dimension = player.dimension;

				// Only look for portals into and out of the Nether
				if(origin == DimensionType.NETHER.getId() || dimension == DimensionType.NETHER.getId()) {
					Log.info("Player %s teleported to the %s", player.getGameProfile().getName(),
							 DimensionType.getById(dimension).getName());
					addPortalMarkerIfNone(player, dimension);
				}
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
				int originDimension = player.getEntityWorld().provider.getDimension();
				Log.info("Player %s left the %s", player.getGameProfile().getName(),
						 DimensionType.getById(originDimension).getName());
				teleportingPlayersOrigin.put(entity.getEntityId(), originDimension);
				// TODO Check what is the target dimension
				addPortalMarkerIfNone(player, originDimension);
			}
		}
	}
	
	/** Put the Portal marker at the player's current coordinates into all
	 * atlases that he is carrying, if the same marker is not already there. */
	private void addPortalMarkerIfNone(EntityPlayer player, int dimension) {
		if(!SettingsConfig.gameplay.autoNetherPortalMarkers) {
			return;
		}

		// Due to switching dimensions this player entity's worldObj is lagging.
		// We need the very specific dimension each time.
		World world = AntiqueAtlasMod.proxy.getServer().getWorld(dimension);

		if (!SettingsConfig.gameplay.itemNeeded) {
			addPortalMarkerIfNone(player, world, dimension, player.getUniqueID().hashCode());
			return;
		}

		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack == null || stack.getItem() != RegistrarAntiqueAtlas.ATLAS) continue;

			addPortalMarkerIfNone(player, world, dimension, stack.getItemDamage());
		}
	}

	private void addPortalMarkerIfNone(EntityPlayer player, World world, int dimension, int atlasID) {
		// Can't use entity.dimension here, because its value has already been updated!
		DimensionMarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world)
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
		AtlasAPI.markers.putMarker(world, false, atlasID, MarkerTypes.NETHER_PORTAL.getRegistryName().toString(), "gui.antiqueatlas.marker.netherPortal", x, z);
	}
	
	private static boolean isEntityInPortal(Entity entity) {
		return ObfuscationReflectionHelper.getPrivateValue(Entity.class, entity, inPortalFieldNames);
	}
}
