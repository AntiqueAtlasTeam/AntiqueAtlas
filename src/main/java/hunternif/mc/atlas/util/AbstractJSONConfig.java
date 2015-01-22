package hunternif.mc.atlas.util;

import hunternif.mc.atlas.AntiqueAtlasMod;

import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Basic JSON config that supports versions, empty and malformed files.
 * @author Hunternif
 */
public abstract class AbstractJSONConfig<T extends SaveData> implements Config<T> {
	protected final File file;

	public AbstractJSONConfig(File file) {
		this.file = file;
	}
	
	@Override
	public void load(T data) {
		JsonElement root = FileUtil.readJson(file);
		if (root == null) {
			AntiqueAtlasMod.logger.info("Config " + file.getName() + " not found; creating new");
			save(data);
			return;
		}
		try {
			JsonElement versionElem = root.getAsJsonObject().get("version");
			JsonObject jsonData;
			int version;
			boolean outdated = false;
			if (versionElem == null) {
				AntiqueAtlasMod.logger.warn("Outdated config " + file.getName());
				version = 0;
				// Non-existent version means the whole file is just data:
				jsonData = root.getAsJsonObject();
				outdated = true;
			} else {
				version = versionElem.getAsInt();
				if (currentVersion() > version) {
					AntiqueAtlasMod.logger.warn("Outdated config " + file.getName()
							+ ": version was " + version + ", but current is " + currentVersion());
					outdated = true;
				}
				JsonElement jsonElem = root.getAsJsonObject().get("data");
				if (jsonElem == null) {
					AntiqueAtlasMod.logger.error("Malformed config " + file.getName());
					return;
				}
				jsonData = jsonElem.getAsJsonObject();
			}
			
			loadData(jsonData, data, version);
			
			if (outdated) {
				save(data);
			}
		} catch (IllegalStateException e) {
			AntiqueAtlasMod.logger.error("Malformed config " + file.getName() + ": " + e.getMessage());
		} catch (NumberFormatException e) {
			AntiqueAtlasMod.logger.error("Malformed config " + file.getName() + ": " + e.getMessage());
		}
	}
	
	@Override
	public void save(T data) {
		JsonObject root = new JsonObject();
		root.addProperty("version", currentVersion());
		JsonObject jsonData = new JsonObject();
		saveData(jsonData, data);
		root.add("data", jsonData);
		FileUtil.writeJson(root, file);
	}
	
	/**
	 * Read data from a JsonObject using the appropriate version parser.
	 * @param json		JsonObject to load from
	 * @param data		data instance to load into
	 * @param version	version of the json file
	 */
	protected abstract void loadData(JsonObject json, T data, int version);
	
	/**
	 * Write data to a JsonObject (using the latest version).
	 * @param json		JsonObject to write to
	 * @param data		data to save
	 */
	protected abstract void saveData(JsonObject json, T data);
	
	/** Latest version of the config format. This version is what is going to
	 * get written into file, but previous versions may still be read. */
	public abstract int currentVersion();
}
