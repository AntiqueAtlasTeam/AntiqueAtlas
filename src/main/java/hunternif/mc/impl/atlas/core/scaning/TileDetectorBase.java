package hunternif.mc.impl.atlas.core.scaning;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.levelgen.Heightmap;

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
    private static final ResourceLocation waterPoolBiome = Biomes.RIVER.location();
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
    private static final Set<ResourceLocation> waterBiomes = new HashSet<>();
    /**
     * Set to true for biome IDs that return true for BiomeDictionary.isBiomeOfType(BEACH)
     */
    private static final Set<ResourceLocation> beachBiomes = new HashSet<>();

    private static final Set<ResourceLocation> swampBiomes = new HashSet<>();

    /**
     * Scan all registered biomes to mark biomes of certain types that will be
     * given higher priority when identifying mean biome ID for a chunk.
     * (Currently WATER, BEACH and SWAMP)
     */
    public static void scanBiomeTypes() {
        for (Biome biome : BuiltinRegistries.BIOME) {
            switch (biome.getBiomeCategory()) {
                case BEACH:
                    beachBiomes.add(BuiltinRegistries.BIOME.getKey(biome));
                    break;
                case RIVER:
                case OCEAN:
                    waterBiomes.add(BuiltinRegistries.BIOME.getKey(biome));
                    break;
                case SWAMP:
                    swampBiomes.add(BuiltinRegistries.BIOME.getKey(biome));
                    break;
            }
        }
    }

    int priorityForBiome(ResourceLocation biome) {
        if (waterBiomes.contains(biome)) {
            return 4;
        } else if (beachBiomes.contains(biome)) {
            return 3;
        } else {
            return 1;
        }
    }

    protected static ResourceLocation getBiomeIdentifier(Level world, Biome biome) {
        return world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
    }

    protected static void updateOccurrencesMap(Map<ResourceLocation, Integer> map, ResourceLocation biome, int weight) {
        int occurrence = map.getOrDefault(biome, 0) + weight;
        map.put(biome, occurrence);
    }

    protected static void updateOccurrencesMap(Map<ResourceLocation, Integer> map, Level world, Biome biome, int weight) {
        ResourceLocation id = getBiomeIdentifier(world, biome);
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
    public ResourceLocation getBiomeID(Level world, ChunkAccess chunk) {
        ChunkBiomeContainer chunkBiomes = chunk.getBiomes();
        Map<ResourceLocation, Integer> biomeOccurrences = new HashMap<>(BuiltinRegistries.BIOME.keySet().size());

        if (chunkBiomes == null)
            return null;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // biomes seems to be changing with height as well. Let's scan at sea level.
                Biome biome = chunkBiomes.getNoiseBiome(x, world.getSeaLevel(), z);
                if (AntiqueAtlasMod.CONFIG.doScanPonds) {
                    int y = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING).getFirstAvailable(x, z);
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
                    int height = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING).getFirstAvailable(x, z);

                    if (height > 0 && height < world.getSeaLevel() - ravineMinDepth) {
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
