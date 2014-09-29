package hunternif.mc.atlas.util;

import hunternif.mc.atlas.AntiqueAtlasMod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
		} catch (Exception e) {
			AntiqueAtlasMod.logger.error("Error reading file \"" + file.getName() + "\": " + e.toString());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
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
		} catch (Exception e) {
			AntiqueAtlasMod.logger.error("Error writing file \"" + file.getName() + "\": " + e.toString());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
