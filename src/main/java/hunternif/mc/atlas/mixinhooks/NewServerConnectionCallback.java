package hunternif.mc.atlas.mixinhooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface NewServerConnectionCallback {
    Event<NewServerConnectionCallback> EVENT = EventFactory.createArrayBacked(NewServerConnectionCallback.class,
            (invokers) -> (isRemote) -> {
                for (NewServerConnectionCallback callback : invokers) {
                    callback.onNewConnection(isRemote);
                }
            });

    void onNewConnection(boolean isRemote);
}
