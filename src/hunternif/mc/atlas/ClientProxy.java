package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.StandardTextureSet.BEACH;
import static hunternif.mc.atlas.client.StandardTextureSet.FOREST;
import static hunternif.mc.atlas.client.StandardTextureSet.FOREST_HILLS;
import static hunternif.mc.atlas.client.StandardTextureSet.FROZEN_WATER;
import static hunternif.mc.atlas.client.StandardTextureSet.HILLS;
import static hunternif.mc.atlas.client.StandardTextureSet.JUNGLE;
import static hunternif.mc.atlas.client.StandardTextureSet.JUNGLE_HILLS;
import static hunternif.mc.atlas.client.StandardTextureSet.MOUNTAINS;
import static hunternif.mc.atlas.client.StandardTextureSet.MUSHROOM;
import static hunternif.mc.atlas.client.StandardTextureSet.PINES;
import static hunternif.mc.atlas.client.StandardTextureSet.PINES_HILLS;
import static hunternif.mc.atlas.client.StandardTextureSet.PLAINS;
import static hunternif.mc.atlas.client.StandardTextureSet.SAND;
import static hunternif.mc.atlas.client.StandardTextureSet.SNOW;
import static hunternif.mc.atlas.client.StandardTextureSet.SWAMP;
import static hunternif.mc.atlas.client.StandardTextureSet.WATER;
import static net.minecraft.world.biome.BiomeGenBase.beach;
import static net.minecraft.world.biome.BiomeGenBase.desert;
import static net.minecraft.world.biome.BiomeGenBase.desertHills;
import static net.minecraft.world.biome.BiomeGenBase.extremeHills;
import static net.minecraft.world.biome.BiomeGenBase.extremeHillsEdge;
import static net.minecraft.world.biome.BiomeGenBase.forest;
import static net.minecraft.world.biome.BiomeGenBase.forestHills;
import static net.minecraft.world.biome.BiomeGenBase.frozenOcean;
import static net.minecraft.world.biome.BiomeGenBase.frozenRiver;
import static net.minecraft.world.biome.BiomeGenBase.iceMountains;
import static net.minecraft.world.biome.BiomeGenBase.icePlains;
import static net.minecraft.world.biome.BiomeGenBase.jungle;
import static net.minecraft.world.biome.BiomeGenBase.jungleHills;
import static net.minecraft.world.biome.BiomeGenBase.mushroomIsland;
import static net.minecraft.world.biome.BiomeGenBase.mushroomIslandShore;
import static net.minecraft.world.biome.BiomeGenBase.ocean;
import static net.minecraft.world.biome.BiomeGenBase.plains;
import static net.minecraft.world.biome.BiomeGenBase.river;
import static net.minecraft.world.biome.BiomeGenBase.sky;
import static net.minecraft.world.biome.BiomeGenBase.swampland;
import static net.minecraft.world.biome.BiomeGenBase.taiga;
import static net.minecraft.world.biome.BiomeGenBase.taigaHills;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.client.GuiAtlas;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.core.TextureConfig;
import hunternif.mc.atlas.ext.ExtTileIdMap;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	private TextureConfig textureConfig;
	
	private GuiAtlas guiAtlas;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		textureConfig = new TextureConfig(new File(configDir, "textures.json"));
		textureConfig.load();
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		guiAtlas = new GuiAtlas();
		AtlasAPI.getTileAPI().setTextureIfNone(ExtTileIdMap.TILE_VILLAGE_HOUSE, StandardTextureSet.HOUSE);
		AtlasAPI.getTileAPI().setTextureIfNone(ExtTileIdMap.TILE_VILLAGE_TERRITORY, StandardTextureSet.FENCE);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		if (assignVanillaTextures()) {
			// Only rewrite config, if new textures were automatically assigned.
			updateTextureConfig();
		}
	}
	
	@Override
	public void updateTextureConfig() {
		textureConfig.save();
	}
	
	@Override
	public void openAtlasGUI(ItemStack stack) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) { // In-game screen
			mc.displayGuiScreen(guiAtlas.setAtlasItemStack(stack));
		}
	}
	
	/** Assign default textures to vanilla biomes. Returns true if any texture
	 * was changed. */
	private boolean assignVanillaTextures() {
		boolean changed = false;
		BiomeAPI api = AtlasAPI.getBiomeAPI();
		changed |= api.setTextureIfNone(ocean,			WATER);
		changed |= api.setTextureIfNone(river,			WATER);
		changed |= api.setTextureIfNone(frozenOcean,	FROZEN_WATER);
		changed |= api.setTextureIfNone(frozenRiver,	FROZEN_WATER);
		changed |= api.setTextureIfNone(beach,			BEACH);
		changed |= api.setTextureIfNone(desert,		SAND);
		changed |= api.setTextureIfNone(plains,		PLAINS);
		changed |= api.setTextureIfNone(icePlains,	SNOW);
		changed |= api.setTextureIfNone(jungleHills,	JUNGLE_HILLS);
		changed |= api.setTextureIfNone(forestHills,	FOREST_HILLS);
		changed |= api.setTextureIfNone(desertHills,	HILLS);
		changed |= api.setTextureIfNone(extremeHills,	MOUNTAINS);
		changed |= api.setTextureIfNone(extremeHillsEdge, MOUNTAINS);
		changed |= api.setTextureIfNone(iceMountains,	MOUNTAINS);
		changed |= api.setTextureIfNone(forest,		FOREST);
		changed |= api.setTextureIfNone(jungle,		JUNGLE);
		changed |= api.setTextureIfNone(taiga,			PINES);
		changed |= api.setTextureIfNone(taigaHills,	PINES_HILLS);
		changed |= api.setTextureIfNone(swampland,		SWAMP);
		changed |= api.setTextureIfNone(sky,			BEACH);
		//changed |= api.addTextureIfNone(hell,		NETHER);
		changed |= api.setTextureIfNone(mushroomIsland, MUSHROOM);
		changed |= api.setTextureIfNone(mushroomIslandShore, BEACH);
		
		return changed;
	}
}