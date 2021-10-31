package hunternif.mc.impl.atlas.marker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.forge.resource.IResourceReloadListener;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
@OnlyIn(Dist.CLIENT)
public class MarkerTextureConfig implements IResourceReloadListener<Map<ResourceLocation, MarkerType>> {
    private static final int VERSION = 1;
    private static final JsonParser parser = new JsonParser();

    @Override
    public CompletableFuture<Map<ResourceLocation, MarkerType>> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, MarkerType> typeMap = new HashMap<>();

            for (ResourceLocation id : manager.listResources("atlas/markers", (s) -> s.endsWith(".json"))) {
                ResourceLocation markerId = new ResourceLocation(
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
    public CompletableFuture<Void> apply(Map<ResourceLocation, MarkerType> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            for (ResourceLocation markerId : data.keySet()) {
                MarkerType.register(markerId, data.get(markerId));
            }
        });
    }

    @Override
    public ResourceLocation getForgeId() {
        return new ResourceLocation("antiqueatlas:markers");
    }
}
