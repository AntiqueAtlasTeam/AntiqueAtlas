package hunternif.mc.impl.atlas.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.client.texture.TileTexture;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Reads all png files available under assets/(?modid)/textures/gui/tiles/(?tex).png as Textures that
 * are referenced by the TextureSets.
 *
 * Note that each texture is represented by TWO Identifiers:
 *  - The identifier of the physical location in modid:texture/gui/tiles/tex.png
 *  - The logical identifier modid:tex referenced by TextureSets
 */
@Environment(EnvType.CLIENT)
public class TextureConfig implements SimpleResourceReloadListener<Map<Identifier, ITexture>> {
    private final Map<Identifier, ITexture> texture_map;

    public TextureConfig(Map<Identifier, ITexture> texture_map) {
        this.texture_map = texture_map;
    }

    @Override
    public CompletableFuture<Map<Identifier, ITexture>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Identifier, ITexture> textures = new HashMap<>();

            try {
                for (Identifier id : manager.findResources("textures/gui/tiles", (s) -> s.endsWith(".png"))) {
                    // id now contains the physical file path of the texture

                    // texture_id is the logical identifier, as it will be referenced by TextureSets
                    Identifier texture_id = new Identifier(
                            id.getNamespace(),
                            id.getPath().replace("textures/gui/tiles/", "").replace(".png", "")
                    );

                    AntiqueAtlasMod.LOG.info("Found new Texture: " + texture_id);

                    texture_map.put(texture_id, new TileTexture(id));
                }

            } catch (Throwable e) {
                AntiqueAtlasMod.LOG.warn("Failed to read textures!", e);
            }

            return textures;
        });
    }

    @Override
    public CompletableFuture<Void> apply(Map<Identifier, ITexture> textures, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            for (Map.Entry<Identifier, ITexture> entry : textures.entrySet()) {
                texture_map.put(entry.getKey(), entry.getValue());
                Log.info("Loaded texture %s with path %s", entry.getKey(), entry.getValue().getTexture());
            }
        });
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("antiqueatlas:textures");
    }
}
