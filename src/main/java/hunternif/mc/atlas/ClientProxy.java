package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureConfig;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.marker.MarkerTextureConfig;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
	
	//TODO save all config files automatically if anything was changed.
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		guiAtlas = new GuiAtlas();
		registerVillageTiles();
		
		updateBiomeTextureConfig();
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		assignVanillaTextures();
		registerDefaultMarker();
		
		updateBiomeTextureConfig();
		updateMarkerTextureConfig();
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
			guiAtlas.updateL18n();
			mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
		}
	}
	
	/** Assign default textures to vanilla biomes. */
	private void assignVanillaTextures() {
		BiomeAPI api = AtlasAPI.getBiomeAPI();
		api.setTexture(ocean,			WATER);
		api.setTexture(river,			WATER);
		api.setTexture(frozenOcean,	FROZEN_WATER);
		api.setTexture(frozenRiver,	FROZEN_WATER);
		api.setTexture(beach,			BEACH);
		api.setTexture(desert,		SAND);
		api.setTexture(plains,		PLAINS);
		api.setTexture(icePlains,	SNOW);
		api.setTexture(jungleHills,	JUNGLE_HILLS);
		api.setTexture(forestHills,	FOREST_HILLS);
		api.setTexture(desertHills,	HILLS);
		api.setTexture(extremeHills,	MOUNTAINS);
		api.setTexture(extremeHillsEdge, MOUNTAINS);
		api.setTexture(iceMountains,	MOUNTAINS);
		api.setTexture(forest,		FOREST);
		api.setTexture(jungle,		JUNGLE);
		api.setTexture(taiga,			PINES);
		api.setTexture(taigaHills,	PINES_HILLS);
		api.setTexture(swampland,		SWAMP);
		api.setTexture(sky,			BEACH);
		//api.addTexture(hell,		NETHER);
		api.setTexture(mushroomIsland, MUSHROOM);
		api.setTexture(mushroomIslandShore, BEACH);
	}
	
	private void registerDefaultMarker() {;
		MarkerAPI api = AtlasAPI.getMarkerAPI();
		api.setTexture("google", Textures.MARKER_GOOGLE_MARKER);
		api.setTexture("red_x_large", Textures.MARKER_RED_X_LARGE);
		api.setTexture("red_x_small", Textures.MARKER_RED_X_SMALL);
		api.setTexture("village", Textures.MARKER_VILLAGE);
	}
	
	private void registerVillageTiles() {
		TileAPI api = AtlasAPI.getTileAPI();
		api.setTexture(ExtTileIdMap.TILE_VILLAGE_HOUSE, StandardTextureSet.HOUSE);
		api.setTexture(ExtTileIdMap.TILE_VILLAGE_TERRITORY, StandardTextureSet.FENCE);
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}
}