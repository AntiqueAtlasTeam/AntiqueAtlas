package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.TextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureConfig;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSetConfig;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.marker.MarkerTextureConfig;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.util.Log;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {
	private TextureSetMap textureSetMap;
	private TextureSetConfig textureSetConfig;
	private BiomeTextureMap biomeTextureMap;
	private BiomeTextureConfig biomeTextureConfig;
	private MarkerTextureMap markerTextureMap;
	private MarkerTextureConfig markerTextureConfig;
	
	private GuiAtlas guiAtlas;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		textureSetMap = TextureSetMap.instance();
		textureSetConfig = new TextureSetConfig(new File(configDir, "texture_sets.json"));
		// Register default values before the config file loads, possibly overwriting the,:
		registerDefaultTextureSets(textureSetMap);
		textureSetConfig.load(textureSetMap);
		// Prevent rewriting of the config while there haven't been any changes made:
		textureSetMap.setDirty(false);
		
		biomeTextureMap = BiomeTextureMap.instance();
		biomeTextureConfig = new BiomeTextureConfig(new File(configDir, "textures.json"), textureSetMap);
		// Assign default values before the config file loads, possibly overwriting them:
		assignVanillaBiomeTextures();
		registerVillageTiles();
		biomeTextureConfig.load(biomeTextureMap);
		// Prevent rewriting of the config while there haven't been any changes made:
		biomeTextureMap.setDirty(false);
		
		markerTextureMap = MarkerTextureMap.instance();
		markerTextureConfig = new MarkerTextureConfig(new File(configDir, "marker_textures.json"));
		// Assign default values before the config file loads, possibly overwriting them:
		registerDefaultMarker();
		markerTextureConfig.load(markerTextureMap);
		// Prevent rewriting of the config while there haven't been any changes made:
		markerTextureMap.setDirty(false);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		guiAtlas = new GuiAtlas();
		
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void openAtlasGUI(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) { // In-game screen
			guiAtlas.updateL18n();
			mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
		}
	}
	
	private void registerDefaultTextureSets(TextureSetMap map) {
		map.register(TEST);
		map.register(ICE);
		map.register(SHORE);
		map.register(ROCK_SHORE);
		map.register(SAND);
		map.register(PLAINS);
		
		map.register(ICE_SPIKES);
		map.register(SNOW_PINES);
		map.register(SNOW_PINES_HILLS);
		map.register(SNOW_HILLS);
		map.register(SNOW);
		
		map.register(MOUNTAINS_NAKED);
		map.register(MOUNTAINS);
		map.register(MOUNTAINS_SNOW_CAPS);
		map.register(MOUNTAINS_ALL);
		
		map.register(HILLS);
		map.register(FOREST);
		map.register(FOREST_HILLS);
		map.register(FOREST_FLOWERS);
		map.register(DENSE_FOREST);
		map.register(DENSE_FOREST_HILLS);
		map.register(BIRCH);
		map.register(BIRCH_HILLS);
		map.register(TALL_BIRCH);
		map.register(TALL_BIRCH_HILLS);
		map.register(DENSE_BIRCH);
		map.register(JUNGLE);
		map.register(JUNGLE_HILLS);
		map.register(JUNGLE_CLIFFS);
		map.register(JUNGLE_EDGE);
		map.register(JUNGLE_EDGE_HILLS);
		map.register(PINES);
		map.register(PINES_HILLS);
		map.register(SAVANNA);
		
		map.register(MEGA_SPRUCE);
		map.register(MEGA_SPRUCE_HILLS);
		map.register(MEGA_TAIGA);
		map.register(MEGA_TAIGA_HILLS);
		
		map.register(SWAMP);
		map.register(SWAMP_HILLS);
		map.register(MUSHROOM);
		map.register(WATER);
		map.register(HOUSE);
		map.register(FENCE);
	}
	
	/** Assign default textures to vanilla biomes. */
	private void assignVanillaBiomeTextures() {
		TileAPI api = AtlasAPI.getTileAPI();
		api.setBiomeTexture(ocean, WATER);
		api.setBiomeTexture(deepOcean, WATER);
		api.setBiomeTexture(river, WATER);
		api.setBiomeTexture(frozenOcean, ICE);
		api.setBiomeTexture(frozenRiver, ICE);
		api.setBiomeTexture(beach, SHORE);
		api.setBiomeTexture(coldBeach, SHORE);
		api.setBiomeTexture(stoneBeach, ROCK_SHORE);
		api.setBiomeTexture(desert, SAND);
		api.setBiomeTexture(plains, PLAINS);
		api.setBiomeTexture(plains.biomeID + 128, SUNFLOWERS);
		api.setBiomeTexture(icePlains, SNOW);
		api.setBiomeTexture(icePlains.biomeID + 128, ICE_SPIKES); // this is a biome mutation
		api.setBiomeTexture(desertHills, HILLS);
		api.setBiomeTexture(extremeHills, MOUNTAINS);
		api.setBiomeTexture(extremeHillsEdge, MOUNTAINS);
		api.setBiomeTexture(extremeHills.biomeID + 128, MOUNTAINS_SNOW_CAPS);
		api.setBiomeTexture(extremeHillsPlus, MOUNTAINS_ALL);
		api.setBiomeTexture(extremeHillsPlus.biomeID + 128, MOUNTAINS_SNOW_CAPS);
		api.setBiomeTexture(iceMountains, SNOW_HILLS);
		api.setBiomeTexture(forest, FOREST);
		api.setBiomeTexture(forest.biomeID + 128, FOREST_FLOWERS);
		api.setBiomeTexture(forestHills, FOREST_HILLS);
		api.setBiomeTexture(roofedForest, DENSE_FOREST);
		api.setBiomeTexture(roofedForest.biomeID + 128, DENSE_FOREST_HILLS); //TODO roofed forest M has steeper cliffs
		api.setBiomeTexture(birchForest, BIRCH);
		api.setBiomeTexture(birchForest.biomeID + 128, TALL_BIRCH);
		api.setBiomeTexture(birchForestHills, BIRCH_HILLS);
		api.setBiomeTexture(birchForestHills.biomeID + 128, TALL_BIRCH_HILLS);
		api.setBiomeTexture(jungle, JUNGLE);
		api.setBiomeTexture(jungle.biomeID + 128, JUNGLE_CLIFFS);
		api.setBiomeTexture(jungleHills, JUNGLE_HILLS);
		api.setBiomeTexture(jungleEdge, JUNGLE_EDGE);
		api.setBiomeTexture(jungleEdge.biomeID + 128, JUNGLE_EDGE_HILLS);
		api.setBiomeTexture(taiga, PINES);
		api.setBiomeTexture(taigaHills, PINES_HILLS);
		api.setBiomeTexture(coldTaiga, SNOW_PINES);
		api.setBiomeTexture(coldTaigaHills, SNOW_PINES_HILLS);
		api.setBiomeTexture(megaTaiga, MEGA_TAIGA);
		api.setBiomeTexture(megaTaiga.biomeID + 128, MEGA_SPRUCE);
		api.setBiomeTexture(megaTaigaHills, MEGA_TAIGA_HILLS);
		api.setBiomeTexture(megaTaigaHills.biomeID + 128, MEGA_SPRUCE_HILLS);
		api.setBiomeTexture(swampland, SWAMP);
		api.setBiomeTexture(swampland.biomeID + 128, SWAMP_HILLS);
		api.setBiomeTexture(sky, SHORE);
		//api.setBiomeTexture(hell, NETHER);
		api.setBiomeTexture(mushroomIsland, MUSHROOM);
		api.setBiomeTexture(mushroomIslandShore, SHORE);
		api.setBiomeTexture(savanna, SAVANNA);
	}
	
	/** Load default marker textures. */
	private void registerDefaultMarker() {;
		MarkerAPI api = AtlasAPI.getMarkerAPI();
		api.setTexture("google", Textures.MARKER_GOOGLE_MARKER);
		api.setTexture("red_x_large", Textures.MARKER_RED_X_LARGE);
		api.setTexture("red_x_small", Textures.MARKER_RED_X_SMALL);
		api.setTexture("village", Textures.MARKER_VILLAGE);
	}
	
	/** Assign default textures to the pseudo-biomes designating village houses
	 * and territory. */
	private void registerVillageTiles() {
		TileAPI api = AtlasAPI.getTileAPI();
		api.setCustomTileTexture(ExtTileIdMap.TILE_VILLAGE_HOUSE, HOUSE);
		api.setCustomTileTexture(ExtTileIdMap.TILE_VILLAGE_TERRITORY, FENCE);
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}
	
	/** Checks if any of the configs's data has been marked dirty and saves it. */
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (textureSetMap.isDirty()) {
			Log.info("Saving texture set config");
			textureSetConfig.save(textureSetMap);
			textureSetMap.setDirty(false);
		}
		if (biomeTextureMap.isDirty()) {
			Log.info("Saving biome texture config");
			biomeTextureConfig.save(biomeTextureMap);
			biomeTextureMap.setDirty(false);
		}
		if (markerTextureMap.isDirty()) {
			Log.info("Saving marker texture config");
			markerTextureConfig.save(markerTextureMap);
			markerTextureMap.setDirty(false);
		}
	}
}