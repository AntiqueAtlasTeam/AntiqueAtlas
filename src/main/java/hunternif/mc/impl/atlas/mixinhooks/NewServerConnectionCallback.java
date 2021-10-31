package hunternif.mc.impl.atlas.mixinhooks;

import java.util.function.Consumer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

@FunctionalInterface
public interface NewServerConnectionCallback {
	public class TheEvent extends Event {
	    private final boolean isRemote;
		
		public TheEvent(boolean isRemote) {
			this.isRemote = isRemote;
		}
		
		public boolean isRemote() {
			return isRemote;
		}
	}
	
	void onNewConnection(boolean isRemote);
	
	public static void register(Consumer<Boolean> isRemote) {
		MinecraftForge.EVENT_BUS.addListener((Consumer<NewServerConnectionCallback.TheEvent>)event->isRemote.accept(event.isRemote()));
	}
}
