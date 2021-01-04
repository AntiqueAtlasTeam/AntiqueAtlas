package hunternif.mc.impl.atlas.util;

import com.google.gson.*;

import java.io.*;

class FileUtil {
    private static final JsonParser parser = new JsonParser();
    private static final com.google.gson.Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Parse the specified file. Returns null if the file is not found or
     * cannot be parsed correctly.
     */
    public static JsonElement readJson(File file) {
        JsonElement root = null;
        InputStream input = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                return null;
            }
            input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);
            root = parser.parse(reader);
        } catch (FileNotFoundException e) {
            Log.error(e, "Error creating file %s", file.getName());
        } catch (IOException e) {
            Log.error(e, "Error opening file %s", file.getName());
        } catch (JsonIOException | JsonSyntaxException e) {
            Log.error(e, "Error parsing file %s", file.getName());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.error(e, "Error reading file %s", file.getName());
                }
            }
        }
        return root;
    }

    /**
     * Pretty-print JSON root node in the specified text file.
     */
    public static void writeJson(JsonElement root, File file) {
        BufferedWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            gson.toJson(root, writer);
        } catch (IOException e) {
            Log.error(e, "Error opening file %s", file.getName());
        } catch (JsonIOException e) {
            Log.error(e, "Error writing file %s", file.getName());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.error(e, "Error writing file %s", file.getName());
                }
            }
        }
    }
}
