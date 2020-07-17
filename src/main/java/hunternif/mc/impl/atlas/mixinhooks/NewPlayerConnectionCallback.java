package hunternif.mc.impl.atlas.mixinhooks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface NewPlayerConnectionCallback {
    Event<NewPlayerConnectionCallback> EVENT = EventFactory.createArrayBacked(NewPlayerConnectionCallback.class,
            (invokers) -> (isRemote) -> {
                for (NewPlayerConnectionCallback callback : invokers) {
                    callback.onNewConnection(isRemote);
                }
            });

    void onNewConnection(ServerPlayerEntity player);
}
