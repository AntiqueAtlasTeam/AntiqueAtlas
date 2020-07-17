package hunternif.mc.impl.atlas.ext.watcher;

import com.google.common.collect.Sets;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class StructureWatcher {

    public static final StructureWatcher INSTANCE = new StructureWatcher();

    private final Set<IStructureWatcher> structureWatchers = Sets.newHashSet();

    public StructureWatcher() {
    }

    // TODO FABRIC
/*     @SubscribeEvent(priority= EventPriority.LOWEST)
    public void onWorldLoad(WorldEvent.Load event) {
        handlePotential(event.getWorld());
    }

    @SubscribeEvent
    public void onPopulateChunk(PopulateChunkEvent.Post event) {
        handlePotential(event.getWorld());
    } */

    public static void handlePotential(ServerWorld world) {
        for (IStructureWatcher watcher : INSTANCE.structureWatchers)
            if (watcher.isDimensionValid(world)) {
                CompoundTag structureData = watcher.getStructureData(world);
                if (structureData != null) {
                    Set<Pair<WatcherPos, String>> visited = watcher.visitStructure(world, structureData);
                    for (Pair<WatcherPos, String> visit : visited)
                        Log.info("Visited %s in dimension %s at %s", visit.getRight(), world.getDimension().toString(), visit.getLeft().toString());
                }
            }
    }

    public static void handleShutdown() {
        for (IStructureWatcher watcher : INSTANCE.structureWatchers)
            watcher.getVisited().clear();
    }

    public void addWatcher(IStructureWatcher watcher) {
        structureWatchers.add(watcher);
    }
}
