package hunternif.mc.impl.atlas.core.scaning;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
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
    ResourceLocation getBiomeID(Level world, ChunkAccess chunk);

    int getScanRadius();
}
