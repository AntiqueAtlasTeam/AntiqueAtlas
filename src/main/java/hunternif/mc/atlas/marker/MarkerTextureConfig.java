package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.util.AbstractJSONConfig;

import java.io.File;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Maps marker type to texture.
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class MarkerTextureConfig extends AbstractJSONConfig<MarkerTextureMap> {
	private static final int VERSION = 1;

	public MarkerTextureConfig(File file) {
		super(file);
	}
	
	@Override
	public int currentVersion() {
		return VERSION;
	}
	
	@Override
	protected void loadData(JsonObject json, MarkerTextureMap data, int version) {
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			String markerType = entry.getKey();
			ResourceLocation texture = new ResourceLocation(entry.getValue().getAsString());
			data.setTexture(markerType, texture);
		}
		// TODO: read tile data
	}

	@Override
	protected void saveData(JsonObject json, MarkerTextureMap data) {
		// Sort keys alphabetically:
		Queue<String> queue = new PriorityQueue<String>(data.textureMap.keySet());
		while (!queue.isEmpty()) {
			String markerType = queue.poll();
			json.addProperty(markerType, data.textureMap.get(markerType).toString());
		}
		// TODO: save tile data
	}
}
