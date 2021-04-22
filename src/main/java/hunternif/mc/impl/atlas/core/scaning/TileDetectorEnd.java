package hunternif.mc.impl.atlas.core.scaning;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Detects seas of lava, cave ground and cave walls in the Nether.
 * @author Hunternif
 */
public class TileDetectorEnd extends TileDetectorBase implements ITileDetector {

	@Override
	public ResourceLocation getBiomeID(World world, IChunk chunk) {
		BiomeContainer chunkBiomes = chunk.getBiomes();

		if (chunkBiomes == null)
			return null;

		Map<ResourceLocation, Integer> biomeOccurrences = new HashMap<>(ForgeRegistries.BIOMES.getKeys().size());

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome biome = chunkBiomes.getNoiseBiome(x, 0, z);
				ResourceLocation id = getBiomeIdentifier(world, biome);

				if (id == Biomes.THE_VOID.getLocation()) {
					// if the biome is void, it's really empty
					updateOccurrencesMap(biomeOccurrences, id, 1);
				}
				else {
					// we have a biome, but it might be just a few floating island, or nothing

					// TODO check if WORLD_SURFACE is available on dedicated servers
					int top = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).getHeight(x, z);
					BlockState topBlock = chunk.getBlockState(new BlockPos(x, top - 1, z));

					if (topBlock.getBlock() == Blocks.END_STONE)
					{
						// we want to see "coast lines", so we give the islands a slightly higher weight
						updateOccurrencesMap(biomeOccurrences, world, biome, 3);
					}
					else if (topBlock.isAir())
					{
						updateOccurrencesMap(biomeOccurrences, Biomes.THE_VOID.getLocation(), 1);
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
