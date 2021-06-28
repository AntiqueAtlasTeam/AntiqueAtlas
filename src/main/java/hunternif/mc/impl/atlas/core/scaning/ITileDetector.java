package hunternif.mc.impl.atlas.core.scaning;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;


/**
 * Finds the biome ID to be used for a given chunk.
 *
 * @author Hunternif
 */
public interface ITileDetector {
    /**
     * Finds the biome ID to be used for a given chunk.
     */
    @Nullable
    Identifier getBiomeID(World world, Chunk chunk);

    int getScanRadius();
}
