package hunternif.mc.impl.atlas.marker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.forge.IResourceReloadListener;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Maps marker type to texture.
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class MarkerTextureConfig implements IResourceReloadListener<Map<ResourceLocation, MarkerType>> {
	private static final int VERSION = 1;
	private static final JsonParser parser = new JsonParser();

	@Override
	public CompletableFuture<Map<ResourceLocation, MarkerType>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<ResourceLocation, MarkerType> typeMap = new HashMap<>();

			for (ResourceLocation id : manager.getAllResourceLocations("atlas/marker_types", (s) -> s.endsWith(".json"))) {
				if (!id.getNamespace().equals("antiqueatlas")) {
					continue;
				}

				ResourceLocation markerId = new ResourceLocation(
						id.getNamespace(),
						id.getPath().replace("atlas/marker_types/", "").replace(".json", "")
				);

				try {
					IResource resource = manager.getResource(id);
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
					AntiqueAtlasMod.LOG.warn("Error reading marker " + markerId + "!", e);
				}
			}

			return typeMap;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<ResourceLocation, MarkerType> data, IResourceManager manager, IProfiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			for (ResourceLocation markerId : data.keySet()) {
				MarkerType.register(markerId, data.get(markerId));
			}
		});
	}
//
//	@Override
//	public ResourceLocation getFabricId() {
//		return new ResourceLocation("antiqueatlas:marker_types");
//	}
}
