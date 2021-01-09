package hunternif.mc.impl.atlas.client;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.forge.IResourceReloadListener;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class BiomeTextureConfig implements IResourceReloadListener<Map<ResourceLocation, String>> {
	private static final int VERSION = 2;
	private static final JsonParser PARSER = new JsonParser();
	private final BiomeTextureMap biomeTextureMap;
	private final TextureSetMap textureSetMap;

	public BiomeTextureConfig(BiomeTextureMap biomeTextureMap, TextureSetMap textureSetMap) {
		this.biomeTextureMap = biomeTextureMap;
		this.textureSetMap = textureSetMap;
	}

	@Override
	public CompletableFuture<Map<ResourceLocation, String>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, String> map = new HashMap<>();

			try {
				for (IResource resource : manager.getAllResources(new ResourceLocation("antiqueatlas:biome_textures.json"))) {
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

			return map;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, String> biomeTexMap, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (Entry<ResourceLocation, String> entry : biomeTexMap.entrySet()) {
				Biome biome = WorldGenRegistries.BIOME.getOrDefault(entry.getKey());
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
		});
	}

//	@Override
//	public ResourceLocation getFabricId() {
//		return new ResourceLocation("antiqueatlas:biome_textures");
//	}
//
//	@Override
//	public Collection<ResourceLocation> getFabricDependencies() {
//		return Collections.singleton(new ResourceLocation("antiqueatlas:texture_sets"));
//	}
}
