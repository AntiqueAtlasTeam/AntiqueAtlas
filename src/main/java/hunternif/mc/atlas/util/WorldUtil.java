package hunternif.mc.atlas.util;

import java.util.List;

import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.chunk.Chunk;

public class WorldUtil {
	/**
	 * Returns the village if the specified chunk overlays its territory.
	 */
	public static Village getVillageInChunk(Chunk chunk) {
		int centerX = (chunk.xPosition << 4) + 8;
		int centerZ = (chunk.zPosition << 4) + 8;
		List<Village> villages = chunk.getWorld().villageCollectionObj.getVillageList();
		for (Village village : villages) {
			BlockPos coords = village.getCenter();
			if ((centerX - coords.getX())*(centerX - coords.getX()) + (centerZ - coords.getZ())*(centerZ - coords.getZ())
					<= village.getVillageRadius()*village.getVillageRadius()) {
				return village;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if the village has a door in the specified chunk.
	 */
	public static boolean isVillageDoorInChunk(Village village, Chunk chunk) {
		int centerX = (chunk.xPosition << 4) + 8;
		int centerZ = (chunk.zPosition << 4) + 8;
		if (village.isAnnihilated()) {
			return true;
		}
		for (Object doorInfo : village.getVillageDoorInfoList()) {
			BlockPos door = ((VillageDoorInfo) doorInfo).getDoorBlockPos();
			if ((centerX - door.getX())*(centerX - door.getX()) + (centerZ - door.getZ())*(centerZ - door.getZ())
					<= 10*10) {
				return true;
			}
		}
		return false;
	}
}
