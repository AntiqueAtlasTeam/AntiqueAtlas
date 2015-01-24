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
		map.register(SAND);
		map.register(PLAINS);
		map.register(SNOW);
		map.register(ICE_SPIKES);
		map.register(MOUNTAINS);
		map.register(HILLS);
		map.register(FOREST);
		map.register(FOREST_HILLS);
		map.register(JUNGLE);
		map.register(JUNGLE_HILLS);
		map.register(PINES);
		map.register(PINES_HILLS);
		map.register(SWAMP);
		map.register(MUSHROOM);
		map.register(WATER);
		map.register(HOUSE);
		map.register(FENCE);
	}
	
	/** Assign default textures to vanilla biomes. */
	private void assignVanillaBiomeTextures() {
		TileAPI api = AtlasAPI.getTileAPI();
		api.setBiomeTexture(ocean,			WATER);
		api.setBiomeTexture(river,			WATER);
		api.setBiomeTexture(frozenOcean,	ICE);
		api.setBiomeTexture(frozenRiver,	ICE);
		api.setBiomeTexture(beach,			SHORE);
		api.setBiomeTexture(desert,			SAND);
		api.setBiomeTexture(plains,			PLAINS);
		api.setBiomeTexture(icePlains,		SNOW);
		api.setBiomeTexture(icePlains.biomeID + 128, ICE_SPIKES); // this is a biome mutation
		api.setBiomeTexture(coldBeach,		SHORE);
		api.setBiomeTexture(jungleHills,	JUNGLE_HILLS);
		api.setBiomeTexture(forestHills,	FOREST_HILLS);
		api.setBiomeTexture(desertHills,	HILLS);
		api.setBiomeTexture(extremeHills,	MOUNTAINS_FEW_TREES);
		api.setBiomeTexture(extremeHillsEdge, MOUNTAINS_FEW_TREES);
		api.setBiomeTexture(iceMountains,	MOUNTAINS);
		api.setBiomeTexture(forest,			FOREST);
		api.setBiomeTexture(jungle,			JUNGLE);
		api.setBiomeTexture(taiga,			PINES);
		api.setBiomeTexture(taigaHills,		PINES_HILLS);
		api.setBiomeTexture(coldTaiga,		SNOW_PINES);
		api.setBiomeTexture(coldTaigaHills,	SNOW_PINES_HILLS);
		api.setBiomeTexture(swampland,		SWAMP);
		api.setBiomeTexture(sky,			SHORE);
		//api.setBiomeTexture(hell,			NETHER);
		api.setBiomeTexture(mushroomIsland, MUSHROOM);
		api.setBiomeTexture(mushroomIslandShore, SHORE);
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
			AntiqueAtlasMod.logger.info("Saving texture set config");
			textureSetConfig.save(textureSetMap);
			textureSetMap.setDirty(false);
		}
		if (biomeTextureMap.isDirty()) {
			AntiqueAtlasMod.logger.info("Saving biome texture config");
			biomeTextureConfig.save(biomeTextureMap);
			biomeTextureMap.setDirty(false);
		}
		if (markerTextureMap.isDirty()) {
			AntiqueAtlasMod.logger.info("Saving marker texture config");
			markerTextureConfig.save(markerTextureMap);
			markerTextureMap.setDirty(false);
		}
	}
}