package hunternif.mc.atlas.marker;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;

/**
 * Maps marker type to texture.
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class MarkerTextureConfig extends AbstractJSONConfig<MarkerRegistry> {
	private static final int VERSION = 1;
	
	private static final JsonParser parser = new JsonParser();
	private static final String EXAMPLE_LOC = "antiqueatlas:Example";
	private JsonElement EXAMPLE_JSON;
	
	public MarkerTextureConfig(File file) {
		super(file);
		
		InputStream is = getClass().getResourceAsStream("/markerExample.json");
		
		InputStreamReader reader = new InputStreamReader(is);
		EXAMPLE_JSON = parser.parse(reader);
		
		IOUtils.closeQuietly(is);
	}
	
	@Override
	public int currentVersion() {
		return VERSION;
	}
	
	@Override
	protected void loadData(JsonObject json, MarkerRegistry data, int version) {
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			String markerType = entry.getKey();
			if(markerType.equals(EXAMPLE_LOC))
				continue; // don't parse the example
			
			if(!entry.getValue().isJsonObject()) {
				Log.warn("Loading marker %s from JSON: Entry isn't a JSON object!", markerType);
			}
			JsonObject object = (JsonObject) entry.getValue();
			
			ResourceLocation key = MarkerRegistry.getLoc(markerType);
			if(MarkerRegistry.hasKey(key)) {
				MarkerRegistry.find(key).getJSONData().readFrom(object);
			} else {
				MarkerType type = new MarkerType(key);
				type.getJSONData().readFrom(object);
				type.setIsFromJson(true);
				MarkerRegistry.register(type);
			}
		}
	}

	@Override
	protected void saveData(JsonObject json, MarkerRegistry data) {
		Queue<ResourceLocation> queue = new PriorityQueue<ResourceLocation>(new Comparator<ResourceLocation>() {
			@Override
			public int compare(ResourceLocation o1, ResourceLocation o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		queue.addAll(MarkerRegistry.getKeys());

		json.add(EXAMPLE_LOC, EXAMPLE_JSON);
		while (!queue.isEmpty()) {
			ResourceLocation key = queue.poll();
			JsonObject value = new JsonObject();
			MarkerRegistry.find(key).getJSONData().saveTo(value);
			json.add(key.toString(), value);
		}
		queue = null; // for breakpoints
	}
}
