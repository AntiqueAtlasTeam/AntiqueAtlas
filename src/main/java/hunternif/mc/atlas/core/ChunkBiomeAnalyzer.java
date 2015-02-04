package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.ByteUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ChunkBiomeAnalyzer {
	public static final int NOT_FOUND = -1;
	
	/** Biome used for occasional pools of water. */
	private static final int waterPoolBiomeID = BiomeGenBase.river.biomeID;
	/** Increment the counter for water biomes by this much during iteration.
	 * This is done so that water pools are more visible. */
	private static final int waterPoolMultiplier = 2;
	/** Increment the counter for water biomes by this much during iteration.
	 * This is done so that rivers are more likely to be connected. */
	private static final int waterMultiplier = 4;
	/** Increment the counter for beach biomes by this much during iteration.
	 * This is done so that beaches are more common and make the coastline more
	 * interesting, given the fact that water biomes have priority too. */
	private static final int beachMultiplier = 3;
	
	/** If no valid biome ID is found, returns {@link ChunkBiomeAnalyzer#NOT_FOUND}. */
	public int getMeanBiomeID(Chunk chunk) {
		BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
		int[] chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());
		int[] biomeOccurences = new int[biomes.length];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int biomeID = chunkBiomes[x << 4 | z];
				int y = chunk.getHeightValue(x, z);
				Block topBlock = chunk.getBlock(x, y-1, z);
				// Check if there's surface of water at (x, z), but not swamp
				if (topBlock != null && topBlock == Blocks.water &&
						biomeID != BiomeGenBase.swampland.biomeID &&
						biomeID != BiomeGenBase.swampland.biomeID + 128) {
					biomeOccurences[waterPoolBiomeID] += waterPoolMultiplier;
				}
				if (biomeID >= 0 && biomeID < biomes.length && biomes[biomeID] != null) {
					if (BiomeDictionary.isBiomeOfType(biomes[biomeID], Type.WATER)) {
						// Water is important to show connected rivers:
						biomeOccurences[biomeID] += waterMultiplier;
					} else if (BiomeDictionary.isBiomeOfType(biomes[biomeID], Type.BEACH)){
						biomeOccurences[biomeID] += beachMultiplier;
					} else {
						biomeOccurences[biomeID] ++;
					}
				}
			}
		}
		int meanBiomeId = NOT_FOUND;
		int meanBiomeOccurences = 0;
		for (int i = 0; i < biomeOccurences.length; i++) {
			if (biomeOccurences[i] > meanBiomeOccurences) {
				meanBiomeId = i;
				meanBiomeOccurences = biomeOccurences[i];
			}
		}
		return meanBiomeId;
	}
}
