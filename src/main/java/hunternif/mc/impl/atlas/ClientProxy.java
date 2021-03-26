package hunternif.mc.impl.atlas;

import static hunternif.mc.impl.atlas.client.TextureSet.BIRCH;
import static hunternif.mc.impl.atlas.client.TextureSet.BIRCH_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.BRYCE;
import static hunternif.mc.impl.atlas.client.TextureSet.BUTCHERS_SHOP;
import static hunternif.mc.impl.atlas.client.TextureSet.CAVE_WALLS;
import static hunternif.mc.impl.atlas.client.TextureSet.CHURCH;
import static hunternif.mc.impl.atlas.client.TextureSet.DENSE_BIRCH;
import static hunternif.mc.impl.atlas.client.TextureSet.DENSE_FOREST;
import static hunternif.mc.impl.atlas.client.TextureSet.DENSE_FOREST_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.DESERT;
import static hunternif.mc.impl.atlas.client.TextureSet.DESERT_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.END_ISLAND;
import static hunternif.mc.impl.atlas.client.TextureSet.END_ISLAND_PLANTS;
import static hunternif.mc.impl.atlas.client.TextureSet.END_VOID;
import static hunternif.mc.impl.atlas.client.TextureSet.FARMLAND_LARGE;
import static hunternif.mc.impl.atlas.client.TextureSet.FARMLAND_SMALL;
import static hunternif.mc.impl.atlas.client.TextureSet.FENCE;
import static hunternif.mc.impl.atlas.client.TextureSet.FOREST;
import static hunternif.mc.impl.atlas.client.TextureSet.FOREST_FLOWERS;
import static hunternif.mc.impl.atlas.client.TextureSet.FOREST_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.HOUSE;
import static hunternif.mc.impl.atlas.client.TextureSet.HOUSE_SMALL;
import static hunternif.mc.impl.atlas.client.TextureSet.HUT;
import static hunternif.mc.impl.atlas.client.TextureSet.ICE;
import static hunternif.mc.impl.atlas.client.TextureSet.ICE_SPIKES;
import static hunternif.mc.impl.atlas.client.TextureSet.JUNGLE;
import static hunternif.mc.impl.atlas.client.TextureSet.JUNGLE_CLIFFS;
import static hunternif.mc.impl.atlas.client.TextureSet.JUNGLE_EDGE;
import static hunternif.mc.impl.atlas.client.TextureSet.JUNGLE_EDGE_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.JUNGLE_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.LAVA;
import static hunternif.mc.impl.atlas.client.TextureSet.LAVA_SHORE;
import static hunternif.mc.impl.atlas.client.TextureSet.LIBRARY;
import static hunternif.mc.impl.atlas.client.TextureSet.L_HOUSE;
import static hunternif.mc.impl.atlas.client.TextureSet.MEGA_SPRUCE;
import static hunternif.mc.impl.atlas.client.TextureSet.MEGA_SPRUCE_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.MEGA_TAIGA;
import static hunternif.mc.impl.atlas.client.TextureSet.MEGA_TAIGA_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.MESA;
import static hunternif.mc.impl.atlas.client.TextureSet.MOUNTAINS;
import static hunternif.mc.impl.atlas.client.TextureSet.MOUNTAINS_ALL;
import static hunternif.mc.impl.atlas.client.TextureSet.MOUNTAINS_NAKED;
import static hunternif.mc.impl.atlas.client.TextureSet.MOUNTAINS_SNOW_CAPS;
import static hunternif.mc.impl.atlas.client.TextureSet.MUSHROOM;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_BRIDGE;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_BRIDGE_END_X;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_BRIDGE_END_Z;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_BRIDGE_GATE;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_BRIDGE_X;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_BRIDGE_Z;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_FORT_STAIRS;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_HALL;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_THRONE;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_TOWER;
import static hunternif.mc.impl.atlas.client.TextureSet.NETHER_WALL;
import static hunternif.mc.impl.atlas.client.TextureSet.PINES;
import static hunternif.mc.impl.atlas.client.TextureSet.PINES_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.PLAINS;
import static hunternif.mc.impl.atlas.client.TextureSet.PLATEAU_MESA;
import static hunternif.mc.impl.atlas.client.TextureSet.PLATEAU_MESA_LOW;
import static hunternif.mc.impl.atlas.client.TextureSet.PLATEAU_MESA_TREES;
import static hunternif.mc.impl.atlas.client.TextureSet.PLATEAU_MESA_TREES_LOW;
import static hunternif.mc.impl.atlas.client.TextureSet.PLATEAU_SAVANNA;
import static hunternif.mc.impl.atlas.client.TextureSet.PLATEAU_SAVANNA_M;
import static hunternif.mc.impl.atlas.client.TextureSet.RAVINE;
import static hunternif.mc.impl.atlas.client.TextureSet.ROCK_SHORE;
import static hunternif.mc.impl.atlas.client.TextureSet.SAVANNA;
import static hunternif.mc.impl.atlas.client.TextureSet.SAVANNA_CLIFFS;
import static hunternif.mc.impl.atlas.client.TextureSet.SHORE;
import static hunternif.mc.impl.atlas.client.TextureSet.SMITHY;
import static hunternif.mc.impl.atlas.client.TextureSet.SNOW;
import static hunternif.mc.impl.atlas.client.TextureSet.SNOW_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.SNOW_PINES;
import static hunternif.mc.impl.atlas.client.TextureSet.SNOW_PINES_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.SOUL_SAND_VALLEY;
import static hunternif.mc.impl.atlas.client.TextureSet.SUNFLOWERS;
import static hunternif.mc.impl.atlas.client.TextureSet.SWAMP;
import static hunternif.mc.impl.atlas.client.TextureSet.SWAMP_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.TALL_BIRCH;
import static hunternif.mc.impl.atlas.client.TextureSet.TALL_BIRCH_HILLS;
import static hunternif.mc.impl.atlas.client.TextureSet.VILLAGE_TORCH;
import static hunternif.mc.impl.atlas.client.TextureSet.WATER;
import static hunternif.mc.impl.atlas.client.TextureSet.WELL;

import java.util.function.Predicate;

import hunternif.mc.impl.atlas.client.BiomeTextureMap;
import hunternif.mc.impl.atlas.client.TextureSet;
import hunternif.mc.impl.atlas.client.TextureSetConfig;
import hunternif.mc.impl.atlas.client.TextureSetMap;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements ISelectiveResourceReloadListener {
	private static TextureSetMap textureSetMap;
	private static TextureSetConfig textureSetConfig;
	private static BiomeTextureMap textureMap;
	public static MarkerTextureConfig markerTextureConfig;

	private static GuiAtlas guiAtlas;

	public void initClient() {
		//TODO Enforce texture config loading process as follows:
		// 1. pre-init: Antique Atlas defaults are loaded, config files are read.
		// 2. init: mods set their custom textures. Those loaded from the config must not be overwritten!
		IReloadableResourceManager resourceManager = Minecraft.getInstance().resourceManager;

		textureSetMap = TextureSetMap.instance();
		textureSetConfig = new TextureSetConfig(textureSetMap);
		// Register default values before the config file loads, possibly overwriting the,:
		registerDefaultTextureSets(textureSetMap);
		// Prevent rewriting of the config while no changes have been made:
		textureSetMap.setDirty(false);

		resourceManager.addReloadListener(textureSetConfig);
		// Legacy file name:
		resourceManager.addReloadListener(this);
		// init
		textureMap = BiomeTextureMap.instance();
		registerVanillaCustomTileTextures();
		// Prevent rewriting of the config while no changes have been made:
		textureMap.setDirty(false);
		assignVanillaBiomeTextures();

		markerTextureConfig = new MarkerTextureConfig();
		resourceManager.addReloadListener(markerTextureConfig);
		
//		ClientProxy.markerTextureConfig.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
		
		// Prevent rewriting of the config while no changes have been made:
//		MarkerType.REGISTRY.setDirty(true);
		for (MarkerType type : MarkerType.REGISTRY) {
			type.initMips();
		}

	}

	@OnlyIn(Dist.CLIENT)
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

		map.register(SOUL_SAND_VALLEY);

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
		setBiomeTextureIfNone(Biomes.TAIGA_MOUNTAINS, PINES_HILLS);
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA, SNOW_PINES);
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_HILLS, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_MOUNTAINS, SNOW_PINES_HILLS);
		setBiomeTextureIfNone(Biomes.GIANT_TREE_TAIGA, MEGA_TAIGA);
		setBiomeTextureIfNone(Biomes.GIANT_SPRUCE_TAIGA, MEGA_SPRUCE);
		setBiomeTextureIfNone(Biomes.GIANT_TREE_TAIGA_HILLS, MEGA_TAIGA_HILLS);
		setBiomeTextureIfNone(Biomes.GIANT_SPRUCE_TAIGA_HILLS, MEGA_SPRUCE_HILLS);

		setBiomeTextureIfNone(Biomes.NETHER_WASTES, CAVE_WALLS);
		setBiomeTextureIfNone(Biomes.SOUL_SAND_VALLEY, SOUL_SAND_VALLEY);
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
		setBiomeTextureIfNone(Biomes.SAVANNA_PLATEAU, SAVANNA_CLIFFS);
		setBiomeTextureIfNone(Biomes.SHATTERED_SAVANNA, SAVANNA);
		setBiomeTextureIfNone(Biomes.SHATTERED_SAVANNA_PLATEAU, SAVANNA_CLIFFS);

		setBiomeTextureIfNone(Biomes.DEEP_FROZEN_OCEAN, ICE_SPIKES);

		// Now let's register every other biome, they'll come from other mods
		for (Biome biome : WorldGenRegistries.BIOME) {
			BiomeTextureMap.instance().checkRegistration(biome);
		}
	}

	/** Only applies the change if no texture is registered for this biome.
	 * This prevents overwriting of the config when there is no real change. */
	private void setBiomeTextureIfNone(RegistryKey<Biome> biome, TextureSet textureSet) {
		if(!textureMap.isRegistered(biome.getLocation())) {
			textureMap.setTexture(biome.getLocation(), textureSet);
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
	private void setCustomTileTextureIfNone(ResourceLocation tileId, TextureSet textureSet) {
		if (!textureMap.isRegistered(tileId)) {
			textureMap.setTexture(tileId, textureSet);
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		onResourceManagerReload(resourceManager);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		for (MarkerType type : MarkerType.REGISTRY) {
			type.initMips();
		}
	}
	
//	@Override
//	public ResourceLocation getFabricId() {
//		return AntiqueAtlasMod.id("proxy");
//	}

//	@Override
//	public void apply(IResourceManager var1) {
//		for (MarkerType type : MarkerType.REGISTRY) {
//			type.initMips();
//		}
//	}
}
