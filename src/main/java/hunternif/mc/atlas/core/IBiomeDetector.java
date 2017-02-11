package hunternif.mc.atlas.core;

import net.minecraft.world.chunk.Chunk;

/**
 * Finds the biome ID to be used for a given chunk.
 * @author Hunternif
 */
interface IBiomeDetector {
	int NOT_FOUND = -1;
	
	/** Finds the biome ID to be used for a given chunk. */
	int getBiomeID(Chunk chunk);
}
