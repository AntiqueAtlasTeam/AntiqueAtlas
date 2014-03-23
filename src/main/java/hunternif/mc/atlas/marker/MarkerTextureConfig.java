package hunternif.mc.atlas.marker;

import static argo.jdom.JsonNodeBuilders.aStringBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.FileUtil;

import java.io.File;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.util.ResourceLocation;
import argo.jdom.JsonNode;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

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
		JsonRootNode root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("No marker textures config found");
			return;
		}
		
		for (Entry<JsonStringNode, JsonNode> entry : root.getFields().entrySet()) {
			try {
				String markerType = entry.getKey().getText();
				ResourceLocation texture = new ResourceLocation(entry.getValue().getText());
				MarkerTextureMap.instance().setTexture(markerType, texture);
			} catch (NumberFormatException e) {
				AntiqueAtlasMod.logger.severe("Malformed marker textures config: " + e.toString());
			}
		}
	}
	
	public void save() {
		JsonObjectNodeBuilder builder = anObjectBuilder();
		for (Entry<String, ResourceLocation> entry : MarkerTextureMap.instance().getMap().entrySet()) {
			builder.withField(entry.getKey(), aStringBuilder(entry.getValue().toString()));
		}
		JsonRootNode root = builder.build();
		FileUtil.writeJson(root, file);
	}
}
