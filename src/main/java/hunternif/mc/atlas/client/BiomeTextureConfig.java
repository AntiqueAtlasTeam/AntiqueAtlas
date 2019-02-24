package hunternif.mc.atlas.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class BiomeTextureConfig extends AbstractJSONConfig<BiomeTextureMap> {
	private static final int VERSION = 2;
	private final TextureSetMap textureSetMap;

	public BiomeTextureConfig(File file, TextureSetMap textureSetMap) {
		super(file);
		this.textureSetMap = textureSetMap;
	}
	
	@Override
	public int currentVersion() {
		return VERSION;
	}

	@Override
	protected void loadData(JsonObject json, BiomeTextureMap data, int version) {
		if (version == 0) {
			Log.warn("Too many biome textures changed since config version 0,"
					+ " disregarding this config entirely");
			return;
		}
		if (version == 1) {
			Log.warn("Config version 1 no longer supported, config file will be reset"
					+ " We now use resource location to identify biomes");
			return;
		}
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			Biome biome = Registry.BIOME.get(new Identifier(entry.getKey()));
			if (biome == null) {
				Log.warn("Unknown biome in texture map: %s", entry.getKey());
				continue;
			}
			if (entry.getValue().isJsonArray()) {
				// List of textures: (this should be gone as of VERSION > 1)
				JsonArray array = entry.getValue().getAsJsonArray();
				Identifier[] textures = new Identifier[array.size()];
				for (int i = 0; i < array.size(); i++) {
					String path = array.get(i).getAsString();
					textures[i] = new Identifier(path);
				}
				data.setTexture(biome, new TextureSet(null, textures));
				Log.info("Registered %d custom texture(s) for biome %s",
						textures.length, entry.getKey());
			} else {
				// Texture set:
				String name = entry.getValue().getAsString();
				if (textureSetMap.isRegistered(name)) {
					data.setTexture(biome, textureSetMap.getByName(name));
					Log.info("Registered texture set %s for biome %s", name, entry.getKey());
				} else {
					Log.warn("Unknown texture set %s for biome %s", name, entry.getKey());
				}
			}
		}
	}
	
	@Override
	protected void saveData(JsonObject json, BiomeTextureMap data) {
		// Sort resource locations by name
		List<Biome> keys = new ArrayList<>(data.biomeTextureMap.keySet());
		Collections.sort(keys, Comparator.comparing((b) -> Registry.BIOME.getId(b).toString()));

		for(Biome key : keys) {
			json.addProperty(Registry.BIOME.getId(key).toString(), data.biomeTextureMap.get(key).name);
		}
	}
}
