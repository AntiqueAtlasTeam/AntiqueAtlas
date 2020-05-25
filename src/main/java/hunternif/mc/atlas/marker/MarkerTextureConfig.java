package hunternif.mc.atlas.marker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Maps marker type to texture.
 *
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class MarkerTextureConfig implements ISelectiveResourceReloadListener {
    private static final int VERSION = 1;
    private static final JsonParser parser = new JsonParser();


    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Map<ResourceLocation, MarkerType> typeMap = new HashMap<>();

        for (ResourceLocation id : resourceManager.getAllResourceLocations("marker_types", (s) -> s.endsWith(".json"))) {
            if (!id.getNamespace().equals("antiqueatlas")) {
                continue;
            }

            ResourceLocation markerId = new ResourceLocation(
                    id.getNamespace(),
                    id.getPath().replace("marker_types/", "").replace(".json", "")
            );

            try {
                IResource resource = resourceManager.getResource(id);
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
        for (ResourceLocation markerId : typeMap.keySet()) {
            MarkerRegistry.register(markerId, typeMap.get(markerId));
        }

    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if (resourcePredicate.test(getResourceType())) {
            onResourceManagerReload(resourceManager);
        }
    }

    @Nullable
    @Override
    public IResourceType getResourceType() {
        return VanillaResourceType.TEXTURES;
    }
}
