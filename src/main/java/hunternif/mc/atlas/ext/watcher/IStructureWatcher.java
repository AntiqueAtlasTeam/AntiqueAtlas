package hunternif.mc.atlas.ext.watcher;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public interface IStructureWatcher {

    /**
     * In SSP, this is cleared when the user has left the world.
     *
     * @return a <b>modifiable</b> collection of locations that have already been previously visited.
     */
    @Nonnull
    Set<WatcherPos> getVisited();

    /**
     * @return if the dimension is valid for this watcher
     */
    boolean isDimensionValid(DimensionType type);

    /**
     * Return null if a structure should not be attempted to be visited.
     *
     * @param world the world containing the structure
     *
     * @return the NBT tag to read structure data from
     */
    @Nullable
    NBTTagCompound getStructureData(@Nonnull World world);

    /**
     * Called when a world is loaded or a chunk is populated.
     *
     * @param world the world containing the structure
     */
    void visitStructure(@Nonnull World world, @Nonnull NBTTagCompound structureTag);
}
