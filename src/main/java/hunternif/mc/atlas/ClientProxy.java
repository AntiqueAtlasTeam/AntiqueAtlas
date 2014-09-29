package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.BiomeTextureConfig;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.marker.MarkerTextureConfig;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	private BiomeTextureConfig biomeTextureConfig;
	private MarkerTextureConfig markerTextureConfig;
	
	private GuiAtlas guiAtlas;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		biomeTextureConfig = new BiomeTextureConfig(new File(configDir, "textures.json"));
		biomeTextureConfig.load();
		markerTextureConfig = new MarkerTextureConfig(new File(configDir, "marker_textures.json"));
		markerTextureConfig.load();
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		guiAtlas = new GuiAtlas();
		if (registerVillageTiles()) {
			updateBiomeTextureConfig();
		}
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		if (assignVanillaTextures()) {
			// Only rewrite config, if new textures were automatically assigned.
			updateBiomeTextureConfig();
		}
		if (setDefaultMarker()) {
			updateMarkerTextureConfig();
		}
	}
	
	@Override
	public void updateBiomeTextureConfig() {
		biomeTextureConfig.save();
	}
	
	@Override
	public void updateMarkerTextureConfig() {
		markerTextureConfig.save();
	}
	
	@Override
	public void openAtlasGUI(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) { // In-game screen
			mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
		}
	}
	
	/** Assign default textures to vanilla biomes. Returns true if any texture
	 * was changed. */
	private boolean assignVanillaTextures() {
		boolean changed = false;
		BiomeAPI api = AtlasAPI.getBiomeAPI();
		changed |= api.setTextureIfNone(ocean,			WATER);
		changed |= api.setTextureIfNone(river,			WATER);
		changed |= api.setTextureIfNone(frozenOcean,	FROZEN_WATER);
		changed |= api.setTextureIfNone(frozenRiver,	FROZEN_WATER);
		changed |= api.setTextureIfNone(beach,			BEACH);
		changed |= api.setTextureIfNone(desert,		SAND);
		changed |= api.setTextureIfNone(plains,		PLAINS);
		changed |= api.setTextureIfNone(icePlains,	SNOW);
		changed |= api.setTextureIfNone(jungleHills,	JUNGLE_HILLS);
		changed |= api.setTextureIfNone(forestHills,	FOREST_HILLS);
		changed |= api.setTextureIfNone(desertHills,	HILLS);
		changed |= api.setTextureIfNone(extremeHills,	MOUNTAINS);
		changed |= api.setTextureIfNone(extremeHillsEdge, MOUNTAINS);
		changed |= api.setTextureIfNone(iceMountains,	MOUNTAINS);
		changed |= api.setTextureIfNone(forest,		FOREST);
		changed |= api.setTextureIfNone(jungle,		JUNGLE);
		changed |= api.setTextureIfNone(taiga,			PINES);
		changed |= api.setTextureIfNone(taigaHills,	PINES_HILLS);
		changed |= api.setTextureIfNone(swampland,		SWAMP);
		changed |= api.setTextureIfNone(sky,			BEACH);
		//changed |= api.addTextureIfNone(hell,		NETHER);
		changed |= api.setTextureIfNone(mushroomIsland, MUSHROOM);
		changed |= api.setTextureIfNone(mushroomIslandShore, BEACH);
		
		return changed;
	}
	
	private boolean setDefaultMarker() {
		boolean changed = false;
		MarkerAPI api = AtlasAPI.getMarkerAPI();
		changed |= api.setTextureIfNone("google", Textures.MARKER_GOOGLE_MARKER);
		changed |= api.setTextureIfNone("red_x_large", Textures.MARKER_RED_X_LARGE);
		changed |= api.setTextureIfNone("red_x_small", Textures.MARKER_RED_X_SMALL);
		return changed;
	}
	
	private boolean registerVillageTiles() {
		boolean changed = false;
		TileAPI api = AtlasAPI.getTileAPI();
		changed |= api.setTextureIfNone(ExtTileIdMap.TILE_VILLAGE_HOUSE, StandardTextureSet.HOUSE);
		changed |= api.setTextureIfNone(ExtTileIdMap.TILE_VILLAGE_TERRITORY, StandardTextureSet.FENCE);
		return changed;
	}
}