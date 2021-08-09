package hunternif.mc.impl.atlas.core.scaning;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Detects the 256 vanilla biomes, water pools and lava pools.
 * Water and beach biomes are given priority because shore line is the defining
 * feature of the map, and so that rivers are more connected.
 * @author Hunternif
 */
public class TileDetectorBase implements ITileDetector {
	/** Biome used for occasional pools of water.
	 * This used our own representation of biomes, but this was switched to Minecraft biomes.
	 * So in absence of a better idea, this will just count as River from now on. */
	private static final ResourceLocation waterPoolBiome = Biomes.RIVER.getLocation();
	/** Increment the counter for water biomes by this much during iteration.
	 * This is done so that water pools are more visible. */
	private static final int priorityRavine = 12, priorityWaterPool = 4, priorityLavaPool = 6;

	/** Minimum depth in the ground to be considered a ravine */
	private static final int ravineMinDepth = 7;

	/** Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(WATER) */
	private static final Set<ResourceLocation> waterBiomes = new HashSet<>();
	/** Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(BEACH) */
	private static final Set<ResourceLocation> beachBiomes = new HashSet<>();

	private static final Set<ResourceLocation> swampBiomes = new HashSet<>();

	/** Scan all registered biomes to mark biomes of certain types that will be
	 * given higher priority when identifying mean biome ID for a chunk.
	 * (Currently WATER, BEACH and SWAMP) */
	@SuppressWarnings("incomplete-switch")
	public static void scanBiomeTypes() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			switch (biome.getCategory()) {
				case BEACH:
					beachBiomes.add(ForgeRegistries.BIOMES.getKey(biome));
					break;
				case RIVER:
				case OCEAN:
					waterBiomes.add(ForgeRegistries.BIOMES.getKey(biome));
					break;
				case SWAMP:
					swampBiomes.add(ForgeRegistries.BIOMES.getKey(biome));
					break;
			}
		}
	}

	int priorityForBiome(ResourceLocation biome) {
		if (waterBiomes.contains(biome)) {
			return 4;
		} else if (beachBiomes.contains(biome)) {
			return 3;
		} else{
			return 1;
		}
	}

	protected static ResourceLocation getBiomeIdentifier(World world, Biome biome)
	{
		return world./*getRegistryManager()*/func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome);
	}

	protected static void updateOccurrencesMap(Map<ResourceLocation, Integer> map, ResourceLocation biome, int weight) {
		int occurrence = map.getOrDefault(biome, 0) + weight;
		map.put(biome, occurrence);
	}

	protected static void updateOccurrencesMap(Map<ResourceLocation, Integer> map, World world, Biome biome, int weight) {
		ResourceLocation id = getBiomeIdentifier(world, biome);
		int occurrence = map.getOrDefault(id, 0) + weight;
		map.put(id, occurrence);
	}
	
	@Override
    public int getScanRadius() {
        return AntiqueAtlasConfig.scanRadius.get();
    }

	/** If no valid biome ID is found, returns null.
	 * @return*/
	@Override
	public ResourceLocation getBiomeID(World world, IChunk chunk) {
		BiomeContainer chunkBiomes = chunk.getBiomes();
		Map<ResourceLocation, Integer> biomeOccurrences = new HashMap<>(ForgeRegistries.BIOMES.getKeys().size());

		if (chunkBiomes == null)
			return null;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome biome = chunkBiomes.getNoiseBiome(x, world.getSeaLevel(), z);
				if (AntiqueAtlasConfig.doScanPonds.get()) {
					int y = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).getHeight(x, z);
					if (y > 0) {
						Block topBlock = chunk.getBlockState(new BlockPos(x, y-1, z)).getBlock();
						// Check if there's surface of water at (x, z), but not swamp
						if (topBlock == Blocks.WATER) {
                            if (swampBiomes.contains(getBiomeIdentifier(world, biome))) {
                                updateOccurrencesMap(biomeOccurrences, TileIdMap.SWAMP_WATER, priorityWaterPool);
                            } else {
                                updateOccurrencesMap(biomeOccurrences, waterPoolBiome, priorityWaterPool);
                            }
                        } else if (topBlock == Blocks.LAVA) {
							updateOccurrencesMap(biomeOccurrences, TileIdMap.TILE_LAVA, priorityLavaPool);
						}
					}
				}
				if (AntiqueAtlasConfig.doScanRavines.get()) {
					int height = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).getHeight(x, z);

					if(height > 0 && height < world.getSeaLevel() - ravineMinDepth)	{
						updateOccurrencesMap(biomeOccurrences, TileIdMap.TILE_RAVINE, priorityRavine);
					}
				}

				updateOccurrencesMap(biomeOccurrences, world, biome, priorityForBiome(getBiomeIdentifier(world,biome)));
			}
		}

		if (biomeOccurrences.isEmpty()) return null;

		Map.Entry<ResourceLocation, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
		return meanBiome.getKey();
	}
}
