package hunternif.mc.impl.atlas.core.scanning;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

/**
 * Detects seas of lava, cave ground and cave walls in the Nether.
 *
 * @author Hunternif
 */
public class TileDetectorNether extends TileDetectorBase implements ITileDetector {
    /**
     * The Nether will be checked for air/ground at this level.
     */
    private static final int airProbeLevel = 50;
    /**
     * The Nether will be checked for lava at this level.
     */
    private static final int lavaSeaLevel = 31;

    /**
     * Increment the counter for lava biomes by this much during iteration.
     * This is done so that rivers are more likely to be connected.
     */
    private static final int priorityLava = 1;

    @Override
    public Identifier getBiomeID(World world, Chunk chunk) {
        Multiset<Identifier> biomeOccurrences = HashMultiset.create(BuiltinRegistries.BIOME.getIds().size());
        Registry<Biome> biomeRegistry = world.getRegistryManager().get(Registry.BIOME_KEY);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = chunk.getBiomeForNoiseGen(x, lavaSeaLevel, z).value();
                RegistryEntry<Biome> biomeTag = biomeRegistry.entryOf(biomeRegistry.getKey(biome).orElse(null));
                if (biomeTag.isIn(BiomeTags.IS_NETHER)) {
                    // The Nether!
                    Block seaLevelBlock = chunk.getBlockState(new BlockPos(x, lavaSeaLevel, z)).getBlock();
                    if (seaLevelBlock == Blocks.LAVA) {
                        updateOccurrencesMap(biomeOccurrences, TileIdMap.TILE_LAVA, priorityLava);
                    } else {
                        BlockState airProbeBlock = chunk.getBlockState(new BlockPos(x, airProbeLevel, z));
                        if (airProbeBlock.isAir()) {
                            updateOccurrencesMap(biomeOccurrences, TileIdMap.TILE_LAVA_SHORE, 1);
                        } else {
                            // cave walls
                            updateOccurrencesMap(biomeOccurrences, getBiomeIdentifier(world,biome), 2);
                        }
                    }
                } else {
                    // In case there are custom biomes "modded in":
                    updateOccurrencesMap(biomeOccurrences, getBiomeIdentifier(world,biome), priorityForBiome(biomeTag));
                }
            }
        }

        if (biomeOccurrences.isEmpty()) return null;
        return biomeOccurrences.entrySet().stream().max(Ordering.natural().onResultOf(Multiset.Entry::getCount)).orElseThrow().getElement();
    }

    @Override
    public int getScanRadius() {
        return Math.min(super.getScanRadius(), 6);
    }
}
