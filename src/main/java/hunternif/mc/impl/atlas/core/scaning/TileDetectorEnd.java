package hunternif.mc.impl.atlas.core.scaning;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Detects seas of lava, cave ground and cave walls in the Nether.
 *
 * @author Hunternif
 */
public class TileDetectorEnd extends TileDetectorBase implements ITileDetector {

    @Override
    public Identifier getBiomeID(World world, Chunk chunk) {
        BiomeArray chunkBiomes = chunk.getBiomeArray();

        if (chunkBiomes == null)
            return null;

        Map<Identifier, Integer> biomeOccurrences = new HashMap<>(BuiltinRegistries.BIOME.getIds().size());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = chunkBiomes.getBiomeForNoiseGen(x, 0, z);
                Identifier id = getBiomeIdentifier(world, biome);

                if (id == BiomeKeys.THE_VOID.getValue()) {
                    // if the biome is void, it's really empty
                    updateOccurrencesMap(biomeOccurrences, id, 1);
                } else {
                    // we have a biome, but it might be just a few floating island, or nothing

                    // TODO check if WORLD_SURFACE is available on dedicated servers
                    int top = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).get(x, z);
                    BlockState topBlock = chunk.getBlockState(new BlockPos(x, top - 1, z));

                    if (topBlock.getBlock() == Blocks.END_STONE) {
                        // we want to see "coast lines", so we give the islands a slightly higher weight
                        updateOccurrencesMap(biomeOccurrences, world, biome, 3);
                    } else if (topBlock.isAir()) {
                        updateOccurrencesMap(biomeOccurrences, BiomeKeys.THE_VOID.getValue(), 1);
                    }
                }
            }
        }

        if (biomeOccurrences.isEmpty())
            return null;

        Map.Entry<Identifier, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator
                .comparingInt(Map.Entry::getValue));
        return meanBiome.getKey();
    }
}
