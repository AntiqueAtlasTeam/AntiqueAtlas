package hunternif.mc.impl.atlas.ext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.TextureSetConfig;
import hunternif.mc.impl.atlas.client.TextureSetMap;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Client-only config mapping tile names to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class ExtTileTextureConfig implements SimpleResourceReloadListener<Map<String, String>> {
	private static final int VERSION = 1;
	private static final JsonParser PARSER = new JsonParser();
	private final TextureSetMap textureSetMap;
	private final ExtTileTextureMap extTileTextureMap;

	public ExtTileTextureConfig(ExtTileTextureMap extTileTextureMap, TextureSetMap textureSetMap) {
		this.extTileTextureMap = extTileTextureMap;
		this.textureSetMap = textureSetMap;
	}

	@Override
	public CompletableFuture<Map<String, String>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<String, String> map = new HashMap<>();

			try {
				for (Resource resource : manager.getAllResources(new Identifier("antiqueatlas:tile_textures.json"))) {
					try (InputStream stream = resource.getInputStream(); InputStreamReader reader = new InputStreamReader(stream)) {
						JsonElement element = PARSER.parse(reader);
						if (element.isJsonObject()) {
							JsonObject obj = element.getAsJsonObject();
							if (!obj.has("version")) {
								Log.warn("Invalid tile texture file found!");
							} else if (obj.get("version").getAsInt() < VERSION) {
								Log.warn("Outdated tile texture file version: " + obj.get("version").getAsInt());
							} else {
								for (Entry<String, JsonElement> entry : obj.get("data").getAsJsonObject().entrySet()) {
									map.put(entry.getKey(), entry.getValue().getAsString());
								}
							}
						} else {
							Log.warn("Invalid tile texture file found!");
						}
					} catch (Throwable e) {
						Log.warn(e, "Failed to read tile texture file!");
					}
				}
			} catch (Throwable e) {
				Log.warn(e, "Failed to read tile textures!");
			}

			return map;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<String, String> tileTexMap, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (Entry<String, String> entry : tileTexMap.entrySet()) {
				String tileName = entry.getKey();
				String textureName = entry.getValue();

				if (textureSetMap.isRegistered(textureName)) {
					extTileTextureMap.setTexture(AntiqueAtlasMod.id(tileName), textureSetMap.getByName(AntiqueAtlasMod.id(textureName)));
					Log.info("Registered texture set %s for tile \"%s\"", textureName, tileName);
				} else {
					Log.warn("Unknown texture set %s for tile \"%s\"", textureName, tileName);
				}
			}
		});
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("antiqueatlas:tile_textures");
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return Collections.singleton(new Identifier("antiqueatlas:texture_sets"));
	}
}
