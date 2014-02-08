package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.util.ShortVec2;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.IPlayerTracker;

public class ExtBiomeDataHandler implements IPlayerTracker {
	private static final String DATA_KEY = "aAtlasExtTiles";
	
	private ExtBiomeData data;
	
	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote) {
			data = (ExtBiomeData) event.world.loadItemData(ExtBiomeData.class, DATA_KEY);
			if (data == null) {
				data = new ExtBiomeData(DATA_KEY);
				data.markDirty();
				event.world.setItemData(DATA_KEY, data);
			}
			
			// Put all villages on the map:
			int houseID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_HOUSE);
			int territoryID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_TERRITORY);
			List<Village> villages = event.world.villageCollectionObj.getVillageList();
			for (Village village : villages) {
				// Cover village territory
				for (int dx = -village.getVillageRadius(); dx <= village.getVillageRadius(); dx += 16) {
					for (int dz = -village.getVillageRadius(); dz <= village.getVillageRadius(); dz += 16) {
						// Fill only the inside of the circle:
						if (dx*dx + dz*dz > village.getVillageRadius()*village.getVillageRadius()) {
							continue;
						}
						data.setBiomeIdAt(0, territoryID, new ShortVec2(
								(village.getCenter().posX + dx) >> 4,
								(village.getCenter().posZ + dz) >> 4));
						data.markDirty();
					}
				}
			}
		}
	}
	
	public ExtBiomeData getData() {
		return data;
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		data.syncOnPlayer(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {}

}
