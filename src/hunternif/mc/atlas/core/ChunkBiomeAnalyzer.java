package hunternif.mc.atlas.core;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ChunkBiomeAnalyzer {
	public static class BiomeFlag {
		public static final int NONE = -1;
		//public static final int VILLAGE = -2;
	}
	
	public static final ChunkBiomeAnalyzer instance = new ChunkBiomeAnalyzer();
	
	public int getChunkBiomeID(Chunk chunk) {
		if (!chunk.isChunkLoaded) return BiomeFlag.NONE;
		int[] biomeOccurences = new int[256];
		for (int i = 0; i < chunk.getBiomeArray().length; i++) {
			int biomeId = chunk.getBiomeArray()[i];
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
		int meanBiomeId = BiomeFlag.NONE;
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
