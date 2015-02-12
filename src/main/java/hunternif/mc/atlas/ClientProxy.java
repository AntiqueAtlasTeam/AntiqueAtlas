package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.TextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.client.BiomeTextureConfig;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetConfig;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureConfig;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.ext.VillageWatcher;
import hunternif.mc.atlas.marker.MarkerTextureConfig;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.marker.NetherPortalWatcher;
import hunternif.mc.atlas.util.Log;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
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
	private ExtTileTextureMap tileTextureMap;
	private ExtTileTextureConfig tileTextureConfig;
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
		// Prevent rewriting of the config while no changes have been made:
		textureSetMap.setDirty(false);
		// Register a texture set so that it provides an example for the config:
		textureSetMap.register(TEST);

		// Legacy file name:
		File biomeTextureConfigFile = new File(configDir, "textures.json");
		if (biomeTextureConfigFile.exists()) {
			biomeTextureConfigFile.renameTo(new File(configDir, "biome_textures.json"));
		}
		biomeTextureMap = BiomeTextureMap.instance();
		biomeTextureConfig = new BiomeTextureConfig(new File(configDir, "biome_textures.json"), textureSetMap);
		biomeTextureConfig.load(biomeTextureMap);
		// Prevent rewriting of the config while no changes have been made:
		biomeTextureMap.setDirty(false);
		assignVanillaBiomeTextures();
		
		tileTextureMap = ExtTileTextureMap.instance();
		tileTextureConfig = new ExtTileTextureConfig(new File(configDir, "tile_textures.json"), textureSetMap);
		tileTextureConfig.load(tileTextureMap);
		// Prevent rewriting of the config while no changes have been made:
		tileTextureMap.setDirty(false);
		registerVanillaCustomTileTextures();
		
		markerTextureMap = MarkerTextureMap.instance();
		markerTextureConfig = new MarkerTextureConfig(new File(configDir, "marker_textures.json"));
		markerTextureConfig.load(markerTextureMap);
		// Prevent rewriting of the config while no changes have been made:
		markerTextureMap.setDirty(false);
		registerDefaultMarkers();
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
		map.register(ICE);
		map.register(SHORE);
		map.register(ROCK_SHORE);
		map.register(DESERT);
		map.register(PLAINS);
		map.register(SUNFLOWERS);
		map.register(HILLS);
		map.register(DESERT_HILLS);
		
		map.register(ICE_SPIKES);
		map.register(SNOW_PINES);
		map.register(SNOW_PINES_HILLS);
		map.register(SNOW_HILLS);
		map.register(SNOW);
		
		map.register(MOUNTAINS_NAKED);
		map.register(MOUNTAINS);
		map.register(MOUNTAINS_SNOW_CAPS);
		map.register(MOUNTAINS_ALL);
		
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
		map.register(SAVANNA_CLIFFS);
		map.register(PLATEAU_SAVANNA_M);
		map.register(MESA);
		map.register(BRYCE);
		map.register(PLATEAU_MESA);
		map.register(PLATEAU_MESA_LOW);
		map.register(PLATEAU_MESA_TREES);
		map.register(PLATEAU_MESA_TREES_LOW);
		map.register(PLATEAU_SAVANNA);
		
		map.register(MEGA_SPRUCE);
		map.register(MEGA_SPRUCE_HILLS);
		map.register(MEGA_TAIGA);
		map.register(MEGA_TAIGA_HILLS);
		
		map.register(SWAMP);
		map.register(SWAMP_HILLS);
		map.register(MUSHROOM);
		map.register(WATER);
		map.register(LAVA);
		map.register(LAVA_SHORE);
		map.register(CAVE_WALLS);
		map.register(HOUSE);
		map.register(FENCE);
		
		map.register(NETHER_BRIDGE);
		map.register(NETHER_BRIDGE_X);
		map.register(NETHER_BRIDGE_Z);
		map.register(NETHER_BRIDGE_END_X);
		map.register(NETHER_BRIDGE_END_Z);
		map.register(NETHER_BRIDGE_GATE);
		map.register(NETHER_TOWER);
		map.register(NETHER_WALL);
		map.register(NETHER_HALL);
		map.register(NETHER_FORT_STAIRS);
		map.register(NETHER_THRONE);
	}
	
	/** Assign default textures to vanilla biomes. The textures are assigned
	 * only if the biome was not in the config. This prevents unnecessary
	 * overwriting, to aid people who manually modify the config. */
	private void assignVanillaBiomeTextures() {
		setBiomeTextureIfNone(ocean, WATER);
		setBiomeTextureIfNone(deepOcean, WATER);
		setBiomeTextureIfNone(river, WATER);
		setBiomeTextureIfNone(frozenOcean, ICE);
		setBiomeTextureIfNone(frozenRiver, ICE);
		setBiomeTextureIfNone(beach, SHORE);
		setBiomeTextureIfNone(coldBeach, SHORE);
		setBiomeTextureIfNone(stoneBeach, ROCK_SHORE);
		setBiomeTextureIfNone(desert, DESERT);
		setBiomeTextureIfNone(desert.biomeID + 128, DESERT);
		setBiomeTextureIfNone(desertHills, DESERT_HILLS);
		setBiomeTextureIfNone(plains, PLAINS);
		setBiomeTextureIfNone(plains.biomeID + 128, SUNFLOWERS);
		setBiomeTextureIfNone(icePlains, SNOW);
		setBiomeTextureIfNone(icePlains.biomeID + 128, ICE_SPIKES); // this is a biome mutation
		setBiomeTextureIfNone(iceMountains, SNOW_HILLS);
		setBiomeTextureIfNone(extremeHills, MOUNTAINS);
		setBiomeTextureIfNone(extremeHillsEdge, MOUNTAINS);
		setBiomeTextureIfNone(extremeHills.biomeID + 128, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(extremeHillsPlus, MOUNTAINS_ALL);
		setBiomeTextureIfNone(extremeHillsPlus.biomeID + 128, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(forest, FOREST);
		setBiomeTextureIfNone(forest.biomeID + 128, FOREST_FLOWERS);
		setBiomeTextureIfNone(forestHills, FOREST_HILLS);
		setBiomeTextureIfNone(roofedForest, DENSE_FOREST);
		setBiomeTextureIfNone(roofedForest.biomeID + 128, DENSE_FOREST_HILLS); //TODO roofed forest M has steeper cliffs
		setBiomeTextureIfNone(birchForest, BIRCH);
		setBiomeTextureIfNone(birchForest.biomeID + 128, TALL_BIRCH);
		setBiomeTextureIfNone(birchForestHills, BIRCH_HILLS);
		setBiomeTextureIfNone(birchForestHills.biomeID + 128, TALL_BIRCH_HILLS);
		setBiomeTextureIfNone(jungle, JUNGLE);
		setBiomeTextureIfNone(jungle.biomeID + 128, JUNGLE_CLIFFS);
		setBiomeTextureIfNone(jungleHills, JUNGLE_HILLS);
		setBiomeTextureIfNone(jungleEdge, JUNGLE_EDGE);
		setBiomeTextureIfNone(jungleEdge.biomeID + 128, JUNGLE_EDGE_HILLS);
		setBiomeTextureIfNone(taiga, PINES);
		setBiomeTextureIfNone(taiga.biomeID + 128, PINES_HILLS);
		setBiomeTextureIfNone(taigaHills, PINES_HILLS);
		setBiomeTextureIfNone(coldTaiga, SNOW_PINES);
		setBiomeTextureIfNone(coldTaiga.biomeID + 128, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(coldTaigaHills, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(megaTaiga, MEGA_TAIGA);
		setBiomeTextureIfNone(megaTaiga.biomeID + 128, MEGA_SPRUCE);
		setBiomeTextureIfNone(megaTaigaHills, MEGA_TAIGA_HILLS);
		setBiomeTextureIfNone(megaTaigaHills.biomeID + 128, MEGA_SPRUCE_HILLS);
		setBiomeTextureIfNone(swampland, SWAMP);
		setBiomeTextureIfNone(swampland.biomeID + 128, SWAMP_HILLS);
		setBiomeTextureIfNone(sky, SHORE);
		setBiomeTextureIfNone(hell, CAVE_WALLS);
		setBiomeTextureIfNone(mushroomIsland, MUSHROOM);
		setBiomeTextureIfNone(mushroomIslandShore, SHORE);
		setBiomeTextureIfNone(savanna, SAVANNA);
		setBiomeTextureIfNone(savanna.biomeID + 128, SAVANNA_CLIFFS);
		setBiomeTextureIfNone(mesa, MESA);
		setBiomeTextureIfNone(mesa.biomeID + 128, BRYCE);
		setBiomeTextureIfNone(mesaPlateau, PLATEAU_MESA);
		setBiomeTextureIfNone(mesaPlateau_F, PLATEAU_MESA_TREES);
		setBiomeTextureIfNone(mesaPlateau.biomeID + 128, PLATEAU_MESA_LOW);
		setBiomeTextureIfNone(mesaPlateau_F.biomeID + 128, PLATEAU_MESA_TREES_LOW);
		setBiomeTextureIfNone(savannaPlateau, PLATEAU_SAVANNA);
		setBiomeTextureIfNone(savannaPlateau.biomeID + 128, PLATEAU_SAVANNA_M);
	}
	/** Only applies the change if no texture is registered for this biome.
	 * This prevents overwriting of the config when there is no real change. */
	private void setBiomeTextureIfNone(int biomeID, TextureSet textureSet) {
		if (!biomeTextureMap.isRegistered(biomeID)) {
			biomeTextureMap.setTexture(biomeID, textureSet);
		}
	}
	private void setBiomeTextureIfNone(BiomeGenBase biome, TextureSet textureSet) {
		setBiomeTextureIfNone(biome.biomeID, textureSet);
	}
	
	/** Load default marker textures. */
	private void registerDefaultMarkers() {;
		setMarkerTextureIfNone("google", Textures.MARKER_GOOGLE_MARKER);
		setMarkerTextureIfNone("red_x_large", Textures.MARKER_RED_X_LARGE);
		setMarkerTextureIfNone("red_x_small", Textures.MARKER_RED_X_SMALL);
		setMarkerTextureIfNone(VillageWatcher.MARKER, Textures.MARKER_VILLAGE);
		setMarkerTextureIfNone("diamond", Textures.MARKER_DIAMOND);
		setMarkerTextureIfNone("bed", Textures.MARKER_BED);
		setMarkerTextureIfNone("pickaxe", Textures.MARKER_PICKAXE);
		setMarkerTextureIfNone("sword", Textures.MARKER_SWORD);
		setMarkerTextureIfNone(NetherPortalWatcher.MARKER_PORTAL, Textures.MARKER_NETHER_PORTAL);
	}
	/** Only applies the change if no texture is registered for this marker type.
	 * This prevents overwriting of the config when there is no real change. */
	private void setMarkerTextureIfNone(String markerType, ResourceLocation texture) {
		if (!markerTextureMap.isRegistered(markerType)) {
			markerTextureMap.setTexture(markerType, texture);
		}
	}
	
	/** Assign default textures to the pseudo-biomes used for vanilla Minecraft.
	 * The pseudo-biomes are: villages houses, village territory and lava. */
	private void registerVanillaCustomTileTextures() {
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_HOUSE, HOUSE);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_TERRITORY, FENCE);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_LAVA, LAVA);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_LAVA_SHORE, LAVA_SHORE);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_BRIDGE, NETHER_BRIDGE);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_BRIDGE_X, NETHER_BRIDGE_X);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_BRIDGE_Z, NETHER_BRIDGE_Z);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_BRIDGE_END_X, NETHER_BRIDGE_END_X);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_BRIDGE_END_Z, NETHER_BRIDGE_END_Z);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_BRIDGE_GATE, NETHER_BRIDGE_GATE);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_TOWER, NETHER_TOWER);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_WALL, NETHER_WALL);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_HALL, NETHER_HALL);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_FORT_STAIRS, NETHER_FORT_STAIRS);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_NETHER_THRONE, NETHER_THRONE);
	}
	/** Only applies the change if no texture is registered for this tile name.
	 * This prevents overwriting of the config when there is no real change. */
	private void setCustomTileTextureIfNone(String tileName, TextureSet textureSet) {
		if (!tileTextureMap.isRegistered(tileName)) {
			tileTextureMap.setTexture(tileName, textureSet);
		}
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
		if (tileTextureMap.isDirty()) {
			Log.info("Saving tile texture config");
			tileTextureConfig.save(tileTextureMap);
			tileTextureMap.setDirty(false);
		}
		if (markerTextureMap.isDirty()) {
			Log.info("Saving marker texture config");
			markerTextureConfig.save(markerTextureMap);
			markerTextureMap.setDirty(false);
		}
	}
}