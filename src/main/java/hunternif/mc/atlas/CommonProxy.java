package hunternif.mc.atlas;

import hunternif.mc.atlas.ext.ExtTileConfig;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.Log;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
	protected File configDir;
	
	private ExtTileIdMap extTileIdMap;
	private ExtTileConfig extTileConfig;
	
	public void preInit(FMLPreInitializationEvent event) {
		configDir = new File(event.getModConfigurationDirectory(), "antiqueatlas");
		configDir.mkdir();
		extTileIdMap = ExtTileIdMap.instance();
		extTileConfig = new ExtTileConfig(new File(configDir, "tileids.json"));
		extTileConfig.load(extTileIdMap);
	}
	
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void postInit(FMLPostInitializationEvent event) {}
	
	// Purely client stuff

	public void openAtlasGUI(ItemStack stack) {}

	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}
	
	/** When a world is saved, so is the custom tile id config. */
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
		if (extTileIdMap.isDirty()) {
			Log.info("Saving ext tile id config");
			extTileConfig.save(extTileIdMap);
			extTileIdMap.setDirty(false);
		}
	}
}