package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

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
	public Identifier getBiomeID(World world, Chunk chunk) {
		BiomeArray chunkBiomes = chunk.getBiomeArray();
		if (chunkBiomes == null)
			return ExtTileIdMap.TILE_LAVA;

		Map<Biome, Integer> biomeOccurrences = new HashMap<>(Registry.BIOME.getIds().size());
		
		// The following important pseudo-biomes don't have IDs:
		int lavaOccurences = 0;
		int groundOccurences = 0;
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome biome = chunkBiomes.getBiomeForNoiseGen(x, lavaSeaLevel, z);
				if (biome.getCategory() == Biome.Category.NETHER) {
					// The Nether!
					Block netherBlock = chunk.getBlockState(new BlockPos(x, lavaSeaLevel, z)).getBlock();
					if (netherBlock == Blocks.LAVA) {
						lavaOccurences += priorityLava;
					} else {
						BlockState netherBlockState = chunk.getBlockState(new BlockPos(x, airProbeLevel, z));
						if (netherBlockState.isAir()) {
							groundOccurences++; // ground
						} else {
							// cave walls
							biomeOccurrences.put(biome,
											biomeOccurrences.getOrDefault(biome, 0) + 1
							);
						}
					}
				} else {
					// In case there are custom biomes "modded in":
					biomeOccurrences.put(biome,
									biomeOccurrences.getOrDefault(biome, 0) + priorityForBiome(biome)
					);
				}
			}
		}

		Identifier meanBiomeId = null;
		int meanBiomeOccurences = 0;
		for (Biome biome : biomeOccurrences.keySet()) {
			int occ = biomeOccurrences.get(biome);
			if (biomeOccurrences.get(biome) > meanBiomeOccurences) {
				meanBiomeId = Registry.BIOME.getId(biome);
				meanBiomeOccurences = occ;
			}
		}
		
		// The following important pseudo-biomes don't have IDs:
		if (meanBiomeOccurences < lavaOccurences) {
			meanBiomeId = ExtTileIdMap.TILE_LAVA;
		} else if (meanBiomeOccurences < groundOccurences) {
			meanBiomeId = ExtTileIdMap.TILE_LAVA_SHORE;
		}
		
		return meanBiomeId;
	}
}
