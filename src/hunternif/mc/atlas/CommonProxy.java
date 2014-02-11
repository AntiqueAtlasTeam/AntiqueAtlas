package hunternif.mc.atlas;

import hunternif.mc.atlas.ext.ExtTileConfig;

import java.io.File;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	protected File configDir;
	
	private ExtTileConfig extTileConfig;
	
	public void preInit(FMLPreInitializationEvent event) {
		configDir = new File(event.getModConfigurationDirectory(), "antiqueatlas");
		configDir.mkdir();
		extTileConfig = new ExtTileConfig(new File(configDir, "tileids.json"));
		extTileConfig.load();
	}
	
	/** Must be called after preInit! */
	public File getItemConfigFile() {
		return new File(configDir, "items.cfg");
	}
	
	public void init(FMLInitializationEvent event) {}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
	public void updateExtTileConfig() {
		extTileConfig.save();
	}
	
	// Purely client stuff
	public void updateBiomeTextureConfig() {}
	public void updateMarkerTextureConfig() {}
	public void openAtlasGUI(ItemStack stack) {}
}