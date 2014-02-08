package hunternif.mc.atlas;

import hunternif.mc.atlas.client.GuiAtlas;
import hunternif.mc.atlas.core.BiomeTextureMap;
import hunternif.mc.atlas.core.TextureConfig;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	private TextureConfig textureConfig;
	
	private GuiAtlas guiAtlas;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		textureConfig = new TextureConfig(new File(configDir, "textures.json"));
		textureConfig.load();
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
			updateTextureConfig();
		}
	}
	
	@Override
	public void updateTextureConfig() {
		textureConfig.save();
	}
	
	@Override
	public void openAtlasGUI(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) { // In-game screen
			mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
		}
	}
}