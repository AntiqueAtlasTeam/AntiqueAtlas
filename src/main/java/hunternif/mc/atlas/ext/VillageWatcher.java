package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.api.AtlasAPI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class VillageWatcher {
	private final Set<Village> visited = new HashSet<Village>();
	
	/** Used to look for villages that have not been added to ExtBiomeData when
	 * they were generated. It could have happened in a previous version of the
	 * mod. */
	@ForgeSubscribe(priority=EventPriority.LOWEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote) {
			visitAllUnvisitedVillages(event.world);
		}
	}
	
	/** Used to look for newly spawned villages. */
	// This is still buggy, a freshly-generated village might not show up on an Atlas.
	@ForgeSubscribe
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
		// Cover village territory:
		for (int dx = -village.getVillageRadius(); dx <= village.getVillageRadius(); dx += 16) {
			for (int dz = -village.getVillageRadius(); dz <= village.getVillageRadius(); dz += 16) {
				// Fill only the inside of the circle:
				if (dx*dx + dz*dz > village.getVillageRadius()*village.getVillageRadius()) {
					continue;
				}
				AtlasAPI.getTileAPI().putCustomTile(world, 0, ExtTileIdMap.TILE_VILLAGE_TERRITORY,
						(village.getCenter().posX + dx) >> 4,
						(village.getCenter().posZ + dz) >> 4);
			}
		}
		// Cover doors with houses:
		for (Object doorInfo : village.getVillageDoorInfoList()) {
			VillageDoorInfo door = (VillageDoorInfo) doorInfo;
			AtlasAPI.getTileAPI().putCustomTile(world, 0, ExtTileIdMap.TILE_VILLAGE_HOUSE, door.posX >> 4, door.posZ >> 4);
			visited.add(village);
		}
	}
}
