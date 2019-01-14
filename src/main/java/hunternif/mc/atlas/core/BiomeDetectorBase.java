package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	/** Biome used for occasional pools of water. */
	private static final int waterPoolBiomeID = Biome.getIdForBiome(Biomes.RIVER);
	/** Increment the counter for water biomes by this much during iteration.
	 * This is done so that water pools are more visible. */
	private static final int priorityRavine = 12, priorityWaterPool = 3, prioritylavaPool = 6;

	/** Minimum depth in the ground to be considered a ravine */
	private static final int ravineMinDepth = 7;

	/** Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(WATER) */
	private static final Set<Biome> waterBiomes = new HashSet<>();
	/** Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(BEACH) */
	private static final Set<Biome> beachBiomes = new HashSet<>();

	private static final Set<Biome> swampBiomes = new HashSet<>();


	protected static Method biomeArrayMethod;

	/** Scan all registered biomes to mark biomes of certain types that will be
	 * given higher priority when identifying mean biome ID for a chunk.
	 * (Currently WATER, BEACH and SWAMP) */
	public static void scanBiomeTypes() {
		waterBiomes.addAll(BiomeDictionary.getBiomes(Type.WATER));
		beachBiomes.addAll(BiomeDictionary.getBiomes(Type.BEACH));
		swampBiomes.addAll(BiomeDictionary.getBiomes(Type.SWAMP));
	}

	public static void setBiomeArrayMethod(boolean jeidPresent) {
		try {
			if (jeidPresent) {
				biomeArrayMethod = Chunk.class.getMethod("getIntBiomeArray");
			}
			else {
				biomeArrayMethod = ReflectionHelper.findMethod(Chunk.class, "getBiomeArray", "func_76605_m");
			}
		}
		catch (NoSuchMethodException e) { throw new RuntimeException(e); }
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

	/** If no valid biome ID is found, returns {@link IBiomeDetector#NOT_FOUND}. */
	@Override
	public int getBiomeID(Chunk chunk) {
		int biomeCount = Biome.REGISTRY.getKeys().size();

		int[] chunkBiomes;
		try {
			chunkBiomes = ByteUtil.unsignedByteToIntArray(biomeArrayMethod.invoke(chunk));
		}
		catch (IllegalAccessException | InvocationTargetException e) { throw new RuntimeException(e); }


		Map<Integer, Integer> biomeOccurrences = new HashMap<>(biomeCount);

		// The following important pseudo-biomes don't have IDs:
		int lavaOccurrences = 0;
		int ravineOccurences = 0;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int biomeID = chunkBiomes[x << 4 | z];
				if (doScanPonds) {
					int y = chunk.getHeightValue(x, z);
					if (y > 0) {
						Block topBlock = chunk.getBlockState(x, y-1, z).getBlock();
						// For some reason lava doesn't count in height value
						// TODO: check if 1.8 fixes this!
						Block topBlock2 = chunk.getBlockState(x, y, z).getBlock();
						// Check if there's surface of water at (x, z), but not swamp
						if (topBlock == Blocks.WATER && !swampBiomes.contains(Biome.getBiomeForId(biomeID))) {
							int occurrence = biomeOccurrences.getOrDefault(waterPoolBiomeID, 0) + priorityWaterPool;
							biomeOccurrences.put(waterPoolBiomeID, occurrence);
						} else if (topBlock2 == Blocks.LAVA) {
							lavaOccurrences += prioritylavaPool;
						}
					}
				}
				if (doScanRavines) {
					if(chunk.getHeightValue(x, z) < chunk.getWorld().provider.getAverageGroundLevel() - ravineMinDepth)	{
						ravineOccurences += priorityRavine;
					}
				}
				if (biomeID >= 0 && Biome.getBiomeForId(biomeID) != null) {
					int occurrence = biomeOccurrences.getOrDefault(biomeID, 0) + priorityForBiome(Biome.getBiomeForId(biomeID));
					biomeOccurrences.put(biomeID, occurrence);
				}
			}
		}

		try {
			Map.Entry<Integer, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
			int meanBiomeId = meanBiome.getKey();
			int meanBiomeOccurrences = meanBiome.getValue();

			// The following important pseudo-biomes don't have IDs:
			if (meanBiomeOccurrences < ravineOccurences) {
				return ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_RAVINE);
			}
			if (meanBiomeOccurrences < lavaOccurrences) {
				return ExtTileIdMap.instance().getPseudoBiomeID(ExtTileIdMap.TILE_LAVA);
			}

			return meanBiomeId;
		} catch(NoSuchElementException e){
			return Biome.getIdForBiome(Biomes.DEFAULT);
		}
	}
}
