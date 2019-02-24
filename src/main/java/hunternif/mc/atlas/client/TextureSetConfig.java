package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;

import java.io.File;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Saves texture set names with the lists of texture variations.
 */
@Environment(EnvType.CLIENT)
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
			Identifier[] textures = new Identifier[array.size()];
			for (int i = 0; i < array.size(); i++) {
				String path = array.get(i).getAsString();
				textures[i] = new Identifier(path);
			}
			data.register(new TextureSet(name, textures));
			Log.info("Loaded texture set %s with %d custom texture(s)", name, textures.length);
		}
	}
	
	@Override
	protected void saveData(JsonObject json, TextureSetMap data) {
		// Sort keys alphabetically:
		Queue<TextureSet> queue = new PriorityQueue<>(data.getAllNonStandardTextureSets());
		while (!queue.isEmpty()) {
			TextureSet set = queue.poll();
			JsonArray paths = new JsonArray();
			for (Identifier texture : set.textures) {
				paths.add(new JsonPrimitive(texture.toString()));
			}
			json.add(set.name, paths);
		}
	}
}
