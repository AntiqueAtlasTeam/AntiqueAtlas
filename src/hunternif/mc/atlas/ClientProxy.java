package hunternif.mc.atlas;

import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.Config;
import hunternif.mc.atlas.client.GuiAtlas;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
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
	
	@Override
	public void openAtlasGUI(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) { // In-game screen
			mc.displayGuiScreen(new GuiAtlas(stack));
		}
	}
}