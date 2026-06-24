package hunternif.mc.impl.atlas.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.scaning.TileHeightType;
import hunternif.mc.impl.atlas.resource.ResourceReloadListener;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
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
public class TileTextureConfig implements ResourceReloadListener<Map<Identifier, Identifier>> {
    public static final Identifier ID = AntiqueAtlasMod.id("tile_textures");
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
                for (Identifier id : manager.findResources("atlas/tiles", (s) -> s.getPath().endsWith(".json")).keySet()) {
                    Identifier tile_id = new Identifier(id.getNamespace(), id.getPath().replace("atlas/tiles/", "").replace(".json", ""));

                    try {
                        Resource resource = manager.getResource(id).get();
                        try (InputStream stream = resource.getInputStream(); InputStreamReader reader = new InputStreamReader(stream)) {
                            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();

                            int version = object.getAsJsonPrimitive("version").getAsInt();
                            if (version == 1) {
                                Identifier texture_set = new Identifier(object.get("texture_set").getAsString());

                                map.put(tile_id, texture_set);

                                for (TileHeightType layer : TileHeightType.values()) {
                                    map.put(Identifier.tryParse(tile_id + "_" + layer.getName()), texture_set);
                                }
                            } else if (version == 2) {
                                Identifier default_entry = TileTextureMap.DEFAULT_TEXTURE;

                                try {
                                    default_entry = new Identifier(object.getAsJsonObject("texture_sets").get("default").getAsString());
                                } catch (Exception ignored) {
                                }

                                // insert the old-style texture set with the default one
                                map.put(tile_id, default_entry);

                                for (TileHeightType layer : TileHeightType.values()) {
                                    Identifier texture_set = default_entry;

                                    try {
                                        texture_set = new Identifier(object.getAsJsonObject("texture_sets").get(layer.getName()).getAsString());
                                    } catch (Exception ignored) {
                                    }

                                    map.put(Identifier.tryParse(tile_id + "_" + layer), texture_set);
                                }
                            } else {
                                AntiqueAtlasMod.LOG.warn("The tile " + tile_id + " is in the wrong version! Skipping.");
                            }
                        }
                    } catch (Exception e) {
                        AntiqueAtlasMod.LOG.warn("Error reading tile mapping " + tile_id + "!", e);
                    }
                }
            } catch (Throwable e) {
                Log.warn(e, "Failed to read tile mappings!");
            }

            return map;
        }, executor);
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
                if (AntiqueAtlasMod.CONFIG.resourcePackLogging)
                    Log.info("Loaded tile %s with texture set %s", tile_id, set.name);
            }
        }, executor);
    }
    
    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Collection<Identifier> getDependencies() {
        return Collections.singleton(TextureSetConfig.ID);
    }
}
