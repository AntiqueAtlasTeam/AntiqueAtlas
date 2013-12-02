package hunternif.mc.atlas.client;

import static argo.jdom.JsonNodeBuilders.aStringBuilder;
import static argo.jdom.JsonNodeBuilders.anArrayBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.BiomeTextureMap.BiomeTextureEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonArrayNodeBuilder;
import argo.jdom.JsonNode;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

public class Config {
	private static final JdomParser parser = new JdomParser();
	private static final JsonFormatter formatter = new PrettyJsonFormatter();
	
	private final File file;
	
	public Config(File file) {
		this.file = file;
	}
	
	public void load() {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			InputStream input = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(input);
			JsonRootNode root = parser.parse(reader);
			input.close();
			for (Entry<JsonStringNode, JsonNode> entry : root.getFields().entrySet()) {
				int biomeID = Integer.parseInt(entry.getKey().getText());
				if (entry.getValue().isArrayNode()) {
					// List of textures:
					List<JsonNode> paths = entry.getValue().getArrayNode();
					for (JsonNode path : paths) {
						String texture = path.getText();
						BiomeTextureMap.instance().addTexture(biomeID, texture);
					}
					AntiqueAtlasMod.logger.info("Registered custom texture for biome " + biomeID);
				} else {
					// Standard texture set:
					String textureSetName = entry.getValue().getText();
					if (StandardTextureSet.contains(textureSetName)) {
						BiomeTextureMap.instance().addTexture(biomeID, StandardTextureSet.valueOf(textureSetName));
					}
					AntiqueAtlasMod.logger.info("Registered standard texture set for biome " + biomeID);
				}
			}
		} catch (Exception e) {
			AntiqueAtlasMod.logger.warning("Error reading texture config: " + e.toString());
		}
	}
	
	public void save() {
		try {
			JsonObjectNodeBuilder builder = anObjectBuilder();
			for (BiomeTextureEntry entry : BiomeTextureMap.instance().textureMap.values()) {
				if (entry.isStandardSet()) {
					builder.withField(String.valueOf(entry.biomeID), aStringBuilder(entry.textureSet.name()));
				} else {
					JsonArrayNodeBuilder arrayBuilder = anArrayBuilder();
					for (String texture : entry.textures) {
						arrayBuilder.withElement(aStringBuilder(texture.toString()));
					}
					builder.withField(String.valueOf(entry.biomeID), arrayBuilder);
				}
			}
			JsonRootNode root = builder.build();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(formatter.format(root));
			writer.close();
		} catch (Exception e) {
			AntiqueAtlasMod.logger.warning("Error writing texture config: " + e.toString());
		}
	}
}
