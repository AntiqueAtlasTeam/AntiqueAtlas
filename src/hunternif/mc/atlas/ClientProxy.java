package hunternif.mc.atlas;

import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.Config;
import hunternif.mc.atlas.client.GuiAtlas;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	private Config config;
	
	private GuiAtlas guiAtlas;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		config = new Config(new File(event.getModConfigurationDirectory(), "AtlasTextures.json"));
		config.load();
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		guiAtlas = new GuiAtlas();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		if (BiomeTextureMap.instance().assignVanillaTextures()) {
			// Only rewrite config, if new textures were automatically assigned.
			updateConfig();
		}
	}
	
	@Override
	public void updateConfig() {
		config.save();
	}
	
	@Override
	public void openAtlasGUI(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) { // In-game screen
			mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
		}
	}
}