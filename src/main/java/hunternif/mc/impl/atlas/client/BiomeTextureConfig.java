package hunternif.mc.impl.atlas.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.util.Log;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class BiomeTextureConfig implements SimpleResourceReloadListener<Map<Identifier, String>> {
	private static final int VERSION = 2;
	private static final JsonParser PARSER = new JsonParser();
	private final BiomeTextureMap biomeTextureMap;
	private final TextureSetMap textureSetMap;

	public BiomeTextureConfig(BiomeTextureMap biomeTextureMap, TextureSetMap textureSetMap) {
		this.biomeTextureMap = biomeTextureMap;
		this.textureSetMap = textureSetMap;
	}

	@Override
	public CompletableFuture<Map<Identifier, String>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, String> map = new HashMap<>();

			try {
				for (Resource resource : manager.getAllResources(new Identifier("antiqueatlas:biome_textures.json"))) {
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
	public CompletableFuture<Void> apply(Map<Identifier, String> biomeTexMap, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (Entry<Identifier, String> entry : biomeTexMap.entrySet()) {
				Biome biome = Registry.BIOME.get(entry.getKey());
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

	@Override
	public Identifier getFabricId() {
		return new Identifier("antiqueatlas:biome_textures");
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return Collections.singleton(new Identifier("antiqueatlas:texture_sets"));
	}
}
