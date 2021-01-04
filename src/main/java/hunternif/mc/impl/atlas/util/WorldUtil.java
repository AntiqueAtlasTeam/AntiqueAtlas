package hunternif.mc.impl.atlas.util;

class WorldUtil {

    /**
     * Returns the village if the specified chunk overlays its territory.
     */
	/* public static VillageProperties getVillageInChunk(World world, Chunk chunk) {
		int centerX = (chunk.getPos().x << 4) + 8;
		int centerZ = (chunk.getPos().z << 4) + 8;
		// TODO FABRIC: Optimize? No more per-chunk villages...
		List<VillageProperties> villages = world.getVillageManager().getVillages();
		for (VillageProperties village : villages) {
			BlockPos coords = village.getCenter();
			if ((centerX - coords.getX())*(centerX - coords.getX()) + (centerZ - coords.getZ())*(centerZ - coords.getZ())
					<= village.getRadius()*village.getRadius()) {
				return village;
			}
		}
		return null;
	} */

    /**
     * Returns true if the village has a door in the specified chunk.
     */
	/* public static boolean isVillageDoorInChunk(VillageProperties village, Chunk chunk) {
		int centerX = (chunk.getPos().x << 4) + 8;
		int centerZ = (chunk.getPos().z << 4) + 8;
		if (village.hasNoDoors()) {
			return true;
		}
		for (Object doorInfo : village.getDoors()) {
			BlockPos door = ((VillageDoor) doorInfo).getPosition();
			if ((centerX - door.getX())*(centerX - door.getX()) + (centerZ - door.getZ())*(centerZ - door.getZ())
					<= 10*10) {
				return true;
			}
		}
		return false;
	} */
}
