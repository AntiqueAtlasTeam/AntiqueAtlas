package hunternif.mc.atlas.util;

import hunternif.mc.atlas.AntiqueAtlasMod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;

public class FileUtil {
	private static final JdomParser parser = new JdomParser();
	private static final JsonFormatter formatter = new PrettyJsonFormatter();
	
	/** Parse the specified file. Returns null if the file is not found or
	 * cannot be parsed correctly. */
	public static JsonRootNode readJson(File file) {
		JsonRootNode root = null;
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
			AntiqueAtlasMod.logger.severe("Error reading file \"" + file.getName() + "\": " + e.toString());
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
	public static void writeJson(JsonRootNode root, File file) {
		BufferedWriter writer = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(formatter.format(root));
		} catch (Exception e) {
			AntiqueAtlasMod.logger.severe("Error writing file \"" + file.getName() + "\": " + e.toString());
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