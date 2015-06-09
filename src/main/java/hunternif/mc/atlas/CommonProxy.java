package hunternif.mc.atlas;

import hunternif.mc.atlas.core.BiomeDetectorBase;
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
		// Assign default values AFTER the config file loads, so that the old saved values are kept:
		registerVanillaCustomTiles();
		checkSaveConfig();
	}
	
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		BiomeDetectorBase.scanBiomeTypes();
	}
	
	/** Register IDs for the pseudo-biomes used for vanilla Minecraft.
	 * The pseudo-biomes are: villages houses, village territory and lava. */
	private void registerVanillaCustomTiles() {
		// Village:
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_LIBRARY);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_SMITHY);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_L_HOUSE);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_FARMLAND_LARGE);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_FARMLAND_SMALL);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_WELL);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_TORCH);
//		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_PATH_X);
//		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_PATH_Z);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_HUT);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_SMALL_HOUSE);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_BUTCHERS_SHOP);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_VILLAGE_CHURCH);
		
		// Nether & Nether Fortress:
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_LAVA);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_LAVA_SHORE);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_BRIDGE);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_BRIDGE_X);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_BRIDGE_Z);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_BRIDGE_END_X);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_BRIDGE_END_Z);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_BRIDGE_GATE);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_TOWER);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_WALL);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_HALL);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_FORT_STAIRS);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_NETHER_THRONE);
	}
	
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
		checkSaveConfig();
	}
	
	private void checkSaveConfig() {
		if (extTileIdMap.isDirty()) {
			Log.info("Saving ext tile id config");
			extTileConfig.save(extTileIdMap);
			extTileIdMap.setDirty(false);
		}
	}
}