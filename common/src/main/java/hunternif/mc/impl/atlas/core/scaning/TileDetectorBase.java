package hunternif.mc.impl.atlas.core.scaning;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

/**
 * Detects the 256 vanilla biomes, water pools and lava pools.
 * Water and beach biomes are given priority because shore line is the defining
 * feature of the map, and so that rivers are more connected.
 *
 * @author Hunternif
 */
public class TileDetectorBase implements ITileDetector {
    /**
     * Biome used for occasional pools of water.
     * This used our own representation of biomes, but this was switched to Minecraft biomes.
     * So in absence of a better idea, this will just count as River from now on.
     */
    private static final Identifier waterPoolBiome = BiomeKeys.RIVER.getValue();
    /**
     * Increment the counter for water biomes by this much during iteration.
     * This is done so that water pools are more visible.
     */
    private static final int priorityRavine = 12, priorityWaterPool = 4, priorityLavaPool = 6;

    /**
     * Minimum depth in the ground to be considered a ravine
     */
    private static final int ravineMinDepth = 7;

    /**
     * Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(WATER)
     */
    private static final Set<Identifier> waterBiomes = new HashSet<>();
    /**
     * Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(BEACH)
     */
    private static final Set<Identifier> beachBiomes = new HashSet<>();

    private static final Set<Identifier> swampBiomes = new HashSet<>();

    /**
     * Scan all registered biomes to mark biomes of certain types that will be
     * given higher priority when identifying mean biome ID for a chunk.
     * (Currently WATER, BEACH and SWAMP)
     */
    public static void scanBiomeTypes() {
        for (Biome biome : BuiltinRegistries.BIOME) {
            switch (biome.getCategory()) {
                case BEACH:
                    beachBiomes.add(BuiltinRegistries.BIOME.getId(biome));
                    break;
                case RIVER:
                case OCEAN:
                    waterBiomes.add(BuiltinRegistries.BIOME.getId(biome));
                    break;
                case SWAMP:
                    swampBiomes.add(BuiltinRegistries.BIOME.getId(biome));
                    break;
            }
        }
    }

    int priorityForBiome(Identifier biome) {
        if (waterBiomes.contains(biome)) {
            return 4;
        } else if (beachBiomes.contains(biome)) {
            return 3;
        } else {
            return 1;
        }
    }

    protected static Identifier getBiomeIdentifier(World world, Biome biome) {
        return world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
    }

    protected static void updateOccurrencesMap(Map<Identifier, Integer> map, Identifier biome, int weight) {
        int occurrence = map.getOrDefault(biome, 0) + weight;
        map.put(biome, occurrence);
    }

    protected static void updateOccurrencesMap(Map<Identifier, Integer> map, World world, Biome biome, int weight) {
        Identifier id = getBiomeIdentifier(world, biome);
        int occurrence = map.getOrDefault(id, 0) + weight;
        map.put(id, occurrence);
    }

    @Override
    public int getScanRadius() {
        return AntiqueAtlasMod.CONFIG.scanRadius;
    }

    /**
     * If no valid biome ID is found, returns null.
     *
     * @return the detected biome ID for the given chunk
     */
    @Override
    public Identifier getBiomeID(World world, Chunk chunk) {
        BiomeArray chunkBiomes = chunk.getBiomeArray();
        Map<Identifier, Integer> biomeOccurrences = new HashMap<>(BuiltinRegistries.BIOME.getIds().size());

        if (chunkBiomes == null)
            return null;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = chunkBiomes.getBiomeForNoiseGen(x, 0, z);
                if (AntiqueAtlasMod.CONFIG.doScanPonds) {
                    int y = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).get(x, z);
                    if (y > 0) {
                        Block topBlock = chunk.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
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

                if (AntiqueAtlasMod.CONFIG.doScanRavines) {
                    int height = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).get(x, z);

                    if (height > 0 && height < world.getSeaLevel() - ravineMinDepth) {
                        updateOccurrencesMap(biomeOccurrences, TileIdMap.TILE_RAVINE, priorityRavine);
                    }
                }

                updateOccurrencesMap(biomeOccurrences, world, biome, priorityForBiome(getBiomeIdentifier(world,biome)));
            }
        }

        if (biomeOccurrences.isEmpty()) return null;

        Map.Entry<Identifier, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
        return meanBiome.getKey();
    }
}
