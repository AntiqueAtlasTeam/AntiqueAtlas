package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

/**
 * Detects the 256 vanilla biomes, water pools and lava pools.
 * Water and beach biomes are given priority because shore line is the defining
 * feature of the map, and so that rivers are more connected.
 * @author Hunternif
 */
public class BiomeDetectorBase implements IBiomeDetector {
	/** Biome used for occasional pools of water. */
	private static final int waterPoolBiomeID = BiomeGenBase.river.biomeID;
	/** Increment the counter for water biomes by this much during iteration.
	 * This is done so that water pools are more visible. */
	private static final int priorityWaterPool = 3, prioritylavaPool = 6;
	
	protected int priorityForBiome(BiomeGenBase biome) {
		if (BiomeDictionary.isBiomeOfType(biome, Type.WATER)) {
			return 4;
		} else if (BiomeDictionary.isBiomeOfType(biome, Type.BEACH)) {
			return 3;
		} else {
			return 1;
		}
	}
	
	/** If no valid biome ID is found, returns {@link IBiomeDetector#NOT_FOUND}. */
	@Override
	public int getBiomeID(Chunk chunk) {
		BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
		int[] chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());
		int[] biomeOccurrences = new int[biomes.length];
		
		// The following important pseudo-biomes don't have IDs:
		int lavaOccurences = 0;
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int biomeID = chunkBiomes[x << 4 | z];
				int y = chunk.getHeightValue(x, z);
				if (y > 0) {
					Block topBlock = chunk.getBlock(x, y-1, z);
					// For some reason lava doesn't count in height value
					// TODO: check if 1.8 fixes this!
					Block topBlock2 = chunk.getBlock(x, y, z);
					// Check if there's surface of water at (x, z), but not swamp
					if (topBlock == Blocks.water &&
							biomeID != BiomeGenBase.swampland.biomeID &&
							biomeID != BiomeGenBase.swampland.biomeID + 128) {
						biomeOccurrences[waterPoolBiomeID] += priorityWaterPool;
					} else if (topBlock2 == Blocks.lava) {
						lavaOccurences += prioritylavaPool;
					}
				}
				if (biomeID >= 0 && biomeID < biomes.length && biomes[biomeID] != null) {
					biomeOccurrences[biomeID] += priorityForBiome(biomes[biomeID]);
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
			return ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_LAVA);
		}
		
		return meanBiomeId;
	}
}
