package hunternif.mc.atlas;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.BiomeAPI;
import hunternif.mc.atlas.client.GuiAtlas;
import hunternif.mc.atlas.core.TextureConfig;

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