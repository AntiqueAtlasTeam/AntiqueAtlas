package hunternif.mc.atlas.core;

import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;
import hunternif.mc.atlas.util.WorldUtil;

/**
 * Detects seas of lava, cave ground and cave walls in the Nether.
 * @author Hunternif
 */
public class BiomeDetectorEnd extends BiomeDetectorBase implements IBiomeDetector {
	
	@Override
	public int getBiomeID(Chunk chunk) {
		int biomesCount = Biome.REGISTRY.getKeys().size();
		int[] chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());
		int[] biomeOccurrences = new int[biomesCount];
		
		// The following pseudo-biomes don't have IDs:
		int islandOccurences = 0;
		int plantOccurences = 0;
		int voidOccurences = 0;
		
		int endID = Biome.getIdForBiome(Biomes.SKY);
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int biomeID = chunkBiomes[x << 4 | z];
				if (biomeID == endID) {
					// The End!
					int top = chunk.getHeightValue(x, z);
					Block topBlock = chunk.getBlockState(x, top-1, z).getBlock();
					
					if(topBlock == Blocks.END_STONE) {
						islandOccurences++;
						Block rootBlock = chunk.getBlockState(x, top, z).getBlock();
						if(rootBlock == Blocks.CHORUS_FLOWER || rootBlock == Blocks.CHORUS_PLANT) {
							plantOccurences++;
						}
					}
					if(topBlock == Blocks.AIR) {
						voidOccurences++;
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
		if (meanBiomeOccurences < islandOccurences) {
			if(plantOccurences == 0)
				meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_END_ISLAND);
			else
				meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_END_ISLAND_PLANTS);
		} else if (meanBiomeOccurences < voidOccurences) {
			meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_END_VOID); 
		}
		
		return meanBiomeId;
	}
}
