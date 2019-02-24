package hunternif.mc.atlas;

import net.fabricmc.api.ClientModInitializer;

public class AntiqueAtlasModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AntiqueAtlasMod.proxy = new ClientProxy();
	}
}
