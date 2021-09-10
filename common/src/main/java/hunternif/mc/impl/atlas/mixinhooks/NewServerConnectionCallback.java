package hunternif.mc.impl.atlas.mixinhooks;


import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

@FunctionalInterface
public interface NewServerConnectionCallback {
    Event<NewServerConnectionCallback> EVENT = EventFactory.createLoop();

    void onNewConnection(boolean isRemote);
}
