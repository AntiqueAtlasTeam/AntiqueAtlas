package hunternif.mc.impl.atlas.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.util.Log;
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
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 *
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class TileTextureConfig implements IResourceReloadListener<Map<Identifier, Identifier>> {
    private static final int VERSION = 1;
    private static final JsonParser PARSER = new JsonParser();
    private final TileTextureMap tileTextureMap;
    private final TextureSetMap textureSetMap;

    public TileTextureConfig(TileTextureMap biomeTextureMap, TextureSetMap textureSetMap) {
        this.tileTextureMap = biomeTextureMap;
        this.textureSetMap = textureSetMap;
    }

    @Override
    public CompletableFuture<Map<Identifier, Identifier>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Identifier, Identifier> map = new HashMap<>();

            try {
                for (Identifier id : manager.findResources("atlas/tiles", (s) -> s.endsWith(".json"))) {
                    Identifier tile_id = new Identifier(
                            id.getNamespace(),
                            id.getPath().replace("atlas/tiles/", "").replace(".json", "")
                    );

                    try {
                        Resource resource = manager.getResource(id);
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

                            Identifier texture_set = new Identifier(object.get("texture_set").getAsString());

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
    public CompletableFuture<Void> apply(Map<Identifier, Identifier> tileMap, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            for (Map.Entry<Identifier, Identifier> entry : tileMap.entrySet()) {
                Identifier tile_id = entry.getKey();
                Identifier texture_set = entry.getValue();
                TextureSet set = textureSetMap.getByName(entry.getValue());

                if (set == null) {
                    AntiqueAtlasMod.LOG.error("Missing texture set `{}` for tile `{}`. Using default.", texture_set, tile_id);

                    set = tileTextureMap.getDefaultTexture();
                }

                tileTextureMap.setTexture(entry.getKey(), set);
                Log.info("Using texture set %s for tile %s", set.name, tile_id);
            }
        });
    }

    @Override
    public String getName() {
        return "antiqueatlas:tile_textures";
    }

    // TODO DEPENDENCY
//    @Override
//    public Collection<Identifier> getFabricDependencies() {
//        return Collections.singleton(new Identifier("antiqueatlas:texture_sets"));
//    }
}
