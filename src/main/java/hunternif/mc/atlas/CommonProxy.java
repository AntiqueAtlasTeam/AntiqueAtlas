package hunternif.mc.atlas;

import hunternif.mc.atlas.core.BiomeDetectorBase;
import hunternif.mc.atlas.ext.ExtTileConfig;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.Log;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

public class CommonProxy {
	static File configDir;

	private static ExtTileIdMap extTileIdMap;
	private static ExtTileConfig extTileConfig;

	public CommonProxy() {
		configDir = new File("./config", "antiqueatlas");
		configDir.mkdir();
		extTileIdMap = ExtTileIdMap.instance();
		extTileConfig = new ExtTileConfig(new File(configDir, "tileids.json"));
		extTileConfig.load(extTileIdMap);

		// Assign default values AFTER the config file loads, so that the old saved values are kept:
		registerVanillaCustomTiles();
		checkSaveConfig();

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

		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_END_ISLAND);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_END_ISLAND_PLANTS);
		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_END_VOID);

		extTileIdMap.getOrCreatePseudoBiomeID(ExtTileIdMap.TILE_RAVINE);
	}

	public void openAtlasGUI(ItemStack stack) {}

	public void openAtlasGUI() {}

	public File getConfigDir(){
		return configDir;
	}

	/** When a world is saved, so is the custom tile id config. */
	// TODO FABRIC
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
