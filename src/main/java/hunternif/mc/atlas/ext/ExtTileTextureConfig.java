package hunternif.mc.atlas.ext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.client.TextureSetConfig;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Client-only config mapping tile names to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class ExtTileTextureConfig extends AbstractJSONConfig<ExtTileTextureMap> {
	private static final int VERSION = 1;
	private final TextureSetMap textureSetMap;

	public ExtTileTextureConfig(File file, TextureSetMap textureSetMap) {
		super(file);
		this.textureSetMap = textureSetMap;
	}
	
	@Override
	public int currentVersion() {
		return VERSION;
	}

	@Override
	protected void loadData(JsonObject json, ExtTileTextureMap data, int version) {
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			String tileName = entry.getKey();
			String textureName = entry.getValue().getAsString();
			if (textureSetMap.isRegistered(textureName)) {
				data.setTexture(tileName, textureSetMap.getByName(textureName));
				Log.info("Registered texture set %s for tile \"%s\"", textureName, tileName);
			} else {
				Log.warn("Unknown texture set %s for tile \"%s\"", textureName, tileName);
			}
		}
	}
	
	@Override
	protected void saveData(JsonObject json, ExtTileTextureMap data) {
		// Sort keys alphabetically:
		Queue<String> queue = new PriorityQueue<>(data.textureMap.keySet());
		while (!queue.isEmpty()) {
			String tileName = queue.poll();
			json.addProperty(tileName, data.textureMap.get(tileName).name);
		}
	}
}
