package hunternif.mc.atlas.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.Log;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class BiomeTextureConfig implements ISelectiveResourceReloadListener {
	private static final int VERSION = 2;
	private static final JsonParser PARSER = new JsonParser();
	private final BiomeTextureMap biomeTextureMap;
	private final TextureSetMap textureSetMap;

	public BiomeTextureConfig(BiomeTextureMap biomeTextureMap, TextureSetMap textureSetMap) {
		this.biomeTextureMap = biomeTextureMap;
		this.textureSetMap = textureSetMap;
	}
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		Map<ResourceLocation, String> map = new HashMap<>();

		try {
			for (IResource resource : resourceManager.getAllResources(new ResourceLocation("antiqueatlas:biome_textures.json"))) {
				try (InputStream stream = resource.getInputStream(); InputStreamReader reader = new InputStreamReader(stream)) {
					JsonElement element = PARSER.parse(reader);
					if (element.isJsonObject()) {
						JsonObject obj = element.getAsJsonObject();
						if (!obj.has("version")) {
							Log.warn("Invalid biome texture file found!");
						} else if (obj.get("version").getAsInt() < VERSION) {
							Log.warn("Outdated biome texture file version: " + obj.get("version").getAsInt());
						} else {
							for (Entry<String, JsonElement> entry : obj.get("data").getAsJsonObject().entrySet()) {
								map.put(AntiqueAtlasMod.id(entry.getKey()), entry.getValue().getAsString());
							}
						}
					} else {
						Log.warn("Invalid biome texture file found!");
					}
				} catch (Throwable e) {
					Log.warn(e, "Failed to read biome texture file!");
				}
			}
		} catch (Throwable e) {
			Log.warn(e, "Failed to read biome textures!");
		}

		for (Entry<ResourceLocation, String> entry : map.entrySet()) {
			Biome biome = ForgeRegistries.BIOMES.getValue(entry.getKey());
			if (biome == null) {
				Log.warn("Unknown biome in texture map: %s", entry.getKey());
				continue;
			}

			String name = entry.getValue();
			if (textureSetMap.isRegistered(name)) {
				biomeTextureMap.setTexture(biome, textureSetMap.getByName(AntiqueAtlasMod.id(name)));
				Log.info("Registered texture set %s for biome %s", name, entry.getKey());
			} else {
				Log.warn("Unknown texture set %s for biome %s", name, entry.getKey());
			}
		}
	}
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		if (resourcePredicate.test(getResourceType())) {
			onResourceManagerReload(resourceManager);
		}
	}

	@Nullable
	@Override
	public IResourceType getResourceType() {
		return VanillaResourceType.TEXTURES;
	}

}
