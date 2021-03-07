package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.client.*;
import hunternif.mc.impl.atlas.client.texture.ITexture;
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
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientProxy implements SimpleSynchronousResourceReloadListener {
	public final static Map<Identifier, ITexture> TEXTURE_MAP  = new HashMap<>();
	private static TextureSetMap textureSetMap;
	private static BiomeTextureMap tileTextureMap;

	public void initClient() {
		//TODO Enforce texture config loading process as follows:
		// 1. pre-init: Antique Atlas defaults are loaded, config files are read.
		// 2. init: mods set their custom textures. Those loaded from the config must not be overwritten!

		TextureConfig textureConfig = new TextureConfig(TEXTURE_MAP);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(textureConfig);

		textureSetMap = TextureSetMap.instance();
		TextureSetConfig textureSetConfig = new TextureSetConfig(textureSetMap);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(textureSetConfig);

		// Legacy file name:
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this);

		// init
		tileTextureMap = BiomeTextureMap.instance();

		MarkerTextureConfig markerTextureConfig = new MarkerTextureConfig();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(markerTextureConfig);

		for (MarkerType type : MarkerType.REGISTRY) {
			type.initMips();
		}

		if (!AntiqueAtlasMod.CONFIG.itemNeeded) {
            KeyHandler.registerBindings();
			ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::onClientTick);
        }

	}

	/** Assign default textures to vanilla biomes. The textures are assigned
	 * only if the biome was not in the config. This prevents unnecessary
	 * overwriting, to aid people who manually modify the config. */
	private void assignVanillaBiomeTextures() {
		// Since biome categories are now vanilla, we only handle the
		// "edge cases".

		setBiomeTextureIfNone(BiomeKeys.ICE_SPIKES, textureSetMap.getByName(AntiqueAtlasMod.id("ice_spikes")));
		setBiomeTextureIfNone(BiomeKeys.SUNFLOWER_PLAINS, textureSetMap.getByName(AntiqueAtlasMod.id("sunflowers")));
		setBiomeTextureIfNone(BiomeKeys.SNOWY_BEACH, textureSetMap.getByName(AntiqueAtlasMod.id("shore")));
		setBiomeTextureIfNone(BiomeKeys.STONE_SHORE, textureSetMap.getByName(AntiqueAtlasMod.id("rock_shore")));

		setBiomeTextureIfNone(BiomeKeys.SNOWY_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_snow_caps")));
		setBiomeTextureIfNone(BiomeKeys.MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_all")));
		setBiomeTextureIfNone(BiomeKeys.SNOWY_TAIGA_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_snow_caps")));
		setBiomeTextureIfNone(BiomeKeys.FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("forest")));

		setBiomeTextureIfNone(BiomeKeys.FLOWER_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("forest_flowers")));
		setBiomeTextureIfNone(BiomeKeys.BIRCH_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("birch")));
		setBiomeTextureIfNone(BiomeKeys.TALL_BIRCH_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("tall_birch")));
		setBiomeTextureIfNone(BiomeKeys.BIRCH_FOREST_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("birch_hills")));
		setBiomeTextureIfNone(BiomeKeys.TALL_BIRCH_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("tall_birch_hills")));
		setBiomeTextureIfNone(BiomeKeys.JUNGLE, textureSetMap.getByName(AntiqueAtlasMod.id("jungle")));
		setBiomeTextureIfNone(BiomeKeys.MODIFIED_JUNGLE_EDGE, textureSetMap.getByName(AntiqueAtlasMod.id("jungle_cliffs")));
		setBiomeTextureIfNone(BiomeKeys.JUNGLE_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("jungle_hills")));
		setBiomeTextureIfNone(BiomeKeys.JUNGLE_EDGE, textureSetMap.getByName(AntiqueAtlasMod.id("jungle_edge")));
		setBiomeTextureIfNone(BiomeKeys.TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("pines")));
		setBiomeTextureIfNone(BiomeKeys.TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("pines_hills")));
		setBiomeTextureIfNone(BiomeKeys.TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("pines_hills")));
		setBiomeTextureIfNone(BiomeKeys.TAIGA_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("pines_hills")));
		setBiomeTextureIfNone(BiomeKeys.SNOWY_TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("snow_pines")));
		setBiomeTextureIfNone(BiomeKeys.SNOWY_TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("snow_pines_hills")));
		setBiomeTextureIfNone(BiomeKeys.SNOWY_TAIGA_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("snow_pines_hills")));
		setBiomeTextureIfNone(BiomeKeys.GIANT_TREE_TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("mega_taiga")));
		setBiomeTextureIfNone(BiomeKeys.GIANT_SPRUCE_TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("mega_spruce")));
		setBiomeTextureIfNone(BiomeKeys.GIANT_TREE_TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("mega_taiga_hills")));
		setBiomeTextureIfNone(BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("mega_spruce_hills")));

		setBiomeTextureIfNone(BiomeKeys.NETHER_WASTES, textureSetMap.getByName(AntiqueAtlasMod.id("cave_walls")));
		setBiomeTextureIfNone(BiomeKeys.SOUL_SAND_VALLEY, textureSetMap.getByName(AntiqueAtlasMod.id("soul_sand_valley")));
		setBiomeTextureIfNone(BiomeKeys.CRIMSON_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("forest")));
		setBiomeTextureIfNone(BiomeKeys.WARPED_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("jungle")));
		setBiomeTextureIfNone(BiomeKeys.BASALT_DELTAS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_all")));

		setBiomeTextureIfNone(BiomeKeys.THE_END, textureSetMap.getByName(AntiqueAtlasMod.id("end_island")));

		setBiomeTextureIfNone(BiomeKeys.MUSHROOM_FIELDS, textureSetMap.getByName(AntiqueAtlasMod.id("mushroom")));
		setBiomeTextureIfNone(BiomeKeys.MUSHROOM_FIELD_SHORE, textureSetMap.getByName(AntiqueAtlasMod.id("shore")));

		setBiomeTextureIfNone(BiomeKeys.WOODED_BADLANDS_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("plateau_mesa_trees")));
		setBiomeTextureIfNone(BiomeKeys.BADLANDS_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("plateau_mesa")));
		setBiomeTextureIfNone(BiomeKeys.ERODED_BADLANDS, textureSetMap.getByName(AntiqueAtlasMod.id("mesa")));
		setBiomeTextureIfNone(BiomeKeys.BADLANDS, textureSetMap.getByName(AntiqueAtlasMod.id("mesa")));
		setBiomeTextureIfNone(BiomeKeys.SAVANNA, textureSetMap.getByName(AntiqueAtlasMod.id("savanna")));
		setBiomeTextureIfNone(BiomeKeys.SAVANNA_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("savanna_cliffs")));
		setBiomeTextureIfNone(BiomeKeys.SHATTERED_SAVANNA, textureSetMap.getByName(AntiqueAtlasMod.id("savanna")));
		setBiomeTextureIfNone(BiomeKeys.SHATTERED_SAVANNA_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("savanna_cliffs")));

		setBiomeTextureIfNone(BiomeKeys.DEEP_FROZEN_OCEAN, textureSetMap.getByName(AntiqueAtlasMod.id("ice_spikes")));

		// Now let's register every other biome, they'll come from other mods
		for (Biome biome : BuiltinRegistries.BIOME) {
			BiomeTextureMap.instance().checkRegistration(biome);
		}
	}

	/** Only applies the change if no texture is registered for this biome.
	 * This prevents overwriting of the config when there is no real change. */
	private void setBiomeTextureIfNone(RegistryKey<Biome> biome, TextureSet textureSet) {
		if(!tileTextureMap.isRegistered(biome.getValue())) {
			tileTextureMap.setTexture(biome.getValue(), textureSet);
		}
	}

	/** Assign default textures to the pseudo-biomes used for vanilla Minecraft.
	 * The pseudo-biomes are: villages houses, village territory and lava. */
	private void registerVanillaCustomTileTextures() {
		// Village:
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_LIBRARY, textureSetMap.getByName(AntiqueAtlasMod.id("library")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_SMITHY, textureSetMap.getByName(AntiqueAtlasMod.id("smithy")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_L_HOUSE, textureSetMap.getByName(AntiqueAtlasMod.id("l_house")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_FARMLAND_LARGE, textureSetMap.getByName(AntiqueAtlasMod.id("farmland_large")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_FARMLAND_SMALL, textureSetMap.getByName(AntiqueAtlasMod.id("farmland_small")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_WELL, textureSetMap.getByName(AntiqueAtlasMod.id("well")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_TORCH, textureSetMap.getByName(AntiqueAtlasMod.id("village_torch")));
//		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_PATH_X, VILLAGE_PATH_X);
//		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_PATH_Z, VILLAGE_PATH_Z);
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_HUT, textureSetMap.getByName(AntiqueAtlasMod.id("hut")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_SMALL_HOUSE, textureSetMap.getByName(AntiqueAtlasMod.id("house_small")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_BUTCHERS_SHOP, textureSetMap.getByName(AntiqueAtlasMod.id("butchers_shop")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_VILLAGE_CHURCH, textureSetMap.getByName(AntiqueAtlasMod.id("church")));

		// Nether & Nether Fortress:
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_LAVA, textureSetMap.getByName(AntiqueAtlasMod.id("lava")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_LAVA_SHORE, textureSetMap.getByName(AntiqueAtlasMod.id("lava_shore")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_CROSSING, textureSetMap.getByName(AntiqueAtlasMod.id("nether_bridge")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_X, textureSetMap.getByName(AntiqueAtlasMod.id("nether_bridge_x")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_Z, textureSetMap.getByName(AntiqueAtlasMod.id("nether_bridge_z")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_END_X, textureSetMap.getByName(AntiqueAtlasMod.id("nether_bridge_end_x")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_BRIDGE_END_Z, textureSetMap.getByName(AntiqueAtlasMod.id("nether_bridge_end_z")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, textureSetMap.getByName(AntiqueAtlasMod.id("nether_bridge_gate")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_STAIRS, textureSetMap.getByName(AntiqueAtlasMod.id("nether_tower")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_WALL, textureSetMap.getByName(AntiqueAtlasMod.id("nether_wall")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_EXIT, textureSetMap.getByName(AntiqueAtlasMod.id("nether_hall")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, textureSetMap.getByName(AntiqueAtlasMod.id("nether_fort_stairs")));
		setCustomTileTextureIfNone(ExtTileIdMap.NETHER_FORTRESS_BRIDGE_PLATFORM, textureSetMap.getByName(AntiqueAtlasMod.id("nether_throne")));

		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_ISLAND, textureSetMap.getByName(AntiqueAtlasMod.id("end_island")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_ISLAND_PLANTS, textureSetMap.getByName(AntiqueAtlasMod.id("end_island_plants")));
		setCustomTileTextureIfNone(ExtTileIdMap.TILE_END_VOID, textureSetMap.getByName(AntiqueAtlasMod.id("end_void")));

		setCustomTileTextureIfNone(ExtTileIdMap.TILE_RAVINE, textureSetMap.getByName(AntiqueAtlasMod.id("ravine")));
		setCustomTileTextureIfNone(ExtTileIdMap.SWAMP_WATER, textureSetMap.getByName(AntiqueAtlasMod.id("swamp_water")));
	}
	/** Only applies the change if no texture is registered for this tile name.
	 * This prevents overwriting of the config when there is no real change. */
	private void setCustomTileTextureIfNone(Identifier tileId, TextureSet textureSet) {
		if (!tileTextureMap.isRegistered(tileId)) {
			tileTextureMap.setTexture(tileId, textureSet);
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
		registerVanillaCustomTileTextures();
		assignVanillaBiomeTextures();
	}
}
