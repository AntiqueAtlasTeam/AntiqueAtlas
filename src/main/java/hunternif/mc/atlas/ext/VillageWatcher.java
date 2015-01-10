package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class VillageWatcher {
	private final Set<Village> visited = new HashSet<Village>();
	
	/** Used to look for villages that have not been added to ExtBiomeData when
	 * they were generated. It could have happened in a previous version of the
	 * mod. */
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote) {
			visitAllUnvisitedVillages(event.world);
		}
	}
	
	/** Used to look for newly spawned villages. */
	// This is still buggy, a freshly-generated village might not show up on an Atlas.
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		if (!event.world.isRemote) {
			visitAllUnvisitedVillages(event.world);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void visitAllUnvisitedVillages(World world) {
		VillageCollection villageCollection = world.villageCollectionObj;
		if (villageCollection == null) return;
		for (Village village : (List<Village>) villageCollection.getVillageList()) {
			if (!visited.contains(village)) {
				visitVillage(world, village);
			}
		}
	}
	
	public void visitVillage(World world, Village village) {
		int dim = world.provider.dimensionId;
		// Using markers so that villages are visible at any scale.
		// Village center can move, so first check that there is no village
		// marker within the radius.
		MarkersData markersData = AntiqueAtlasMod.globalMarkersData.getData();
		boolean foundMarker = false;
		int centerX = village.getCenter().posX;
		int centerZ = village.getCenter().posZ;
		// Using custom pseudo-biome tiles to cover actual territory.
		// Cover village territory:
		for (int dx = -village.getVillageRadius(); dx <= village.getVillageRadius(); dx += 16) {
			for (int dz = -village.getVillageRadius(); dz <= village.getVillageRadius(); dz += 16) {
				// Fill only the inside of the circle:
				if (dx*dx + dz*dz > village.getVillageRadius()*village.getVillageRadius()) {
					continue;
				}
				int chunkX = (centerX + dx) >> 4;
				int chunkZ = (centerZ + dz) >> 4;
				// Villages tend to lose doors eventually, so let's not replace
				// the old ones with territory:
				int biomeID = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(dim, chunkX, chunkZ);
				if (biomeID != ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_HOUSE)) {
					AtlasAPI.getTileAPI().putCustomGlobalTile(world,
							ExtTileIdMap.TILE_VILLAGE_TERRITORY, chunkX, chunkZ);
				}
				
				Set<Marker> markers = markersData.getMarkersAtChunk(dim, chunkX, chunkZ);
				if (markers != null) {
					for (Marker marker : markers) {
						if (marker.getType().equals("village")) {
							foundMarker = true;
							break;
						}
					}
				}
			}
		}
		if (!foundMarker) {
			AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, "village",
					"gui.antiqueatlas.marker.village", // This label will be translated
					centerX, centerZ);
		}
		// Cover doors locations with houses:
		for (Object doorInfo : village.getVillageDoorInfoList()) {
			VillageDoorInfo door = (VillageDoorInfo) doorInfo;
			AtlasAPI.getTileAPI().putCustomGlobalTile(world, ExtTileIdMap.TILE_VILLAGE_HOUSE, door.posX >> 4, door.posZ >> 4);
		}
		visited.add(village);
	}
}
