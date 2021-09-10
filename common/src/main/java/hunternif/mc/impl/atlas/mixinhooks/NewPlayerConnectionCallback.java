package hunternif.mc.impl.atlas.mixinhooks;

import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface NewPlayerConnectionCallback {
    Event<NewPlayerConnectionCallback> EVENT = EventFactory.createLoop();

    void onNewConnection(ServerPlayerEntity player);
}
