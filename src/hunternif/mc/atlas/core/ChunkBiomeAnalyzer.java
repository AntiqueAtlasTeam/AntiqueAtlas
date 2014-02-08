package hunternif.mc.atlas.core;

import hunternif.mc.atlas.util.ByteUtil;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ChunkBiomeAnalyzer {
	public static final int NOT_FOUND = -1;
	public static final ChunkBiomeAnalyzer instance = new ChunkBiomeAnalyzer();
	
	/** If biome is not found or the chunk is not loaded, returns -1. */
	public int getChunkBiomeID(Chunk chunk) {
		if (!chunk.isChunkLoaded) return NOT_FOUND;
		int[] biomeOccurences = new int[256];
		for (int i = 0; i < chunk.getBiomeArray().length; i++) {
			int biomeId = ByteUtil.unsignedByteToInt(chunk.getBiomeArray()[i]);
			if (biomeId >= 0 && BiomeGenBase.biomeList[biomeId] != null) {
				if (BiomeDictionary.isBiomeOfType(BiomeGenBase.biomeList[biomeId], Type.WATER)) {
					// Water is important to show connected rivers:
					biomeOccurences[biomeId] += 4;
				} else if (BiomeDictionary.isBiomeOfType(BiomeGenBase.biomeList[biomeId], Type.SWAMP)) {
					// Swamps are often intermingled with water, but they look
					// like water themselves, so they take precedence:
					biomeOccurences[biomeId] += 6;
				} else {
					biomeOccurences[biomeId] ++;
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
