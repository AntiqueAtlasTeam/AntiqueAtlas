package hunternif.mc.atlas.marker;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.FileUtil;

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
public class MarkerTextureConfig {
	private final File file;

	public MarkerTextureConfig(File file) {
		this.file = file;
	}

	public void load() {
		JsonElement root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("Marker textures config not found; creating new");
			save();
			return;
		}
		if (!root.isJsonObject()) {
			AntiqueAtlasMod.logger.error("Malformed marker textures config");
			return;
		}
		
		for (Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
			String markerType = entry.getKey();
			if (!entry.getValue().isJsonPrimitive()) {
				AntiqueAtlasMod.logger.error("Malformed marker textures config entry: " + markerType);
				break;
			}
			ResourceLocation texture = new ResourceLocation(entry.getValue().getAsString());
			MarkerTextureMap.instance().setTexture(markerType, texture);
		}
	}

	public void save() {
		JsonObject root = new JsonObject();
		for (Entry<String, ResourceLocation> entry : MarkerTextureMap.instance().getMap().entrySet()) {
			root.addProperty(entry.getKey(), entry.getValue().toString());
		}
		FileUtil.writeJson(root, file);
	}
}
