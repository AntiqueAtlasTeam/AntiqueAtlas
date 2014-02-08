package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.api.AtlasAPI;

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
			List<Village> villages = event.world.villageCollectionObj.getVillageList();
			for (Village village : villages) {
				// Cover village territory:
				for (int dx = -village.getVillageRadius(); dx <= village.getVillageRadius(); dx += 16) {
					for (int dz = -village.getVillageRadius(); dz <= village.getVillageRadius(); dz += 16) {
						// Fill only the inside of the circle:
						if (dx*dx + dz*dz > village.getVillageRadius()*village.getVillageRadius()) {
							continue;
						}
						AtlasAPI.getTileAPI().putCustomTile(event.world, 0, ExtTileIdMap.TILE_VILLAGE_TERRITORY,
								(village.getCenter().posX + dx) >> 4,
								(village.getCenter().posZ + dz) >> 4);
						data.markDirty();
					}
				}
				// Cover doors with houses:
			}
		}
	}
	
	public ExtBiomeData getData() {
		return data;
	}
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		data.syncOnPlayer(player);
		ExtTileIdMap.instance().syncOnPlayer(player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {}

}
