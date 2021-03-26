package hunternif.mc.impl.atlas;

import java.util.function.Predicate;

import hunternif.mc.impl.atlas.client.TextureConfig;
import hunternif.mc.impl.atlas.client.TextureSetConfig;
import hunternif.mc.impl.atlas.client.TextureSetMap;
import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.TileTextureConfig;
import hunternif.mc.impl.atlas.client.TileTextureMap;
import hunternif.mc.impl.atlas.marker.MarkerTextureConfig;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements ISelectiveResourceReloadListener {

	public void initClient() {
		IReloadableResourceManager resourceManager = Minecraft.getInstance().resourceManager;
		// read Textures first from assets
		TextureConfig textureConfig = new TextureConfig(Textures.TILE_TEXTURES_MAP);
		resourceManager.addReloadListener(textureConfig);
		// than read TextureSets
		TextureSetMap textureSetMap = TextureSetMap.instance();
		TextureSetConfig textureSetConfig = new TextureSetConfig(textureSetMap);
		resourceManager.addReloadListener(textureSetConfig);
		// After that, we can read the tile mappings
		TileTextureMap tileTextureMap = TileTextureMap.instance();
		TileTextureConfig tileTextureConfig = new TileTextureConfig(tileTextureMap, textureSetMap);
		resourceManager.addReloadListener(tileTextureConfig);
		// Legacy file name:
		resourceManager.addReloadListener(this);
		// init
		tileTextureMap = TileTextureMap.instance();
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
	private void assignBiomeTextures() {
		// Now let's register every other biome, they'll come from other mods
		for (Biome biome : WorldGenRegistries.BIOME) {
			TileTextureMap.instance().checkRegistration(biome);
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
		assignBiomeTextures();
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
