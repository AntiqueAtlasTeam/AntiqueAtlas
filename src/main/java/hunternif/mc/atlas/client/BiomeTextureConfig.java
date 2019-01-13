package hunternif.mc.atlas.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.Map.Entry;

/**
 * Client-only config mapping biome IDs to texture sets.
 * <p>Must be loaded after {@link TextureSetConfig}!</p>
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public class BiomeTextureConfig extends AbstractJSONConfig<BiomeTextureMap> {
	private static final int VERSION = 2;
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
		if (version == 1) {
			Log.warn("Config version 1 no longer supported, config file will be reset"
					+ " We now use resource location to identify biomes");
			return;
		}
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			Biome biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(entry.getKey()));
			if (biome == null) {
				Log.warn("Unknown biome in texture map: %s", entry.getKey());
				continue;
			}
			if (entry.getValue().isJsonArray()) {
				// List of textures: (this should be gone as of VERSION > 1)
				JsonArray array = entry.getValue().getAsJsonArray();
				ResourceLocation[] textures = new ResourceLocation[array.size()];
				for (int i = 0; i < array.size(); i++) {
					String path = array.get(i).getAsString();
					textures[i] = new ResourceLocation(path);
				}
				data.setTexture(biome, new TextureSet(null, textures));
				Log.info("Registered %d custom texture(s) for biome %s",
						textures.length, biome.getRegistryName().toString());
			} else {
				// Texture set:
				String name = entry.getValue().getAsString();
				if (textureSetMap.isRegistered(name)) {
					data.setTexture(biome, textureSetMap.getByName(name));
					Log.info("Registered texture set %s for biome %s", name, biome.getRegistryName().toString());
				} else {
					Log.warn("Unknown texture set %s for biome %s", name, biome.getRegistryName().toString());
				}
			}
		}
	}
	
	@Override
	protected void saveData(JsonObject json, BiomeTextureMap data) {
		for(Entry<Biome, TextureSet> entry : data.biomeTextureMap.entrySet()) {
			int biomeID = Biome.getIdForBiome(entry.getKey());
			if (biomeID >= 0 && (AntiqueAtlasMod.instance.jeidPresent || biomeID < 256)) {
				json.addProperty(entry.getKey().getRegistryName().toString(), entry.getValue().name);
			}
		}
	}
}
