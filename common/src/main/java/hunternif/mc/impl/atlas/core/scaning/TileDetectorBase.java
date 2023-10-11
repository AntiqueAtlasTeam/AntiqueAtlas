package hunternif.mc.impl.atlas.core.scaning;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
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

    /*
        We check the tags now!

     * Scan all registered biomes to mark biomes of certain types that will be
     * given higher priority when identifying mean biome ID for a chunk.
     * (Currently WATER, BEACH and SWAMP)
    public static void scanBiomeTypes(World world) {
        for (Biome biome : Registries.BIOME_SOURCE) {
            switch (biome.getCategory()) {
                case BEACH -> beachBiomes.add(BuiltinRegistries.BIOME.getId(biome));
                case RIVER, OCEAN -> waterBiomes.add(BuiltinRegistries.BIOME.getId(biome));
                case SWAMP -> swampBiomes.add(BuiltinRegistries.BIOME.getId(biome));
            }
        }
    }*/

    int priorityForBiome(RegistryEntry<Biome> biomeRegistryKey) {
        if (biomeRegistryKey.isIn(BiomeTags.IS_RIVER) || biomeRegistryKey.isIn(BiomeTags.IS_OCEAN)) {
            return 4;
        } else if (biomeRegistryKey.isIn(BiomeTags.IS_BEACH)) {
            return 3;
        } else {
            return 1;
        }
    }

    /* these are the values used by vanilla, but it just doesn't work for me.
    protected static TileHeightType getHeightType(double weirdness) {
        if (weirdness < (double) VanillaTerrainParameters.getNormalizedWeirdness(0.05f)) {
            return TileHeightType.VALLEY;
        }
        if (weirdness < (double) VanillaTerrainParameters.getNormalizedWeirdness(0.26666668f)) {
            return TileHeightType.LOW;
        }
        if (weirdness < (double) VanillaTerrainParameters.getNormalizedWeirdness(0.4f)) {
            return TileHeightType.MID;
        }
        if (weirdness < (double) VanillaTerrainParameters.getNormalizedWeirdness(0.56666666f)) {
            return TileHeightType.HIGH;
        }
        return TileHeightType.PEAK;
    } */

    protected static TileHeightType getHeightTypeFromY(int y, int sealevel) {
        if (y < sealevel + 10) {
            return TileHeightType.VALLEY;
        }
        if (y < sealevel + 20) {
            return TileHeightType.LOW;
        }
        if (y < sealevel + 35) {
            return TileHeightType.MID;
        }
        if (y < sealevel + 50) {
            return TileHeightType.HIGH;
        }
        return TileHeightType.PEAK;
    }


    protected static Identifier getBiomeIdentifier(World world, Biome biome) {
        return world.getRegistryManager().get(RegistryKeys.BIOME).getId(biome);
    }

    protected static void updateOccurrencesMap(Map<Identifier, Integer> map, Identifier biome, int weight) {
        int occurrence = map.getOrDefault(biome, 0) + weight;
        map.put(biome, occurrence);
    }

    protected static void updateOccurrencesMap(Map<Identifier, Integer> map, World world, Biome biome, TileHeightType type, int weight) {
        Identifier id = getBiomeIdentifier(world, biome);
        id = Identifier.tryParse(id.toString() + "_" + type.getName());

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
        var BIOME_REGISTRY = world.getRegistryManager().get(RegistryKeys.BIOME);
        Map<Identifier, Integer> biomeOccurrences = new HashMap<>(BIOME_REGISTRY.getIds().size());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                RegistryEntry<Biome> registryEntryBiome = chunk.getBiomeForNoiseGen(x, world.getSeaLevel(), z);
                // biomes seems to be changing with height as well. Let's scan at sea level.
                Biome biome = registryEntryBiome.value();


                // get top block
                int y = chunk.getHeightmap(Heightmap.Type.MOTION_BLOCKING).get(x, z);


                //this code runs on the server
//                ServerChunkManager man = (ServerChunkManager) world.getChunkManager();
//                MultiNoiseUtil.MultiNoiseSampler sampler = man.getChunkGenerator().getMultiNoiseSampler();
//                ChunkPos pos = chunk.getPos();
//                MultiNoiseUtil.NoiseValuePoint sample = sampler.sample(pos.getStartX() + x, y + 10, pos.getStartZ() + z);

//                float m = MultiNoiseUtil.method_38666(sample.weirdnessNoise());
//                double weirdness = VanillaTerrainParameters.getNormalizedWeirdness(m);

                if (AntiqueAtlasMod.CONFIG.doScanPonds) {
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
                    if (y > 0 && y < world.getSeaLevel() - ravineMinDepth) {
                        updateOccurrencesMap(biomeOccurrences, TileIdMap.TILE_RAVINE, priorityRavine);
                    }
                }

//                updateOccurrencesMap(biomeOccurrences, world, biome, getHeightType(weirdness), priorityForBiome(getBiomeIdentifier(world, biome)));
                updateOccurrencesMap(biomeOccurrences, world, biome, getHeightTypeFromY(y, world.getSeaLevel()), priorityForBiome(registryEntryBiome));
            }
        }

        if (biomeOccurrences.isEmpty()) return null;

        Map.Entry<Identifier, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
        return meanBiome.getKey();
    }
}
