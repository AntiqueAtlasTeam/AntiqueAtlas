package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.*;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import static hunternif.mc.impl.atlas.client.TextureSet.*;

@Environment(EnvType.CLIENT)
public class ClientProxy implements SimpleSynchronousResourceReloadListener {
	private static TextureSetMap textureSetMap;
	private static TextureSetConfig textureSetConfig;
	private static BiomeTextureMap textureMap;
	private static MarkerTextureConfig markerTextureConfig;

	private static GuiAtlas guiAtlas;

	public void initClient() {
		//TODO Enforce texture config loading process as follows:
		// 1. pre-init: Antique Atlas defaults are loaded, config files are read.
		// 2. init: mods set their custom textures. Those loaded from the config must not be overwritten!

		textureSetMap = TextureSetMap.instance();
		textureSetConfig = new TextureSetConfig(textureSetMap);
		// Register default values before the config file loads, possibly overwriting the,:
		registerDefaultTextureSets(textureSetMap);
		// Prevent rewriting of the config while no changes have been made:
		textureSetMap.setDirty(false);

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(textureSetConfig);

		// Legacy file name:
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);

		// init
		textureMap = BiomeTextureMap.instance();
		registerVanillaCustomTileTextures();

		// Prevent rewriting of the config while no changes have been made:
		textureMap.setDirty(false);
		assignVanillaBiomeTextures();

		markerTextureConfig = new MarkerTextureConfig();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(markerTextureConfig);

		// Prevent rewriting of the config while no changes have been made:
//		MarkerType.REGISTRY.setDirty(true);

		for (MarkerType type : MarkerType.REGISTRY) {
			type.initMips();
		}

		if (!AntiqueAtlasMod.CONFIG.itemNeeded) {
            KeyHandler.registerBindings();
			ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::onClientTick);
        }

	}

	@Environment(EnvType.CLIENT)
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
		map.register(SAVANNA_PLATEAU);
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
		// Since biome categories are now vanilla, we only handle the
		// "edge cases".

		setBiomeTextureIfNone(Biomes.ICE_SPIKES, ICE_SPIKES); // this is a biome mutation
		setBiomeTextureIfNone(Biomes.SUNFLOWER_PLAINS, SUNFLOWERS);
		setBiomeTextureIfNone(Biomes.SNOWY_BEACH, SHORE);
		setBiomeTextureIfNone(Biomes.STONE_SHORE, ROCK_SHORE);

		setBiomeTextureIfNone(Biomes.SNOWY_MOUNTAINS, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(Biomes.MOUNTAINS, MOUNTAINS_ALL);
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_MOUNTAINS, MOUNTAINS_SNOW_CAPS);
		setBiomeTextureIfNone(Biomes.FOREST, FOREST);

		setBiomeTextureIfNone(Biomes.FLOWER_FOREST, FOREST_FLOWERS);
		setBiomeTextureIfNone(Biomes.BIRCH_FOREST, BIRCH);
		setBiomeTextureIfNone(Biomes.TALL_BIRCH_FOREST, TALL_BIRCH);
		setBiomeTextureIfNone(Biomes.BIRCH_FOREST_HILLS, BIRCH_HILLS);
		setBiomeTextureIfNone(Biomes.TALL_BIRCH_HILLS, TALL_BIRCH_HILLS);
		setBiomeTextureIfNone(Biomes.JUNGLE, JUNGLE);
		setBiomeTextureIfNone(Biomes.MODIFIED_JUNGLE_EDGE, JUNGLE_CLIFFS);
		setBiomeTextureIfNone(Biomes.JUNGLE_HILLS, JUNGLE_HILLS);
		setBiomeTextureIfNone(Biomes.JUNGLE_EDGE, JUNGLE_EDGE);
		setBiomeTextureIfNone(Biomes.TAIGA, PINES);
		setBiomeTextureIfNone(Biomes.TAIGA_HILLS, PINES_HILLS);
		setBiomeTextureIfNone(Biomes.TAIGA_HILLS, PINES_HILLS);
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA, SNOW_PINES);
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_HILLS, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_MOUNTAINS, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(Biomes.GIANT_TREE_TAIGA, MEGA_TAIGA);
		setBiomeTextureIfNone(Biomes.GIANT_SPRUCE_TAIGA, MEGA_SPRUCE);
		setBiomeTextureIfNone(Biomes.GIANT_TREE_TAIGA_HILLS, MEGA_TAIGA_HILLS);
		setBiomeTextureIfNone(Biomes.GIANT_SPRUCE_TAIGA_HILLS, MEGA_SPRUCE_HILLS);
		setBiomeTextureIfNone(Biomes.NETHER_WASTES, CAVE_WALLS);
		setBiomeTextureIfNone(Biomes.SOUL_SAND_VALLEY, DESERT);
		setBiomeTextureIfNone(Biomes.CRIMSON_FOREST, FOREST);
		setBiomeTextureIfNone(Biomes.WARPED_FOREST, JUNGLE);
		setBiomeTextureIfNone(Biomes.BASALT_DELTAS, MOUNTAINS_ALL);
		setBiomeTextureIfNone(Biomes.THE_END, END_VOID);

		setBiomeTextureIfNone(Biomes.MUSHROOM_FIELDS, MUSHROOM);
		setBiomeTextureIfNone(Biomes.MUSHROOM_FIELD_SHORE, SHORE);

		setBiomeTextureIfNone(Biomes.WOODED_BADLANDS_PLATEAU, PLATEAU_MESA_TREES);
		setBiomeTextureIfNone(Biomes.BADLANDS_PLATEAU, PLATEAU_MESA);
		setBiomeTextureIfNone(Biomes.ERODED_BADLANDS, MESA);
		setBiomeTextureIfNone(Biomes.BADLANDS, MESA);
		setBiomeTextureIfNone(Biomes.SAVANNA, SAVANNA);
		setBiomeTextureIfNone(Biomes.SAVANNA_PLATEAU, SAVANNA_PLATEAU);
		setBiomeTextureIfNone(Biomes.SHATTERED_SAVANNA, SAVANNA);
		setBiomeTextureIfNone(Biomes.SHATTERED_SAVANNA_PLATEAU, SAVANNA_PLATEAU);

		setBiomeTextureIfNone(Biomes.DEEP_FROZEN_OCEAN, ICE_SPIKES);

		// Now let's register every other biome, they'll come from other mods
		for (Biome biome : Registry.BIOME) {
			BiomeTextureMap.instance().checkRegistration(biome);
		}
	}

	/** Only applies the change if no texture is registered for this biome.
	 * This prevents overwriting of the config when there is no real change. */
	private void setBiomeTextureIfNone(Biome biome, TextureSet textureSet) {
		if(!textureMap.isRegistered(biome)) {
			textureMap.setTexture(biome, textureSet);
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
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_CROSSING, NETHER_BRIDGE);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_X, NETHER_BRIDGE_X);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_Z, NETHER_BRIDGE_Z);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_END_X, NETHER_BRIDGE_END_X);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_END_Z, NETHER_BRIDGE_END_Z);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, NETHER_BRIDGE_GATE);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_STAIRS, NETHER_TOWER);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_WALL, NETHER_WALL);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_EXIT, NETHER_HALL);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, NETHER_FORT_STAIRS);
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_PLATFORM, NETHER_THRONE);

		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_ISLAND, END_ISLAND);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_ISLAND_PLANTS, END_ISLAND_PLANTS);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_VOID, END_VOID);

		setCustomTileTextureIfNone(ExtTileIdMap.TILE_RAVINE, RAVINE);
	}
	/** Only applies the change if no texture is registered for this tile name.
	 * This prevents overwriting of the config when there is no real change. */
	private void setCustomTileTextureIfNone(Identifier tileId, TextureSet textureSet) {
		if (!textureMap.isRegistered(tileId)) {
			textureMap.setTexture(tileId, textureSet);
		}
	}
	
	@Override
	public Identifier getFabricId() {
		return AntiqueAtlasMod.id("proxy");
	}

	@Override
	public void apply(ResourceManager var1) {
		for (MarkerType type : MarkerType.REGISTRY) {
			type.initMips();
		}
	}
}
