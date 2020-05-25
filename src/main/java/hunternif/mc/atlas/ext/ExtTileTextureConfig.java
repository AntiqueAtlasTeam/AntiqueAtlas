package hunternif.mc.atlas.ext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.TextureSetConfig;
import hunternif.mc.atlas.client.TextureSetMap;
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
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * Client-only config mapping tile names to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class ExtTileTextureConfig implements ISelectiveResourceReloadListener {
	private static final int VERSION = 1;
	private static final JsonParser PARSER = new JsonParser();
	private final TextureSetMap textureSetMap;
	private final ExtTileTextureMap extTileTextureMap;

	public ExtTileTextureConfig(ExtTileTextureMap extTileTextureMap, TextureSetMap textureSetMap) {
		this.extTileTextureMap = extTileTextureMap;
		this.textureSetMap = textureSetMap;
	}
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		Map<String, String> map = new HashMap<>();

		try {
			for (IResource resource : resourceManager.getAllResources(new ResourceLocation("antiqueatlas:tile_textures.json"))) {
				try (InputStream stream = resource.getInputStream(); InputStreamReader reader = new InputStreamReader(stream)) {
					JsonElement element = PARSER.parse(reader);
					if (element.isJsonObject()) {
						JsonObject obj = element.getAsJsonObject();
						if (!obj.has("version")) {
							Log.warn("Invalid tile texture file found!");
						} else if (obj.get("version").getAsInt() < VERSION) {
							Log.warn("Outdated tile texture file version: " + obj.get("version").getAsInt());
						} else {
							for (Entry<String, JsonElement> entry : obj.get("data").getAsJsonObject().entrySet()) {
								map.put(entry.getKey(), entry.getValue().getAsString());
							}
						}
					} else {
						Log.warn("Invalid tile texture file found!");
					}
				} catch (Throwable e) {
					Log.warn(e, "Failed to read tile texture file!");
				}
			}
		} catch (Throwable e) {
			Log.warn(e, "Failed to read tile textures!");
		}

		for (Entry<String, String> entry : map.entrySet()) {
			String tileName = entry.getKey();
			String textureName = entry.getValue();

			if (textureSetMap.isRegistered(textureName)) {
				extTileTextureMap.setTexture(AntiqueAtlasMod.id(tileName), textureSetMap.getByName(AntiqueAtlasMod.id(textureName)));
				Log.info("Registered texture set %s for tile \"%s\"", textureName, tileName);
			} else {
				Log.warn("Unknown texture set %s for tile \"%s\"", textureName, tileName);
			}
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
