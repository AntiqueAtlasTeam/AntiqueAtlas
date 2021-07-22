package hunternif.mc.impl.atlas.client;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.forge.IResourceReloadListener;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 *
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class TileTextureConfig implements IResourceReloadListener<Map<ResourceLocation, ResourceLocation>> {
    private static final int VERSION = 1;
    private static final JsonParser PARSER = new JsonParser();
    private final TileTextureMap tileTextureMap;
    private final TextureSetMap textureSetMap;

    public TileTextureConfig(TileTextureMap biomeTextureMap, TextureSetMap textureSetMap) {
        this.tileTextureMap = biomeTextureMap;
        this.textureSetMap = textureSetMap;
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, ResourceLocation>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, ResourceLocation> map = new HashMap<>();

            try {
                for (ResourceLocation id : manager.getAllResourceLocations("atlas/tiles", (s) -> s.endsWith(".json"))) {
                    ResourceLocation tile_id = new ResourceLocation(
                            id.getNamespace(),
                            id.getPath().replace("atlas/tiles/", "").replace(".json", "")
                    );

                    try {
                        IResource resource = manager.getResource(id);
                        try (
                                InputStream stream = resource.getInputStream();
                                InputStreamReader reader = new InputStreamReader(stream)
                        ) {
                            JsonObject object = PARSER.parse(reader).getAsJsonObject();

                            int version = object.getAsJsonPrimitive("version").getAsInt();
                            if (version != VERSION) {
                                AntiqueAtlasMod.LOG.warn("The tile " + tile_id + " is in the wrong version! Skipping.");
                                continue;
                            }

                            ResourceLocation texture_set = new ResourceLocation(object.get("texture_set").getAsString());

                            map.put(tile_id, texture_set);
                        }
                    } catch (Exception e) {
                        AntiqueAtlasMod.LOG.warn("Error reading tile mapping " + tile_id + "!", e);
                    }
                }
            } catch (Throwable e) {
                Log.warn(e, "Failed to read tile mappings!");
            }

            return map;
        });
    }

    @Override
    public CompletableFuture<Void> apply(Map<ResourceLocation, ResourceLocation> tileMap, IResourceManager manager, IProfiler profiler, Executor executor) {
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : tileMap.entrySet()) {
            ResourceLocation tile_id = entry.getKey();
            ResourceLocation texture_set = entry.getValue();
            TextureSet set = textureSetMap.getByName(entry.getValue());

            if(set == null) {
                AntiqueAtlasMod.LOG.error("Missing texture set `{}` for tile `{}`. Using default.", texture_set, tile_id);

                set = tileTextureMap.getDefaultTexture();
            }

            tileTextureMap.setTexture(entry.getKey(), set);
            Log.info("Using texture set %s for tile %s", set.name, tile_id);
        }

        return CompletableFuture.runAsync(() -> {

        });
    }

//    @Override
//    public ResourceLocation getFabricId() {
//        return new ResourceLocation("antiqueatlas:tile_textures");
//    }
//
//    @Override
//    public Collection<ResourceLocation> getFabricDependencies() {
//        return Collections.singleton(new ResourceLocation("antiqueatlas:texture_sets"));
//    }
}
