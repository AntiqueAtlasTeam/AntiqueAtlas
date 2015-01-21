package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.Config;
import hunternif.mc.atlas.util.FileUtil;

import java.io.File;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class TextureSetConfig implements Config<TextureSetMap> {
	private final int VERSION = 1;
	private final File file;

	public TextureSetConfig(File file) {
		this.file = file;
	}
	
	@Override
	public void load(TextureSetMap data) {
		JsonElement root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("Texture set config not found; creating new");
			save(data);
			return;
		}
		if (!root.isJsonObject()) {
			AntiqueAtlasMod.logger.error("Malformed texture set config");
			return;
		}
		
		//TODO read config version
		for (Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
			try {
				String name = entry.getKey();
				JsonArray array = entry.getValue().getAsJsonArray();
				ResourceLocation[] textures = new ResourceLocation[array.size()];
				for (int i = 0; i < array.size(); i++) {
					JsonElement path = array.get(i);
					if (!path.isJsonPrimitive()) {
						AntiqueAtlasMod.logger.error("Malformed texture set path: " + path.toString());
						break;
					}
					textures[i] = new ResourceLocation(path.getAsString());
				}
				data.register(new TextureSet(name, textures));
				AntiqueAtlasMod.logger.info("Loaded texture set \"" + name
						+ "\" with " + textures.length + " custom texture(s).");
			} catch (NumberFormatException e) {
				AntiqueAtlasMod.logger.error("Malformed texture set config entry: " + e.toString());
				break;
			}
		}
	}

	@Override
	public void save(TextureSetMap data) {
		JsonObject root = new JsonObject();
		for (TextureSet set : data.getAllNonStandardTextureSets()) {
			JsonArray paths = new JsonArray();
			for (ResourceLocation texture : set.textures) {
				paths.add(new JsonPrimitive(texture.toString()));
			}
			root.add(set.name, paths);
		}
		FileUtil.writeJson(root, file);
	}

}
