package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.core.BiomeTextureMap.BiomeTextureEntry;
import hunternif.mc.atlas.util.FileUtil;

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
 * Client-only config mapping biome IDs (or pseudo-IDs) to texture or texture sets.
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class BiomeTextureConfig {
	private final File file;
	
	public BiomeTextureConfig(File file) {
		this.file = file;
	}
	
	public void load() {
		JsonElement root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("Biome texture config not found");
			return;
		}
		if (!root.isJsonObject()) {
			AntiqueAtlasMod.logger.error("Malformed biome texture config");
			return;
		}
		
		for (Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
			try {
				int biomeID = Integer.parseInt(entry.getKey());
				if (entry.getValue().isJsonArray()) {
					// List of textures:
					for (JsonElement path : entry.getValue().getAsJsonArray()) {
						if (!path.isJsonPrimitive()) {
							AntiqueAtlasMod.logger.error("Malformed biome texture path: " + path.toString());
							break;
						}
						ResourceLocation texture = new ResourceLocation(path.getAsString());
						BiomeTextureMap.instance().setTexture(biomeID, texture);
					}
					AntiqueAtlasMod.logger.info("Registered custom texture for biome " + biomeID);
				} else {
					// Standard texture set:
					if (!entry.getValue().isJsonPrimitive()) {
						AntiqueAtlasMod.logger.error("Malformed biome texture config entry: " + entry.getValue().toString());
						break;
					}
					String textureSetName = entry.getValue().getAsString();
					if (StandardTextureSet.contains(textureSetName)) {
						BiomeTextureMap.instance().setTexture(biomeID, StandardTextureSet.valueOf(textureSetName));
					}
					AntiqueAtlasMod.logger.info("Registered standard texture set for biome " + biomeID);
				}
			} catch (NumberFormatException e) {
				AntiqueAtlasMod.logger.error("Malformed biome texture config entry: " + e.toString());
				break;
			}
		}
	}
	
	public void save() {
		JsonObject root = new JsonObject();
		for (BiomeTextureEntry entry : BiomeTextureMap.instance().textureMap.values()) {
			if (entry.isStandardSet()) {
				root.addProperty(String.valueOf(entry.biomeID), entry.textureSet.name());
			} else {
				JsonArray paths = new JsonArray();
				for (ResourceLocation texture : entry.textures) {
					paths.add(new JsonPrimitive(texture.toString()));
				}
				root.add(String.valueOf(entry.biomeID), paths);
			}
		}
		FileUtil.writeJson(root, file);
	}
}
