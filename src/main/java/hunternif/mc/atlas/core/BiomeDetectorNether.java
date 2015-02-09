package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

/**
 * Detects seas of lava, cave ground and cave walls in the Nether.
 * @author Hunternif
 */
public class BiomeDetectorNether extends BiomeDetectorBase implements IBiomeDetector {
	/** The Nether will be checked for air/ground at this level. */
	private static final int airProbeLevel = 50;
	/** The Nether will be checked for lava at this level. */
	private static final int lavaSeaLevel = 31;
	
	/** Increment the counter for lava biomes by this much during iteration.
	 * This is done so that rivers are more likely to be connected. */
	private static final int priorityLava = 1;
	
	@Override
	public int getBiomeID(Chunk chunk) {
		BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
		int[] chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());
		int[] biomeOccurrences = new int[biomes.length];
		
		// The following important pseudo-biomes don't have IDs:
		int lavaOccurences = 0;
		int groundOccurences = 0;
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int biomeID = chunkBiomes[x << 4 | z];
				if (biomeID == BiomeGenBase.hell.biomeID) {
					// The Nether!
					Block netherBlock = chunk.getBlock(x, lavaSeaLevel, z);
					if (netherBlock == Blocks.lava) {
						lavaOccurences += priorityLava;
					} else {
						netherBlock = chunk.getBlock(x, airProbeLevel, z);
						if (netherBlock == null || netherBlock == Blocks.air) {
							groundOccurences ++; // ground
						} else {
							biomeOccurrences[biomeID] ++; // cave walls
						}
					}
				} else {
					// In case there are custom biomes "modded in":
					if (biomeID >= 0 && biomeID < biomes.length && biomes[biomeID] != null) {
						biomeOccurrences[biomeID] += priorityForBiome(biomes[biomeID]);
					}
				}
			}
		}
		int meanBiomeId = NOT_FOUND;
		int meanBiomeOccurences = 0;
		for (int i = 0; i < biomeOccurrences.length; i++) {
			if (biomeOccurrences[i] > meanBiomeOccurences) {
				meanBiomeId = i;
				meanBiomeOccurences = biomeOccurrences[i];
			}
		}
		
		// The following important pseudo-biomes don't have IDs:
		if (meanBiomeOccurences < lavaOccurences) {
			meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_LAVA);
		} else if (meanBiomeOccurences < groundOccurences) {
			meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_LAVA_SHORE);
		}
		
		return meanBiomeId;
	}
}
