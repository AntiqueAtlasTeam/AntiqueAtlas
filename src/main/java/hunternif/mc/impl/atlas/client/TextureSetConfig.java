package hunternif.mc.impl.atlas.client;

import com.google.gson.*;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

/**
 * Saves texture set names with the lists of texture variations.
 */
@Environment(EnvType.CLIENT)
public class TextureSetConfig implements SimpleResourceReloadListener<Collection<TextureSet>> {
	private static final int VERSION = 1;
	private static final JsonParser PARSER = new JsonParser();
	private final TextureSetMap textureSetMap;

	public TextureSetConfig(TextureSetMap textureSetMap) {
		this.textureSetMap = textureSetMap;
	}

	@Override
	public CompletableFuture<Collection<TextureSet>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			List<TextureSet> sets = new ArrayList<>();

			try {
				for (Resource resource : manager.getAllResources(new Identifier("antiqueatlas:texture_sets.json"))) {
					try (InputStream stream = resource.getInputStream(); InputStreamReader reader = new InputStreamReader(stream)) {
						JsonElement element = PARSER.parse(reader);
						if (element.isJsonObject()) {
							JsonObject obj = element.getAsJsonObject();
							if (!obj.has("version")) {
								Log.warn("Invalid texture set file found!");
							} else if (obj.get("version").getAsInt() < VERSION) {
								Log.warn("Outdated texture set file version: " + obj.get("version").getAsInt());
							} else {
								for (Entry<String, JsonElement> entry : obj.get("data").getAsJsonObject().entrySet()) {
									String name = entry.getKey();
									JsonArray array = entry.getValue().getAsJsonArray();
									Identifier[] textures = new Identifier[array.size()];
									for (int i = 0; i < array.size(); i++) {
										String path = array.get(i).getAsString();
										textures[i] = new Identifier(path);
									}
									sets.add(new TextureSet(AntiqueAtlasMod.id(name), textures));
								}
							}
						} else {
							Log.warn("Invalid texture set file found!");
						}
					} catch (Throwable e) {
						Log.warn(e, "Failed to read texture set file!");
					}
				}
			} catch (Throwable e) {
				Log.warn(e, "Failed to read texture sets!");
			}

			return sets;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Collection<TextureSet> sets, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (TextureSet set : sets) {
				textureSetMap.register(set);
				Log.info("Loaded texture set %s with %d custom texture(s)", set.name, set.textures.length);
			}
		});
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("antiqueatlas:texture_sets");
	}
}
