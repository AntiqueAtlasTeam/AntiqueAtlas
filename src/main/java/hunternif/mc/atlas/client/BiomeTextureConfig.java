package hunternif.mc.atlas.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.CustomFormatter;
import hunternif.mc.atlas.util.Log;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class BiomeTextureConfig extends AbstractJSONConfig<BiomeTextureMap> {
	private static final int VERSION = 1;
	private final TextureSetMap textureSetMap;

	public BiomeTextureConfig(File file, TextureSetMap textureSetMap) {
		super(file);
		this.textureSetMap = textureSetMap;
	}
	
	@Override
	public int currentVersion() {
		return VERSION;
	}

	@Override
	protected void loadData(JsonObject json, BiomeTextureMap data, int version) {
		if (version == 0) {
			Log.warn("Too many biome textures changed since config version 0,"
					+ " disregarding this config entirely");
			return;
		}

		for (Entry<String, JsonElement> entry : json.entrySet()) {
			String key = entry.getKey();

			if (!(key.length() > 0)) {
				Log.warn("No biome ID specified, skipping entry");
				return;
			}

			int biomeID;
			Biome biome;

			try {
				// See if the biome id is an integer
				biomeID = Integer.parseInt(key);
				biome = Biome.getBiomeForId(biomeID);

				if (biome == null) {
					Log.warn("Cannot find biome for ID %d", biomeID);
					return;
				}
			} catch(NumberFormatException e) {
				// If it is not an integer, attempt to find the biome ID assuming it is a resource location
				biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(key));

				if (biome == null) {
					Log.warn("Biome ID is invalid, skipping entry with key: %s", key);
					return;
				}

				biomeID = Biome.getIdForBiome(biome);
			}

			if (entry.getValue().isJsonArray()) {
				// List of textures: (this should be gone as of VERSION > 1)
				JsonArray array = entry.getValue().getAsJsonArray();
				ResourceLocation[] textures = new ResourceLocation[array.size()];
				for (int i = 0; i < array.size(); i++) {
					String path = array.get(i).getAsString();
					textures[i] = new ResourceLocation(path);
				}
				data.setTexture(biomeID, new TextureSet(null, textures));
				Log.info("Registered %d custom texture(s) for biome %s",
						textures.length, CustomFormatter.getRegistryString(biome));
			} else {
				// Texture set:
				String name = entry.getValue().getAsString();
				if (textureSetMap.isRegistered(name)) {
					data.setTexture(biomeID, textureSetMap.getByName(name));
					Log.info("Registered texture set %s for biome %s",
							name, CustomFormatter.getRegistryString(biome));
				} else {
					Log.warn("Unknown texture set %s for biome %s",
							name, CustomFormatter.getRegistryString(biome));
				}
			}
		}
	}
	
	@Override
	protected void saveData(JsonObject json, BiomeTextureMap data) {
		// Sort keys (biome IDs) numerically:
		Queue<Biome> queue = new PriorityQueue<>(data.textureMap.keySet());
		while (!queue.isEmpty()) {
			Biome biome = queue.poll();
			int biomeID = Biome.getIdForBiome(biome);
			// Only save biomes 0-256 in this config.
			// The rest goes into ExtTileTextureConfig
			if (biomeID >= 0 && biomeID < 256) {
				String key = CustomFormatter.getRegistryString(biome);
				json.addProperty(key, data.textureMap.get(biome).name);
			}
		}
	}
}
