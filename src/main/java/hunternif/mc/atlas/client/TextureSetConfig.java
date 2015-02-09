package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;

import java.io.File;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Saves texture set names with the lists of texture variations.
 */
@SideOnly(Side.CLIENT)
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
			Log.info("Loaded texture set %s with %d custom texture(s)", name, textures.length);
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
