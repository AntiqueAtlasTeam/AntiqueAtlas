package hunternif.mc.atlas.core;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ChunkBiomeAnalyzer {
	public static final int NOT_FOUND = -1;
	
	/** If no valid biome ID is found, returns {@link ChunkBiomeAnalyzer#NOT_FOUND}. */
	public int getMeanBiomeID(int[] biomeIDs) {
		BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
		int[] biomeOccurences = new int[biomes.length];
		for (int i = 0; i < biomes.length; i++) {
			int biomeId = biomeIDs[i];
			if (biomeId >= 0 && biomeId < biomes.length && biomes[biomeId] != null) {
				if (BiomeDictionary.isBiomeOfType(biomes[biomeId], Type.WATER)) {
					// Water is important to show connected rivers:
					biomeOccurences[biomeId] += 4;
				} else if (BiomeDictionary.isBiomeOfType(biomes[biomeId], Type.SWAMP)) {
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
