package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.TextureSet.*;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.biome.Biome;

import hunternif.mc.atlas.client.BiomeTextureConfig;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetConfig;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.client.gui.ExportProgressOverlay;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureConfig;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.marker.MarkerTextureConfig;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {
	private TextureSetMap textureSetMap;
	private TextureSetConfig textureSetConfig;
	private BiomeTextureMap biomeTextureMap;
	private BiomeTextureConfig biomeTextureConfig;
	private ExtTileTextureMap tileTextureMap;
	private ExtTileTextureConfig tileTextureConfig;
	private MarkerTextureConfig markerTextureConfig;
	
	private GuiAtlas guiAtlas;
	
	@Override
	public MinecraftServer getServer() {
		return FMLClientHandler.instance().getServer();
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		
		MinecraftForge.EVENT_BUS.register(ExportProgressOverlay.INSTANCE);
		
		//TODO Enforce texture config loading process as follows:
		// 1. pre-init: Antique Atlas defaults are loaded, config files are read.
		// 2. init: mods set their custom textures. Those loaded from the config must not be overwritten!
		
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
		
		if(Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
			((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
		}
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		markerTextureConfig = new MarkerTextureConfig(new File(configDir, "markers.json"));
		markerTextureConfig.load(MarkerRegistry.INSTANCE);
		// Prevent rewriting of the config while no changes have been made:
		MarkerRegistry.INSTANCE.setDirty(true);
		
		for (MarkerType type : MarkerRegistry.getValues()) {
			type.initMips();
		}
		guiAtlas = new GuiAtlas();
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(AntiqueAtlasMod.itemAtlas, stack -> new ModelResourceLocation(AntiqueAtlasMod.ID + ":antiqueAtlas", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(AntiqueAtlasMod.itemEmptyAtlas, 0, new ModelResourceLocation(AntiqueAtlasMod.ID + ":emptyAntiqueAtlas", "inventory"));
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		guiAtlas.setMapScale(AntiqueAtlasMod.settings.defaultScale);
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
		map.register(LIBRARY);
		map.register(L_HOUSE);
		map.register(SMITHY);
		map.register(FARMLAND_LARGE);
		map.register(FARMLAND_SMALL);
		map.register(WELL);
		map.register(VILLAGE_TORCH);
//		map.register(VILLAGE_PATH_X);
//		map.register(VILLAGE_PATH_Z);
		map.register(HUT);
		map.register(HOUSE_SMALL);
		map.register(BUTCHERS_SHOP);
		map.register(CHURCH);
		
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
		
		map.register(END_ISLAND);
		map.register(END_ISLAND_PLANTS);
		map.register(END_VOID);
	}
	
	/** Assign default textures to vanilla biomes. The textures are assigned
	 * only if the biome was not in the config. This prevents unnecessary
	 * overwriting, to aid people who manually modify the config. */
	private void assignVanillaBiomeTextures() {
		setBiomeTextureIfNone(Biomes.OCEAN, WATER);
		setBiomeTextureIfNone(Biomes.DEEP_OCEAN, WATER);
		setBiomeTextureIfNone(Biomes.RIVER, WATER); //
		setBiomeTextureIfNone(Biomes.FROZEN_OCEAN, ICE);
		setBiomeTextureIfNone(Biomes.FROZEN_RIVER, ICE);
		setBiomeTextureIfNone(Biomes.BEACH, SHORE);
		setBiomeTextureIfNone(Biomes.COLD_BEACH, SHORE);
		setBiomeTextureIfNone(Biomes.STONE_BEACH, ROCK_SHORE);
		setBiomeTextureIfNone(Biomes.DESERT, DESERT);
		setBiomeTextureIfNone(Biomes.MUTATED_DESERT, DESERT);
		setBiomeTextureIfNone(Biomes.DESERT_HILLS, DESERT_HILLS);
		setBiomeTextureIfNone(Biomes.PLAINS, PLAINS);
		setBiomeTextureIfNone(Biomes.MUTATED_PLAINS, SUNFLOWERS);
		setBiomeTextureIfNone(Biomes.ICE_PLAINS, SNOW);
		setBiomeTextureIfNone(Biomes.MUTATED_ICE_FLATS, ICE_SPIKES); // this is a biome mutation
		setBiomeTextureIfNone(Biomes.ICE_MOUNTAINS, SNOW_HILLS);
		setBiomeTextureIfNone(Biomes.EXTREME_HILLS, MOUNTAINS);
		setBiomeTextureIfNone(Biomes.EXTREME_HILLS_EDGE, MOUNTAINS);
		setBiomeTextureIfNone(Biomes.MUTATED_EXTREME_HILLS, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(Biomes.EXTREME_HILLS_WITH_TREES, MOUNTAINS_ALL);
		setBiomeTextureIfNone(Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(Biomes.FOREST, FOREST);
		setBiomeTextureIfNone(Biomes.MUTATED_FOREST, FOREST_FLOWERS);
		setBiomeTextureIfNone(Biomes.FOREST_HILLS, FOREST_HILLS);
		setBiomeTextureIfNone(Biomes.ROOFED_FOREST, DENSE_FOREST);
		setBiomeTextureIfNone(Biomes.MUTATED_ROOFED_FOREST, DENSE_FOREST_HILLS); //TODO roofed forest M has steeper cliffs
		setBiomeTextureIfNone(Biomes.BIRCH_FOREST, BIRCH);
		setBiomeTextureIfNone(Biomes.MUTATED_BIRCH_FOREST, TALL_BIRCH);
		setBiomeTextureIfNone(Biomes.BIRCH_FOREST_HILLS, BIRCH_HILLS);
		setBiomeTextureIfNone(Biomes.MUTATED_BIRCH_FOREST_HILLS, TALL_BIRCH_HILLS);
		setBiomeTextureIfNone(Biomes.JUNGLE, JUNGLE);
		setBiomeTextureIfNone(Biomes.MUTATED_JUNGLE, JUNGLE_CLIFFS);
		setBiomeTextureIfNone(Biomes.JUNGLE_HILLS, JUNGLE_HILLS);
		setBiomeTextureIfNone(Biomes.JUNGLE_EDGE, JUNGLE_EDGE);
		setBiomeTextureIfNone(Biomes.MUTATED_JUNGLE_EDGE, JUNGLE_EDGE_HILLS);
		setBiomeTextureIfNone(Biomes.TAIGA, PINES);
		setBiomeTextureIfNone(Biomes.MUTATED_TAIGA, PINES_HILLS);
		setBiomeTextureIfNone(Biomes.TAIGA_HILLS, PINES_HILLS);
		setBiomeTextureIfNone(Biomes.COLD_TAIGA, SNOW_PINES);
		setBiomeTextureIfNone(Biomes.MUTATED_TAIGA_COLD, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(Biomes.COLD_TAIGA_HILLS, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(Biomes.REDWOOD_TAIGA, MEGA_TAIGA);
		setBiomeTextureIfNone(Biomes.MUTATED_REDWOOD_TAIGA, MEGA_SPRUCE);
		setBiomeTextureIfNone(Biomes.REDWOOD_TAIGA_HILLS, MEGA_TAIGA_HILLS);
		setBiomeTextureIfNone(Biomes.MUTATED_REDWOOD_TAIGA_HILLS, MEGA_SPRUCE_HILLS);
		setBiomeTextureIfNone(Biomes.SWAMPLAND, SWAMP);
		setBiomeTextureIfNone(Biomes.MUTATED_SWAMPLAND, SWAMP_HILLS);
		setBiomeTextureIfNone(Biomes.SKY, SHORE);
		setBiomeTextureIfNone(Biomes.HELL, CAVE_WALLS);
		setBiomeTextureIfNone(Biomes.VOID, END_VOID);
		setBiomeTextureIfNone(Biomes.MUSHROOM_ISLAND, MUSHROOM);
		setBiomeTextureIfNone(Biomes.MUSHROOM_ISLAND_SHORE, SHORE);
		setBiomeTextureIfNone(Biomes.SAVANNA, SAVANNA);
		setBiomeTextureIfNone(Biomes.MUTATED_SAVANNA, SAVANNA_CLIFFS);
		setBiomeTextureIfNone(Biomes.MESA, MESA);
		setBiomeTextureIfNone(Biomes.MUTATED_MESA, BRYCE);
		setBiomeTextureIfNone(Biomes.MESA_CLEAR_ROCK, PLATEAU_MESA);
		setBiomeTextureIfNone(Biomes.MESA_ROCK, PLATEAU_MESA_TREES);
		setBiomeTextureIfNone(Biomes.MUTATED_MESA_CLEAR_ROCK, PLATEAU_MESA_LOW);
		setBiomeTextureIfNone(Biomes.MUTATED_MESA_ROCK, PLATEAU_MESA_TREES_LOW);
		setBiomeTextureIfNone(Biomes.SAVANNA_PLATEAU, PLATEAU_SAVANNA);
		setBiomeTextureIfNone(Biomes.MUTATED_SAVANNA_ROCK, PLATEAU_SAVANNA_M);
	}
	/** Only applies the change if no texture is registered for this biome.
	 * This prevents overwriting of the config when there is no real change. */
	private void setBiomeTextureIfNone(int biomeID, TextureSet textureSet) {
		if (!biomeTextureMap.isRegistered(biomeID)) {
			biomeTextureMap.setTexture(biomeID, textureSet);
		}
	}
	private void setBiomeTextureIfNone(Biome biome, TextureSet textureSet) {
		setBiomeTextureIfNone(Biome.getIdForBiome(biome), textureSet);
	}
	
	/** Assign default textures to the pseudo-biomes used for vanilla Minecraft.
	 * The pseudo-biomes are: villages houses, village territory and lava. */
	private void registerVanillaCustomTileTextures() {
		// Village:
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_LIBRARY, LIBRARY);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_SMITHY, SMITHY);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_L_HOUSE, L_HOUSE);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_FARMLAND_LARGE, FARMLAND_LARGE);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_FARMLAND_SMALL, FARMLAND_SMALL);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_WELL, WELL);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_TORCH, VILLAGE_TORCH);
//		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_PATH_X, VILLAGE_PATH_X);
//		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_PATH_Z, VILLAGE_PATH_Z);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_HUT, HUT);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_SMALL_HOUSE, HOUSE_SMALL);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_BUTCHERS_SHOP, BUTCHERS_SHOP);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_CHURCH, CHURCH);
		
		// Nether & Nether Fortress:
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

		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_ISLAND, END_ISLAND);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_ISLAND_PLANTS, END_ISLAND_PLANTS);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_VOID, END_VOID);
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
		return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
	}
	
	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft() : super.getThreadFromContext(ctx));
	}
	
	public File getConfigDir(){
		return configDir;
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
		if (MarkerRegistry.INSTANCE.isDirty()) {
			Log.info("Saving marker config");
			markerTextureConfig.save(MarkerRegistry.INSTANCE);
			MarkerRegistry.INSTANCE.setDirty(false);
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		for (MarkerType type : MarkerRegistry.getValues()) {
			type.initMips();
		}
	}
}