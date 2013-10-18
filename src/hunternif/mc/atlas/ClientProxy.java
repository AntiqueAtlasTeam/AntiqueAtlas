package hunternif.mc.atlas;

import hunternif.mc.atlas.gui.BiomeTextureMap;

public class ClientProxy extends CommonProxy {
	@Override
	public void postInit() {
		BiomeTextureMap.instance().assignVanillaTextures();
	}
}