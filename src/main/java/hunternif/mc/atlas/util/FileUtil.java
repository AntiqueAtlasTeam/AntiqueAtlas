package hunternif.mc.atlas.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class FileUtil {
	private static final JsonParser parser = new JsonParser();
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	/** Parse the specified file. Returns null if the file is not found or
	 * cannot be parsed correctly. */
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
		} catch (JsonIOException e) {
			Log.error(e, "Error parsing file %s", file.getName());
		} catch (JsonSyntaxException e) {
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

	/** Pretty-print JSON root node in the specified text file. */
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
