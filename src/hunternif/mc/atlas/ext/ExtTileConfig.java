package hunternif.mc.atlas.ext;

import static argo.jdom.JsonNodeBuilders.aStringBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.FileUtil;

import java.io.File;
import java.util.Map.Entry;

import argo.jdom.JsonNode;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

/**
 * Maps unique names of external tiles to pseudo-biome IDs.
 * @author Hunternif
 */
public class ExtTileConfig {
	private final File file;
	
	public ExtTileConfig(File file) {
		this.file = file;
	}
	
	public void load() {
		JsonRootNode root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("No tileIDs config found");
			return;
		}
		
		for (Entry<JsonStringNode, JsonNode> entry : root.getFields().entrySet()) {
			try {
				String name = entry.getKey().getText();
				int id = Integer.parseInt(entry.getValue().getText());
				ExtTileIdMap.instance().setPseudoBiomeID(name, id);
			} catch (NumberFormatException e) {
				AntiqueAtlasMod.logger.severe("Malformed tileIDs config: " + e.toString());
			}
		}
	}
	
	public void save() {
		JsonObjectNodeBuilder builder = anObjectBuilder();
		for (Entry<String, Integer> entry : ExtTileIdMap.instance().getMap().entrySet()) {
			builder.withField(entry.getKey(), aStringBuilder(entry.getValue().toString()));
		}
		JsonRootNode root = builder.build();
		FileUtil.writeJson(root, file);
	}
}
