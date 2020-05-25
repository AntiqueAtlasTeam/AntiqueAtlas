package hunternif.mc.atlas.ext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

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
			data.setPseudoBiomeID(AntiqueAtlasMod.id(name), id);
		}
	}
	
	@Override
	protected void saveData(JsonObject json, ExtTileIdMap data) {
		// Sort keys alphabetically
		Queue<ResourceLocation> queue = new PriorityQueue<>(data.getMap().keySet());
		while (!queue.isEmpty()) {
			ResourceLocation tileName = queue.poll();
			json.addProperty(tileName.toString(), data.getMap().get(tileName));
		}
	}
}
