package hunternif.mc.atlas.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.atlas.AntiqueAtlasMod;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * Saves texture set names with the lists of texture variations.
 */
@OnlyIn(Dist.CLIENT)
public class TextureSetConfig implements ISelectiveResourceReloadListener {
	private static final int VERSION = 1;
	private static final JsonParser PARSER = new JsonParser();
	private final TextureSetMap textureSetMap;

	public TextureSetConfig(TextureSetMap textureSetMap) {
		this.textureSetMap = textureSetMap;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		List<TextureSet> sets = new ArrayList<>();

		try {
			for (IResource resource : resourceManager.getAllResources(new ResourceLocation("antiqueatlas:texture_sets.json"))) {
				try (InputStream stream = resource.getInputStream(); InputStreamReader reader = new InputStreamReader(stream)) {
					JsonElement element = PARSER.parse(reader);
					if (element.isJsonObject()) {
						JsonObject obj = element.getAsJsonObject();
						if (!obj.has("version")) {
							Log.warn("Invalid texture set file found!");
						} else if (obj.get("version").getAsInt() < VERSION) {
							Log.warn("Outdated texture set file version: " + obj.get("version").getAsInt());
						} else {
							for (Entry<String, JsonElement> entry : obj.get("data").getAsJsonObject().entrySet()) {
								String name = entry.getKey();
								JsonArray array = entry.getValue().getAsJsonArray();
								ResourceLocation[] textures = new ResourceLocation[array.size()];
								for (int i = 0; i < array.size(); i++) {
									String path = array.get(i).getAsString();
									textures[i] = new ResourceLocation(path);
								}
								sets.add(new TextureSet(AntiqueAtlasMod.id(name), textures));
							}
						}
					} else {
						Log.warn("Invalid texture set file found!");
					}
				} catch (Throwable e) {
					Log.warn(e, "Failed to read texture set file!");
				}
			}
		} catch (Throwable e) {
			Log.warn(e, "Failed to read texture sets!");
		}

		for (TextureSet set : sets) {
			textureSetMap.register(set);
			Log.info("Loaded texture set %s with %d custom texture(s)", set.name, set.textures.length);
		}
	}

	@Nullable
	@Override
	public IResourceType getResourceType() {
		return VanillaResourceType.TEXTURES;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		if (resourcePredicate.test(getResourceType())) {
			onResourceManagerReload(resourceManager);
		}
	}
}
