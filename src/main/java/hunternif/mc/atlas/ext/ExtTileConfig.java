package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.util.AbstractJSONConfig;

import java.io.File;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Maps unique names of external tiles to pseudo-biome IDs.
 * This config is needed because the NBT data for atlases contains these IDs
 * and not the names.
 * @author Hunternif
 */
public class ExtTileConfig extends AbstractJSONConfig<ExtTileIdMap> {
	private static final int VERSION = 1;

	public ExtTileConfig(File file) {
		super(file);
	}
	
	@Override
	public int currentVersion() {
		return VERSION;
	}

	@Override
	protected void loadData(JsonObject json, ExtTileIdMap data, int version) {
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			String name = entry.getKey();
			int id = entry.getValue().getAsInt();
			data.setPseudoBiomeID(name, id);
		}
	}
	
	@Override
	protected void saveData(JsonObject json, ExtTileIdMap data) {
		for (Entry<String, Integer> entry : data.getMap().entrySet()) {
			json.addProperty(entry.getKey(), entry.getValue());
		}
	}
}
