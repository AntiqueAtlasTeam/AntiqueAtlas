package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.FileUtil;

import java.io.File;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Maps unique names of external tiles to pseudo-biome IDs.
 * @author Hunternif
 */
public class ExtTileConfig {
	private final File file;
	
	public ExtTileConfig(File file) {
		this.file = file;
	}
	
	public void load() {
		JsonElement root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("tileIDs config not found");
			return;
		}
		if (!root.isJsonObject()) {
			AntiqueAtlasMod.logger.error("Malformed tileIDs config");
			return;
		}
		
		for (Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
			String name = entry.getKey();
			if (!entry.getValue().isJsonPrimitive()) {
				AntiqueAtlasMod.logger.error("Malformed tileIDs config entry: " + name);
				break;
			}
			try {
				int id = entry.getValue().getAsInt();
				ExtTileIdMap.instance().setPseudoBiomeID(name, id);
			} catch (NumberFormatException e) {
				AntiqueAtlasMod.logger.error("Malformed tileIDs config entry: " + name);
				break;
			}
		}
	}
	
	public void save() {
		JsonObject root = new JsonObject();
		for (Entry<String, Integer> entry : ExtTileIdMap.instance().getMap().entrySet()) {
			root.addProperty(entry.getKey(), entry.getValue());
		}
		FileUtil.writeJson(root, file);
	}
}
