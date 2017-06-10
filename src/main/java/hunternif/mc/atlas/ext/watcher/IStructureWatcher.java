package hunternif.mc.atlas.ext.watcher;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
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
     * If the boolean is true, NBTTagCompound must be non-null.
     *
     * @param world the world containing the structure
     *
     * @return if a valid structure has been found and the relevant NBT tag to read the data from
     */
    @Nonnull
    ActionResult<NBTTagCompound> canVisit(@Nonnull World world);

    /**
     * Called when a world is loaded or a chunk is populated.
     *
     * @param world the world containing the structure
     */
    void visitStructure(@Nonnull World world, @Nonnull NBTTagCompound structureTag);
}
