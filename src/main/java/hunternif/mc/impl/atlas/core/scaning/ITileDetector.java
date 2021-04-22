package hunternif.mc.impl.atlas.core.scaning;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;

/**
 * Finds the biome ID to be used for a given chunk.
 * @author Hunternif
 */
public interface ITileDetector {
	/** Finds the biome ID to be used for a given chunk. */
	@Nullable
	ResourceLocation getBiomeID(World world, IChunk chunk);
	
	int getScanRadius();
}
