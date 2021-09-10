package hunternif.mc.impl.atlas.marker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.IResourceReloadListener;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Maps marker type to texture.
 *
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class MarkerTextureConfig implements IResourceReloadListener<Map<Identifier, MarkerType>> {
    private static final int VERSION = 1;
    private static final JsonParser parser = new JsonParser();

    @Override
    public CompletableFuture<Map<Identifier, MarkerType>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Identifier, MarkerType> typeMap = new HashMap<>();

            for (Identifier id : manager.findResources("atlas/markers", (s) -> s.endsWith(".json"))) {
                Identifier markerId = new Identifier(
                        id.getNamespace(),
                        id.getPath().replace("atlas/markers/", "").replace(".json", "")
                );

                try {
                    Resource resource = manager.getResource(id);
                    try (
                            InputStream stream = resource.getInputStream();
                            InputStreamReader reader = new InputStreamReader(stream)
                    ) {
                        JsonObject object = parser.parse(reader).getAsJsonObject();

                        int version = object.getAsJsonPrimitive("version").getAsInt();

                        if (version != VERSION) {
                            throw new RuntimeException("Incompatible version (" + VERSION + " != " + version + ")");
                        }

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
    public CompletableFuture<Void> apply(Map<Identifier, MarkerType> data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            for (Identifier markerId : data.keySet()) {
                MarkerType.register(markerId, data.get(markerId));
            }
        });
    }

    @Override
    public String getName() {
        return "antiqueatlas:markers";
    }
}
