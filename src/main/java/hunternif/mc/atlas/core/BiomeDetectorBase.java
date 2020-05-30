package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Detects the 256 vanilla biomes, water pools and lava pools.
 * Water and beach biomes are given priority because shore line is the defining
 * feature of the map, and so that rivers are more connected.
 * @author Hunternif
 */
public class BiomeDetectorBase implements IBiomeDetector {
	private boolean doScanPonds = true;
	private boolean doScanRavines = true;

	/** Biome used for occasional pools of water.
	 * This used our own representation of biomes, but this was switched to Minecraft biomes.
	 * So in absence of a better idea, this will just count as River from now on. */
	private static final Biome waterPoolBiome = Biomes.RIVER;
	/** Increment the counter for water biomes by this much during iteration.
	 * This is done so that water pools are more visible. */
	private static final int priorityRavine = 12, priorityWaterPool = 4, prioritylavaPool = 6;

	/** Minimum depth in the ground to be considered a ravine */
	private static final int ravineMinDepth = 7;

	/** Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(WATER) */
	private static final Set<Biome> waterBiomes = new HashSet<>();
	/** Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(BEACH) */
	private static final Set<Biome> beachBiomes = new HashSet<>();

	private static final Set<Biome> swampBiomes = new HashSet<>();

	/** Scan all registered biomes to mark biomes of certain types that will be
	 * given higher priority when identifying mean biome ID for a chunk.
	 * (Currently WATER, BEACH and SWAMP) */
	public static void scanBiomeTypes() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			switch (biome.getCategory()) {
				case BEACH:
					beachBiomes.add(biome);
					break;
				case OCEAN:
					waterBiomes.add(biome);
					break;
				case SWAMP:
					swampBiomes.add(biome);
					break;
			}
		}
	}

	public void setScanPonds(boolean value) {
		this.doScanPonds = value;
	}

	public void setScanRavines(boolean value) {
		this.doScanRavines = value;
	}

	int priorityForBiome(Biome biome) {
		if (waterBiomes.contains(biome)) {
			return 4;
		} else if (beachBiomes.contains(biome)) {
			return 3;
		} else {
			return 1;
		}
	}

	/** If no valid biome ID is found, returns null. */
	@Override
	public TileKind getBiomeID(World world, IChunk chunk) {
		BiomeContainer chunkBiomes = chunk.getBiomes();
		Map<Biome, Integer> biomeOccurrences = new HashMap<>(Registry.BIOME.keySet().size());

		// The following important pseudo-biomes don't have IDs:
		int lavaOccurrences = 0;
		int ravineOccurences = 0;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome biomeID = chunkBiomes.getNoiseBiome(x, 0, z);
				if (doScanPonds) {
					int y = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).getHeight(x, z);
					if (y > 0) {
						Block topBlock = chunk.getBlockState(new BlockPos(x, y-1, z)).getBlock();
						// Check if there's surface of water at (x, z), but not swamp
						if (topBlock == Blocks.WATER && !swampBiomes.contains(biomeID)) {
							int occurrence = biomeOccurrences.getOrDefault(waterPoolBiome, 0) + priorityWaterPool;
							biomeOccurrences.put(waterPoolBiome, occurrence);
						} else if (topBlock == Blocks.LAVA) {
							lavaOccurrences += prioritylavaPool;
						}
					}
				}
				if (doScanRavines) {
					int height = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).getHeight(x, z);

					if(height > 0 && height < world.getSeaLevel() - ravineMinDepth)	{
						ravineOccurences += priorityRavine;
					}
				}

				int occurrence = biomeOccurrences.getOrDefault(biomeID, 0) + priorityForBiome(biomeID);
				biomeOccurrences.put(biomeID, occurrence);
			}
		}

		try {
			Map.Entry<Biome, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
			Biome meanBiomeId = meanBiome.getKey();
			int meanBiomeOccurrences = meanBiome.getValue();

			// The following important pseudo-biomes don't have IDs:
			if (meanBiomeOccurrences < ravineOccurences) {
				return TileKindFactory.get(ExtTileIdMap.TILE_RAVINE);
			}
			if (meanBiomeOccurrences < lavaOccurrences) {
				return TileKindFactory.get(ExtTileIdMap.TILE_LAVA);
			}

			return TileKindFactory.get(meanBiomeId);
		} catch(NoSuchElementException e){
			return TileKindFactory.get(Biomes.DEFAULT);
		}
	}
}
