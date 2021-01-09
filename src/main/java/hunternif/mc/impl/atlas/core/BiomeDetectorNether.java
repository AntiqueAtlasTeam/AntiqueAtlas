package hunternif.mc.impl.atlas.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.IChunk;

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
	public ResourceLocation getBiomeID(World world, IChunk chunk) {
		BiomeContainer chunkBiomes = chunk.getBiomes();
		if (chunkBiomes == null)
			return null;

		Map<ResourceLocation, Integer> biomeOccurrences = new HashMap<>(WorldGenRegistries.BIOME.keySet().size());

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome biome = chunkBiomes.getNoiseBiome(x, lavaSeaLevel, z);
				if (biome.getCategory() == Biome.Category.NETHER) {
					// The Nether!
					Block netherBlock = chunk.getBlockState(new BlockPos(x, lavaSeaLevel, z)).getBlock();
					if (netherBlock == Blocks.LAVA) {
						updateOccurrencesMap(biomeOccurrences, ExtTileIdMap.TILE_LAVA, priorityLava);
					} else {
						BlockState netherBlockState = chunk.getBlockState(new BlockPos(x, airProbeLevel, z));
						if (netherBlockState.isAir()) {
							updateOccurrencesMap(biomeOccurrences, ExtTileIdMap.TILE_LAVA_SHORE, 1);
						} else {
							// cave walls
							updateOccurrencesMap(biomeOccurrences, world, biome, 1);
						}
					}
				} else {
					// In case there are custom biomes "modded in":
					updateOccurrencesMap(biomeOccurrences, world, biome, priorityForBiome(biome));
				}
			}
		}

		if (biomeOccurrences.isEmpty()) return null;

		Map.Entry<ResourceLocation, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator
				.comparingInt(Map.Entry::getValue));

		return meanBiome.getKey();
	}
}
