package hunternif.mc.atlas.marker;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;

/**
 * Maps marker type to texture.
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class MarkerTextureConfig implements SimpleResourceReloadListener<Map<Identifier, MarkerType>> {
	private static final int VERSION = 1;
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<Identifier, MarkerType>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, MarkerType> typeMap = new HashMap<>();

			for (Identifier id : manager.findResources("marker_types", (s) -> s.endsWith(".json"))) {
				if (!id.getNamespace().equals("antiqueatlas")) {
					continue;
				}

				Identifier markerId = new Identifier(
						id.getNamespace(),
						id.getPath().replace("marker_types/", "").replace(".json", "")
				);

				try {
					Resource resource = manager.getResource(id);
					try (
							InputStream stream = resource.getInputStream();
							InputStreamReader reader = new InputStreamReader(stream)
					) {
						JsonObject object = parser.parse(reader).getAsJsonObject();
						MarkerType markerType = new MarkerType(markerId);
						markerType.getJSONData().readFrom(object);
						markerType.setIsFromJson(true);
						typeMap.put(markerId, markerType);
					}
				} catch (Exception e) {
					AntiqueAtlasMod.logger.warn("Error reading marker " + markerId + "!", e);
				}
			}

			return typeMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<Identifier, MarkerType> data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (Identifier markerId : data.keySet()) {
				MarkerRegistry.register(markerId, data.get(markerId));
			}
		});
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("antiqueatlas:marker_types");
	}
}
