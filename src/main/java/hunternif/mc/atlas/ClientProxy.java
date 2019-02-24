package hunternif.mc.atlas;

import hunternif.mc.atlas.client.*;
import hunternif.mc.atlas.client.gui.ExportProgressOverlay;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureConfig;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.marker.MarkerTextureConfig;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import none.TODO_1_13_2_none_acv;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static hunternif.mc.atlas.client.TextureSet.*;

public class ClientProxy extends CommonProxy implements SimpleSynchronousResourceReloadListener {
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

		tileTextureMap = ExtTileTextureMap.instance();
		tileTextureConfig = new ExtTileTextureConfig(new File(configDir, "tile_textures.json"), textureSetMap);
		tileTextureConfig.load(tileTextureMap);
		// Prevent rewriting of the config while no changes have been made:
		tileTextureMap.setDirty(false);
		registerVanillaCustomTileTextures();

		if(MinecraftClient.getInstance().getResourceManager() instanceof ReloadableResourceManager) {
			((ReloadableResourceManager)MinecraftClient.getInstance().getResourceManager()).registerListener(this);
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		biomeTextureMap = BiomeTextureMap.instance();
		biomeTextureConfig = new BiomeTextureConfig(new File(configDir, "biome_textures.json"), textureSetMap);
		biomeTextureConfig.load(biomeTextureMap);
		// Prevent rewriting of the config while no changes have been made:
		biomeTextureMap.setDirty(false);
		assignVanillaBiomeTextures();

		markerTextureConfig = new MarkerTextureConfig(new File(configDir, "markers.json"));
		markerTextureConfig.load(MarkerRegistry.INSTANCE);
		// Prevent rewriting of the config while no changes have been made:
		MarkerRegistry.INSTANCE.setDirty(true);

		guiAtlas = new GuiAtlas();
		for (MarkerType type : MarkerRegistry.iterable()) {
			type.initMips();
		}

		if (!SettingsConfig.gameplay.itemNeeded) {
            KeyHandler.registerBindings();
            MinecraftForge.EVENT_BUS.register(new KeyHandler());
        }

		MinecraftForge.EVENT_BUS.register(this);
	}


	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		guiAtlas.setMapScale(SettingsConfig.userInterface.defaultScale);
	}

	@Override
	public void openAtlasGUI(ItemStack stack) {
	    openAtlasGUI(guiAtlas.prepareToOpen(stack));
	}

	@Override
	public void openAtlasGUI() {
	    openAtlasGUI(guiAtlas.prepareToOpen());
    }

    private void openAtlasGUI(GuiAtlas gui) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen == null) { // In-game screen
            guiAtlas.updateL18n();
            mc.openScreen(gui);
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
		map.register(RAVINE);

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
		/* setBiomeTextureIfNone(ays.TODO_1_12_2_a, WATER);
		setBiomeTextureIfNone(ays.TODO_1_12_2_z, WATER);
		setBiomeTextureIfNone(ays.TODO_1_12_2_i, WATER); //
		setBiomeTextureIfNone(ays.TODO_1_12_2_l, ICE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_m, ICE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_r, SHORE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_B, SHORE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_A, ROCK_SHORE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_d, DESERT);
		setBiomeTextureIfNone(ays.TODO_1_12_2_R, DESERT);
		setBiomeTextureIfNone(ays.TODO_1_12_2_s, DESERT_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_c, PLAINS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_Q, SUNFLOWERS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_n, SNOW);
		setBiomeTextureIfNone(ays.TODO_1_12_2_W, ICE_SPIKES); // this is a biome mutation
		setBiomeTextureIfNone(ays.TODO_1_12_2_o, SNOW_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_e, MOUNTAINS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_v, MOUNTAINS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_S, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_J, MOUNTAINS_ALL);
		setBiomeTextureIfNone(ays.TODO_1_12_2_af, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_f, FOREST);
		setBiomeTextureIfNone(ays.TODO_1_12_2_T, FOREST_FLOWERS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_t, FOREST_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_E, DENSE_FOREST);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ab, DENSE_FOREST_HILLS); //TODO roofed forest M has steeper cliffs
		setBiomeTextureIfNone(ays.TODO_1_12_2_C, BIRCH);
		setBiomeTextureIfNone(ays.TODO_1_12_2_Z, TALL_BIRCH);
		setBiomeTextureIfNone(ays.TODO_1_12_2_D, BIRCH_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_aa, TALL_BIRCH_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_w, JUNGLE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_X, JUNGLE_CLIFFS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_x, JUNGLE_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_y, JUNGLE_EDGE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_Y, JUNGLE_EDGE_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_g, PINES);
		setBiomeTextureIfNone(ays.TODO_1_12_2_U, PINES_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_u, PINES_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_F, SNOW_PINES);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ac, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_G, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_H, MEGA_TAIGA);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ad, MEGA_SPRUCE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_I, MEGA_TAIGA_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ae, MEGA_SPRUCE_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_h, SWAMP);
		setBiomeTextureIfNone(ays.TODO_1_12_2_V, SWAMP_HILLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_k, SHORE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_j, CAVE_WALLS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_P, END_VOID);
		setBiomeTextureIfNone(ays.TODO_1_12_2_p, MUSHROOM);
		setBiomeTextureIfNone(ays.TODO_1_12_2_q, SHORE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_K, SAVANNA);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ag, SAVANNA_CLIFFS);
		setBiomeTextureIfNone(ays.TODO_1_12_2_M, MESA);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ai, BRYCE);
		setBiomeTextureIfNone(ays.TODO_1_12_2_O, PLATEAU_MESA);
		setBiomeTextureIfNone(ays.TODO_1_12_2_N, PLATEAU_MESA_TREES);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ak, PLATEAU_MESA_LOW);
		setBiomeTextureIfNone(ays.TODO_1_12_2_aj, PLATEAU_MESA_TREES_LOW);
		setBiomeTextureIfNone(ays.TODO_1_12_2_L, PLATEAU_SAVANNA);
		setBiomeTextureIfNone(ays.TODO_1_12_2_ah, PLATEAU_SAVANNA_M); */

		// Now let's register every other biome, they'll come from other mods
		for (Biome biome : Registry.BIOME) {
			BiomeTextureMap.instance().checkRegistration(biome);
		}
	}

	/** Only applies the change if no texture is registered for this biome.
	 * This prevents overwriting of the config when there is no real change. */
	private void setBiomeTextureIfNone(Biome biome, TextureSet textureSet) {
		if(!biomeTextureMap.isRegistered(biome)) {
			biomeTextureMap.setTexture(biome, textureSet);
		}
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

		setCustomTileTextureIfNone(ExtTileIdMap.TILE_RAVINE, RAVINE);
	}
	/** Only applies the change if no texture is registered for this tile name.
	 * This prevents overwriting of the config when there is no real change. */
	private void setCustomTileTextureIfNone(String tileName, TextureSet textureSet) {
		if (!tileTextureMap.isRegistered(tileName)) {
			tileTextureMap.setTexture(tileName, textureSet);
		}
	}

	@Override
	public PlayerEntity getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? MinecraftClient.getInstance().player : super.getPlayerEntity(ctx));
	}

	@Override
	public TODO_1_13_2_none_acv getThreadFromContext(MessageContext ctx) {
		return (ctx.side.isClient() ? MinecraftClient.getInstance() : super.getThreadFromContext(ctx));
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
	public Identifier getFabricId() {
		return new Identifier("antiqueatlas:proxy");
	}

	@Override
	public void apply(ResourceManager var1) {
		for (MarkerType type : MarkerRegistry.iterable()) {
			type.initMips();
		}
	}
}
