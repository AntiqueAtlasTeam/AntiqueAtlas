package hunternif.mc.atlas.core;

import static argo.jdom.JsonNodeBuilders.aStringBuilder;
import static argo.jdom.JsonNodeBuilders.anArrayBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.StandardTextureSet;
import hunternif.mc.atlas.core.BiomeTextureMap.BiomeTextureEntry;
import hunternif.mc.atlas.util.FileUtil;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;
import argo.jdom.JsonArrayNodeBuilder;
import argo.jdom.JsonNode;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
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
		JsonRootNode root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("No texture config found");
			return;
		}
		
		for (Entry<JsonStringNode, JsonNode> entry : root.getFields().entrySet()) {
			try {
				int biomeID = Integer.parseInt(entry.getKey().getText());
				if (entry.getValue().isArrayNode()) {
					// List of textures:
					List<JsonNode> paths = entry.getValue().getArrayNode();
					for (JsonNode path : paths) {
						ResourceLocation texture = new ResourceLocation(path.getText());
						BiomeTextureMap.instance().setTexture(biomeID, texture);
					}
					AntiqueAtlasMod.logger.info("Registered custom texture for biome " + biomeID);
				} else {
					// Standard texture set:
					String textureSetName = entry.getValue().getText();
					if (StandardTextureSet.contains(textureSetName)) {
						BiomeTextureMap.instance().setTexture(biomeID, StandardTextureSet.valueOf(textureSetName));
					}
					AntiqueAtlasMod.logger.info("Registered standard texture set for biome " + biomeID);
				}
			} catch (NumberFormatException e) {
				AntiqueAtlasMod.logger.severe("Malformed texture config: " + e.toString());
			}
		}
	}
	
	public void save() {
		JsonObjectNodeBuilder builder = anObjectBuilder();
		for (BiomeTextureEntry entry : BiomeTextureMap.instance().textureMap.values()) {
			if (entry.isStandardSet()) {
				builder.withField(String.valueOf(entry.biomeID), aStringBuilder(entry.textureSet.name()));
			} else {
				JsonArrayNodeBuilder arrayBuilder = anArrayBuilder();
				for (ResourceLocation texture : entry.textures) {
					arrayBuilder.withElement(aStringBuilder(texture.toString()));
				}
				builder.withField(String.valueOf(entry.biomeID), arrayBuilder);
			}
		}
		JsonRootNode root = builder.build();
		FileUtil.writeJson(root, file);
	}
}
