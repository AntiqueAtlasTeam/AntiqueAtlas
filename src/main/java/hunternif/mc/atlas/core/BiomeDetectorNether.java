package hunternif.mc.atlas.core;

import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;

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
		int biomesCount = Biome.REGISTRY.getKeys().size();
		int[] chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());
		int[] biomeOccurrences = new int[biomesCount];
		
		// The following important pseudo-biomes don't have IDs:
		int lavaOccurences = 0;
		int groundOccurences = 0;
		
		int hellID = Biome.getIdForBiome(Biomes.HELL);
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int biomeID = chunkBiomes[x << 4 | z];
				if (biomeID == hellID) {
					// The Nether!
					Block netherBlock = chunk.getBlockState(x, lavaSeaLevel, z).getBlock();
					if (netherBlock == Blocks.LAVA) {
						lavaOccurences += priorityLava;
					} else {
						netherBlock = chunk.getBlockState(x, airProbeLevel, z).getBlock();
						if (netherBlock == null || netherBlock == Blocks.AIR) {
							groundOccurences ++; // ground
						} else {
							biomeOccurrences[biomeID] ++; // cave walls
						}
					}
				} else {
					// In case there are custom biomes "modded in":
					if (biomeID >= 0 && biomeID < biomesCount && Biome.getBiomeForId(biomeID) != null) {
						biomeOccurrences[biomeID] += priorityForBiome(Biome.getBiomeForId(biomeID));
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
