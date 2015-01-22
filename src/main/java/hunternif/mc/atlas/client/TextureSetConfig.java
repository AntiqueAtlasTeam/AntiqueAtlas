package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.AbstractJSONConfig;

import java.io.File;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TextureSetConfig extends AbstractJSONConfig<TextureSetMap> {
	private static final int VERSION = 1;

	public TextureSetConfig(File file) {
		super(file);
	}
	
	public int currentVersion() {
		return VERSION;
	}
	
	@Override
	protected void loadData(JsonObject json, TextureSetMap data, int version) {
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			String name = entry.getKey();
			JsonArray array = entry.getValue().getAsJsonArray();
			ResourceLocation[] textures = new ResourceLocation[array.size()];
			for (int i = 0; i < array.size(); i++) {
				String path = array.get(i).getAsString();
				textures[i] = new ResourceLocation(path);
			}
			data.register(new TextureSet(name, textures));
			AntiqueAtlasMod.logger.info("Loaded texture set \"" + name
					+ "\" with " + textures.length + " custom texture(s).");
		}
	}
	
	@Override
	protected void saveData(JsonObject json, TextureSetMap data) {
		for (TextureSet set : data.getAllNonStandardTextureSets()) {
			JsonArray paths = new JsonArray();
			for (ResourceLocation texture : set.textures) {
				paths.add(new JsonPrimitive(texture.toString()));
			}
			json.add(set.name, paths);
		}
	}
}
