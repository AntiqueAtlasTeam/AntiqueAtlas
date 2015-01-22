package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.util.AbstractJSONConfig;

import java.io.File;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	}

	@Override
	protected void saveData(JsonObject json, MarkerTextureMap data) {
		for (Entry<String, ResourceLocation> entry : data.getMap().entrySet()) {
			json.addProperty(entry.getKey(), entry.getValue().toString());
		}
	}
}
