package hunternif.mc.atlas;

import hunternif.mc.atlas.network.PacketDispatcher;
import net.fabricmc.api.ClientModInitializer;

public class AntiqueAtlasModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientProxy clientProxy = new ClientProxy();
		AntiqueAtlasMod.proxy = clientProxy;
		clientProxy.initClient();

		// TODO FABRIC hack
		// run twice -> register client-side packets too
		PacketDispatcher.registerPackets();
	}
}
