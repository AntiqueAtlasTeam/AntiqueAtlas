package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects seas of lava, cave ground and cave walls in the Nether.
 * @author Hunternif
 */
public class BiomeDetectorEnd extends BiomeDetectorBase implements IBiomeDetector {
	
	@Override
	public TileKind getBiomeID(World world, IChunk chunk) {
		BiomeContainer chunkBiomes = chunk.getBiomes();
		Map<Biome, Integer> biomeOccurrences = new HashMap<>(Registry.BIOME.keySet().size());
		
		// The following pseudo-biomes don't have IDs:
		int islandOccurences = 0;
		int plantOccurences = 0;
		int voidOccurences = 0;
		
		Biome endID = Biomes.THE_END;
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome biomeID = chunkBiomes.getNoiseBiome(x, 0, z);
				if (biomeID == endID) {
					// The End!
					int top = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).getHeight(x, z);
					BlockState topBlock = chunk.getBlockState(new BlockPos(x, top-1, z));

					if (topBlock.getBlock() == Blocks.END_STONE) {
						islandOccurences++;
						BlockState rootBlock = chunk.getBlockState(new BlockPos(x, top, z));
						if(rootBlock.getBlock() == Blocks.CHORUS_FLOWER || rootBlock.getBlock() == Blocks.CHORUS_PLANT) {
							plantOccurences++;
						}
					} else if (topBlock.isAir()) {
						voidOccurences++;
					}
				} else {
					// In case there are custom biomes "modded in":
					biomeOccurrences.put(biomeID,
						biomeOccurrences.getOrDefault(biomeID, 0) + priorityForBiome(biomeID)
					);
				}
			}
		}

		TileKind meanBiomeId = null;
		int meanBiomeOccurences = 0;
		for (Biome biome : biomeOccurrences.keySet()) {
			int occ = biomeOccurrences.get(biome);
			if (biomeOccurrences.get(biome) > meanBiomeOccurences) {
				meanBiomeId = TileKindFactory.get(biome);
				meanBiomeOccurences = occ;
			}
		}
		
		// The following important pseudo-biomes don't have IDs:
		if (meanBiomeOccurences < islandOccurences) {
			if(plantOccurences == 0)
				meanBiomeId = TileKindFactory.get(ExtTileIdMap.TILE_END_ISLAND);
			else
				meanBiomeId = TileKindFactory.get(ExtTileIdMap.TILE_END_ISLAND_PLANTS);
		} else if (meanBiomeOccurences < voidOccurences) {
			meanBiomeId = TileKindFactory.get(ExtTileIdMap.TILE_END_VOID);
		}
		
		return meanBiomeId;
	}
}
