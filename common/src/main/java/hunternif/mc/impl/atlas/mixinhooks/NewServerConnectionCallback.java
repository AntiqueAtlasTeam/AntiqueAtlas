package hunternif.mc.impl.atlas.mixinhooks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

@FunctionalInterface
public interface NewServerConnectionCallback {
    Event<NewServerConnectionCallback> EVENT = EventFactory.createLoop();

    void onNewConnection(boolean isRemote);
}
