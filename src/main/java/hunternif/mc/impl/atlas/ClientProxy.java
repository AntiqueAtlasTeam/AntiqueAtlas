package hunternif.mc.impl.atlas;

import java.util.function.Predicate;

import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.client.BiomeTextureMap;
import hunternif.mc.impl.atlas.client.TextureConfig;
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
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements ISelectiveResourceReloadListener {
	public final static Map<ResourceLocation, ITexture> TEXTURE_MAP = new HashMap<>();
	private static TextureSetMap textureSetMap;
	private static BiomeTextureMap tileTextureMap;

	private static GuiAtlas guiAtlas;

	public void initClient() {
		//TODO Enforce texture config loading process as follows:
		// 1. pre-init: Antique Atlas defaults are loaded, config files are read.
		// 2. init: mods set their custom textures. Those loaded from the config must not be overwritten!
		IReloadableResourceManager resourceManager = Minecraft.getInstance().resourceManager;

		TextureConfig textureConfig = new TextureConfig(TEXTURE_MAP);

		resourceManager.addReloadListener(textureConfig);
		textureSetMap = TextureSetMap.instance();
		TextureSetConfig textureSetConfig = new TextureSetConfig(textureSetMap);
		// Legacy file name:
		resourceManager.addReloadListener(this);
		// init
		tileTextureMap = BiomeTextureMap.instance();
		MarkerTextureConfig markerTextureConfig = new MarkerTextureConfig();
		resourceManager.addReloadListener(markerTextureConfig);
		
//		ClientProxy.markerTextureConfig.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
		for (MarkerType type : MarkerType.REGISTRY) {
			type.initMips();
		}

	}

	/** Assign default textures to vanilla biomes. The textures are assigned
	 * only if the biome was not in the config. This prevents unnecessary
	 * overwriting, to aid people who manually modify the config. */
	private void assignVanillaBiomeTextures() {
		// Since biome categories are now vanilla, we only handle the
		// "edge cases".

		setBiomeTextureIfNone(Biomes.ICE_SPIKES, textureSetMap.getByName(AntiqueAtlasMod.id("ice_spikes")));
		setBiomeTextureIfNone(Biomes.SUNFLOWER_PLAINS, textureSetMap.getByName(AntiqueAtlasMod.id("sunflowers")));
		setBiomeTextureIfNone(Biomes.SNOWY_BEACH, textureSetMap.getByName(AntiqueAtlasMod.id("shore")));
		setBiomeTextureIfNone(Biomes.STONE_SHORE, textureSetMap.getByName(AntiqueAtlasMod.id("rock_shore")));

		setBiomeTextureIfNone(Biomes.SNOWY_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_snow_caps")));
		setBiomeTextureIfNone(Biomes.MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_all")));
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_snow_caps")));
		setBiomeTextureIfNone(Biomes.FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("forest")));

		setBiomeTextureIfNone(Biomes.FLOWER_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("forest_flowers")));
		setBiomeTextureIfNone(Biomes.BIRCH_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("birch")));
		setBiomeTextureIfNone(Biomes.TALL_BIRCH_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("tall_birch")));
		setBiomeTextureIfNone(Biomes.BIRCH_FOREST_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("birch_hills")));
		setBiomeTextureIfNone(Biomes.TALL_BIRCH_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("tall_birch_hills")));
		setBiomeTextureIfNone(Biomes.JUNGLE, textureSetMap.getByName(AntiqueAtlasMod.id("jungle")));
		setBiomeTextureIfNone(Biomes.MODIFIED_JUNGLE_EDGE, textureSetMap.getByName(AntiqueAtlasMod.id("jungle_cliffs")));
		setBiomeTextureIfNone(Biomes.JUNGLE_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("jungle_hills")));
		setBiomeTextureIfNone(Biomes.JUNGLE_EDGE, textureSetMap.getByName(AntiqueAtlasMod.id("jungle_edge")));
		setBiomeTextureIfNone(Biomes.TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("pines")));
		setBiomeTextureIfNone(Biomes.TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("pines_hills")));
		setBiomeTextureIfNone(Biomes.TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("pines_hills")));
		setBiomeTextureIfNone(Biomes.TAIGA_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("pines_hills")));
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("snow_pines")));
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("snow_pines_hills")));
		setBiomeTextureIfNone(Biomes.SNOWY_TAIGA_MOUNTAINS, textureSetMap.getByName(AntiqueAtlasMod.id("snow_pines_hills")));
		setBiomeTextureIfNone(Biomes.GIANT_TREE_TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("mega_taiga")));
		setBiomeTextureIfNone(Biomes.GIANT_SPRUCE_TAIGA, textureSetMap.getByName(AntiqueAtlasMod.id("mega_spruce")));
		setBiomeTextureIfNone(Biomes.GIANT_TREE_TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("mega_taiga_hills")));
		setBiomeTextureIfNone(Biomes.GIANT_SPRUCE_TAIGA_HILLS, textureSetMap.getByName(AntiqueAtlasMod.id("mega_spruce_hills")));

		setBiomeTextureIfNone(Biomes.NETHER_WASTES, textureSetMap.getByName(AntiqueAtlasMod.id("cave_walls")));
		setBiomeTextureIfNone(Biomes.SOUL_SAND_VALLEY, textureSetMap.getByName(AntiqueAtlasMod.id("soul_sand_valley")));
		setBiomeTextureIfNone(Biomes.CRIMSON_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("forest")));
		setBiomeTextureIfNone(Biomes.WARPED_FOREST, textureSetMap.getByName(AntiqueAtlasMod.id("jungle")));
		setBiomeTextureIfNone(Biomes.BASALT_DELTAS, textureSetMap.getByName(AntiqueAtlasMod.id("mountains_all")));

		setBiomeTextureIfNone(Biomes.THE_END, textureSetMap.getByName(AntiqueAtlasMod.id("end_island")));

		setBiomeTextureIfNone(Biomes.MUSHROOM_FIELDS, textureSetMap.getByName(AntiqueAtlasMod.id("mushroom")));
		setBiomeTextureIfNone(Biomes.MUSHROOM_FIELD_SHORE, textureSetMap.getByName(AntiqueAtlasMod.id("shore")));

		setBiomeTextureIfNone(Biomes.WOODED_BADLANDS_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("plateau_mesa_trees")));
		setBiomeTextureIfNone(Biomes.BADLANDS_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("plateau_mesa")));
		setBiomeTextureIfNone(Biomes.ERODED_BADLANDS, textureSetMap.getByName(AntiqueAtlasMod.id("mesa")));
		setBiomeTextureIfNone(Biomes.BADLANDS, textureSetMap.getByName(AntiqueAtlasMod.id("mesa")));
		setBiomeTextureIfNone(Biomes.SAVANNA, textureSetMap.getByName(AntiqueAtlasMod.id("savanna")));
		setBiomeTextureIfNone(Biomes.SAVANNA_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("savanna_cliffs")));
		setBiomeTextureIfNone(Biomes.SHATTERED_SAVANNA, textureSetMap.getByName(AntiqueAtlasMod.id("savanna")));
		setBiomeTextureIfNone(Biomes.SHATTERED_SAVANNA_PLATEAU, textureSetMap.getByName(AntiqueAtlasMod.id("savanna_cliffs")));

		setBiomeTextureIfNone(Biomes.DEEP_FROZEN_OCEAN, textureSetMap.getByName(AntiqueAtlasMod.id("ice_spikes")));

		// Now let's register every other biome, they'll come from other mods
		for (Biome biome : WorldGenRegistries.BIOME) {
			BiomeTextureMap.instance().checkRegistration(biome);
		}
	}

	/** Only applies the change if no texture is registered for this biome.
	 * This prevents overwriting of the config when there is no real change. */
	private void setBiomeTextureIfNone(RegistryKey<Biome> biome, TextureSet textureSet) {
		if(!tileTextureMap.isRegistered(biome.getLocation())) {
			tileTextureMap.setTexture(biome.getLocation(), textureSet);
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
	private void setCustomTileTextureIfNone(ResourceLocation tileId, TextureSet textureSet) {
		if (!tileTextureMap.isRegistered(tileId)) {
			tileTextureMap.setTexture(tileId, textureSet);
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
		registerVanillaCustomTileTextures();
		assignVanillaBiomeTextures();
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
