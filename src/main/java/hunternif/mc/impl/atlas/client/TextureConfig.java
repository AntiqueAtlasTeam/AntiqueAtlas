package hunternif.mc.impl.atlas.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.client.texture.TileTexture;
import hunternif.mc.impl.atlas.forge.IResourceReloadListener;
import hunternif.mc.impl.atlas.util.Log;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Reads all png files available under assets/(?modid)/textures/gui/tiles/(?tex).png as Textures that
 * are referenced by the TextureSets.
 *
 * Note that each texture is represented by TWO Identifiers:
 *  - The identifier of the physical location in modid:texture/gui/tiles/tex.png
 *  - The logical identifier modid:tex referenced by TextureSets
 */
@OnlyIn(Dist.CLIENT)
public class TextureConfig implements IResourceReloadListener<Map<ResourceLocation, ITexture>> {
    private final Map<ResourceLocation, ITexture> texture_map;

    public TextureConfig(Map<ResourceLocation, ITexture> texture_map) {
        this.texture_map = texture_map;
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, ITexture>> load(IResourceManager manager, IProfiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, ITexture> textures = new HashMap<>();

            try {
                for (ResourceLocation id : manager.getAllResourceLocations("textures/gui/tiles", (s) -> s.endsWith(".png"))) {
                    // id now contains the physical file path of the texture

                    // texture_id is the logical identifier, as it will be referenced by TextureSets
                    ResourceLocation texture_id = new ResourceLocation(
                            id.getNamespace(),
                            id.getPath().replace("textures/gui/tiles/", "").replace(".png", "")
                    );

                    AntiqueAtlasMod.LOG.info("Found new Texture: " + texture_id);

                    textures.put(texture_id, new TileTexture(id));
                }

            } catch (Throwable e) {
                AntiqueAtlasMod.LOG.warn("Failed to read textures!", e);
            }

            return textures;
        });
    }

    @Override
    public CompletableFuture<Void> apply(Map<ResourceLocation, ITexture> textures, IResourceManager manager, IProfiler profiler, Executor executor) {
        for (Map.Entry<ResourceLocation, ITexture> entry : textures.entrySet()) {
            texture_map.put(entry.getKey(), entry.getValue());
            Log.info("Loaded texture %s with path %s", entry.getKey(), entry.getValue().getTexture());
        }

        return CompletableFuture.runAsync(() -> {

        });
    }

//    @Override
//    public ResourceLocation getFabricId() {
//        return new ResourceLocation("antiqueatlas:textures");
//    }
}
