package hunternif.mc.atlas.util;

import java.util.List;

import net.minecraft.util.ChunkCoordinates;
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
		@SuppressWarnings("unchecked")
		List<Village> villages = chunk.worldObj.villageCollectionObj.getVillageList();
		for (Village village : villages) {
			ChunkCoordinates coords = village.getCenter();
			if ((centerX - coords.posX)*(centerX - coords.posX) + (centerZ - coords.posZ)*(centerZ - coords.posZ)
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
			VillageDoorInfo door = (VillageDoorInfo) doorInfo;
			if ((centerX - door.posX)*(centerX - door.posX) + (centerZ - door.posZ)*(centerZ - door.posZ)
					<= 10*10) {
				return true;
			}
		}
		return false;
	}
}
