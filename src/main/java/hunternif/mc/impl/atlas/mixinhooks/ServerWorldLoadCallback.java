package hunternif.mc.impl.atlas.mixinhooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public interface ServerWorldLoadCallback {
    Event<ServerWorldLoadCallback> EVENT = EventFactory.createArrayBacked(ServerWorldLoadCallback.class,
            (invokers) -> (isRemote) -> {
                for (ServerWorldLoadCallback callback : invokers) {
                    callback.onWorldLoaded(isRemote);
                }
            });

    void onWorldLoaded(ServerWorld world);
}
