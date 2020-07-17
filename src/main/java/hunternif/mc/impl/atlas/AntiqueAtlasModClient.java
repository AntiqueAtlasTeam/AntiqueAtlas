package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.network.AntiqueAtlasNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AntiqueAtlasModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientProxy clientProxy = new ClientProxy();
		AntiqueAtlasMod.proxy = clientProxy;
		clientProxy.initClient();

		AntiqueAtlasNetworking.registerS2CListeners();
	}
}
