package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.Config;
import hunternif.mc.atlas.util.FileUtil;

import java.io.File;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Client-only config mapping biome IDs (or pseudo-IDs) to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class BiomeTextureConfig implements Config<BiomeTextureMap> {
	private static final int VERSION = 1;
	private final File file;
	private final TextureSetMap textureSetMap;

	public BiomeTextureConfig(File file, TextureSetMap textureSetMap) {
		this.file = file;
		this.textureSetMap = textureSetMap;
	}

	public void load(BiomeTextureMap data) {
		JsonElement root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("Biome texture config not found; creating new");
			save(data);
			return;
		}
		if (!root.isJsonObject()) {
			AntiqueAtlasMod.logger.error("Malformed biome texture config");
			return;
		}
		
		//TODO read config version
		for (Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
			try {
				int biomeID = Integer.parseInt(entry.getKey());
				if (entry.getValue().isJsonArray()) {
					// List of textures: (this should be gone as of VERSION > 2)
					JsonArray array = entry.getValue().getAsJsonArray();
					ResourceLocation[] textures = new ResourceLocation[array.size()];
					for (int i = 0; i < array.size(); i++) {
						JsonElement path = array.get(i);
						if (!path.isJsonPrimitive()) {
							AntiqueAtlasMod.logger.error("Malformed biome texture path: " + path.toString());
							break;
						}
						textures[i] = new ResourceLocation(path.getAsString());
					}
					data.setTexture(biomeID, new TextureSet(null, textures));
					AntiqueAtlasMod.logger.info("Registered " + textures.length
							+ " custom texture(s) for biome " + biomeID);
				} else {
					// Standard texture set:
					if (!entry.getValue().isJsonPrimitive()) {
						AntiqueAtlasMod.logger.error("Malformed biome texture config entry: " + entry.getValue().toString());
						break;
					}
					String name = entry.getValue().getAsString();
					if (textureSetMap.isRegistered(name)) {
						data.setTexture(biomeID, textureSetMap.getByName(name));
						AntiqueAtlasMod.logger.info("Registered texture set " + name + " for biome " + biomeID);
					} else {
						AntiqueAtlasMod.logger.warn("Unknown texture set " + name + " for biome " + biomeID);
					}
				}
			} catch (NumberFormatException e) {
				AntiqueAtlasMod.logger.error("Malformed biome texture config entry: " + e.toString());
				break;
			}
		}
	}

	public void save(BiomeTextureMap data) {
		JsonObject root = new JsonObject();
		for (Entry<Integer,TextureSet> entry : data.textureMap.entrySet()) {
			root.addProperty(entry.getKey().toString(), entry.getValue().name);
		}
		FileUtil.writeJson(root, file);
	}
}
