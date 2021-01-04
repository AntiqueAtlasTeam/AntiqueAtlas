package hunternif.mc.impl.atlas.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;

/**
 * Basic JSON config that handles format versions and empty or malformed files.
 *
 * @author Hunternif
 */
public abstract class AbstractJSONConfig<T extends SaveData> implements Config<T> {
    private final File file;

    protected AbstractJSONConfig(File file) {
        this.file = file;
    }

    @Override
    public void load(T data) {
        JsonElement root = FileUtil.readJson(file);
        if (root == null) {
            Log.info("Config %s not found; creating new", file.getName());
            save(data);
            return;
        }
        try {
            JsonElement versionElem = root.getAsJsonObject().get("version");
            JsonObject jsonData;
            int version;
            boolean outdated = false;
            if (versionElem == null) {
                Log.warn("Outdated config %s", file.getName());
                version = 0;
                // Non-existent version means the whole file is just data:
                jsonData = root.getAsJsonObject();
                outdated = true;
            } else {
                version = versionElem.getAsInt();
                if (currentVersion() > version) {
                    Log.warn("Outdated config %s: version was %d, but current is %d",
                            file.getName(), version, currentVersion());
                    outdated = true;
                }
                JsonElement jsonElem = root.getAsJsonObject().get("data");
                if (jsonElem == null) {
                    Log.error("Malformed config " + file.getName());
                    return;
                }
                jsonData = jsonElem.getAsJsonObject();
            }

            loadData(jsonData, data, version);

            if (outdated) {
                save(data);
            }
        } catch (IllegalStateException | NumberFormatException e) {
            Log.error(e, "Malformed config %s", file.getName());
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
     *
     * @param json    JsonObject to load from
     * @param data    data instance to load into
     * @param version version of the json file
     */
    protected abstract void loadData(JsonObject json, T data, int version);

    /**
     * Write data to a JsonObject (using the latest version).
     *
     * @param json JsonObject to write to
     * @param data data to save
     */
    protected abstract void saveData(JsonObject json, T data);

    /**
     * Latest version of the config format. This version is what is going to
     * get written into file, but previous versions may still be read.
     */
    protected abstract int currentVersion();
}
