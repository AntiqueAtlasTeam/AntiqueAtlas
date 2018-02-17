package hunternif.mc.atlas.ext.watcher;

import com.google.common.collect.Sets;
import hunternif.mc.atlas.util.Log;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class StructureWatcher {

    public static final StructureWatcher INSTANCE = new StructureWatcher();

    private final Set<IStructureWatcher> structureWatchers = Sets.newHashSet();

    public StructureWatcher() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public void onWorldLoad(WorldEvent.Load event) {
        handlePotential(event.getWorld());
    }

    @SubscribeEvent
    public void onPopulateChunk(PopulateChunkEvent.Post event) {
        handlePotential(event.getWorld());
    }

    private void handlePotential(World world) {
        if (world.isRemote)
            return;

        for (IStructureWatcher watcher : structureWatchers)
            if (watcher.isDimensionValid(world.provider.getDimensionType())) {
                NBTTagCompound structureData = watcher.getStructureData(world);
                if (structureData != null) {
                    Set<Pair<WatcherPos, String>> visited = watcher.visitStructure(world, structureData);
                    for (Pair<WatcherPos, String> visit : visited)
                        Log.info("Visited %s in dimension %d at %s", visit.getRight(), world.provider.getDimension(), visit.getLeft().toString());
                }
            }
    }

    public void handleShutdown() {
        for (IStructureWatcher watcher : structureWatchers)
            watcher.getVisited().clear();
    }

    public void addWatcher(IStructureWatcher watcher) {
        if (!structureWatchers.contains(watcher))
            structureWatchers.add(watcher);
    }
}
