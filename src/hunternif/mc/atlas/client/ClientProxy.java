package hunternif.mc.atlas.client;

import hunternif.mc.atlas.CommonProxy;

import java.io.File;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	private Config config;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		config = new Config(new File(event.getModConfigurationDirectory(), "AtlasTextures.json"));
		config.load();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		BiomeTextureMap.instance().assignVanillaTextures();
		updateConfig();
	}
	
	@Override
	public void updateConfig() {
		config.save();
	}
}