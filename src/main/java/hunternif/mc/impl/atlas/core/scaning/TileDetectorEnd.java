package hunternif.mc.impl.atlas.core.scaning;

import net.minecraft.core.BlockPos;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.levelgen.Heightmap;

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
    public ResourceLocation getBiomeID(Level world, ChunkAccess chunk) {
        ChunkBiomeContainer chunkBiomes = chunk.getBiomes();

        if (chunkBiomes == null)
            return null;

        Map<ResourceLocation, Integer> biomeOccurrences = new HashMap<>(BuiltinRegistries.BIOME.keySet().size());

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Biome biome = chunkBiomes.getNoiseBiome(x, 0, z);
                ResourceLocation id = getBiomeIdentifier(world, biome);

                if (id == Biomes.THE_VOID.location()) {
                    // if the biome is void, it's really empty
                    updateOccurrencesMap(biomeOccurrences, id, 1);
                } else {
                    // we have a biome, but it might be just a few floating island, or nothing

                    // TODO check if WORLD_SURFACE is available on dedicated servers
                    int top = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE).getFirstAvailable(x, z);
                    BlockState topBlock = chunk.getBlockState(new BlockPos(x, top - 1, z));

                    if (topBlock.getBlock() == Blocks.END_STONE) {
                        // we want to see "coast lines", so we give the islands a slightly higher weight
                        updateOccurrencesMap(biomeOccurrences, world, biome, 3);
                    } else if (topBlock.isAir()) {
                        updateOccurrencesMap(biomeOccurrences, Biomes.THE_VOID.location(), 1);
                    }
                }
            }
        }

        if (biomeOccurrences.isEmpty())
            return null;

        Map.Entry<ResourceLocation, Integer> meanBiome = Collections.max(biomeOccurrences.entrySet(), Comparator
                .comparingInt(Map.Entry::getValue));
        return meanBiome.getKey();
    }
}
