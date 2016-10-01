package kenkron.antiqueatlasoverlay;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class AAOConfig {

	private static final int VERSION = 1;

	public static final String POSITION = "Map Position";
	public static final String APPEARANCE = "Map Appearance";

	public static void load(File file, AAORenderEventReceiver receiver) {
		Configuration config = new Configuration(file, String.valueOf(VERSION));
		config.setCategoryComment(POSITION,
				"These settings change the location and size.");

		config.setCategoryComment(APPEARANCE,
				"These settings change what the map shows, or how it is shown.");

		receiver.ALIGN_RIGHT = config.getBoolean("ALIGN_RIGHT", POSITION,
				receiver.ALIGN_RIGHT,
				"If true, the map position's x axis will align 0 to the right\n"
						+ "of the screen, increasing towards the left.\n");

		receiver.ALIGN_BOTTOM = config.getBoolean("ALIGN_BOTTOM", POSITION,
				receiver.ALIGN_BOTTOM,
				"If true, the map position's y axis will align 0 to the bottom\n"
						+ "of the screen, increasing towards the top.\n");

		receiver.X = config.getInt("X", POSITION, receiver.X,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				"Map's minimum position along the x axis in GUI pixels.\n"
						+ "Note that this will change with Minecraft's "
						+ "GUI scale configuration.\n");

		receiver.Y = config.getInt("Y", POSITION, receiver.Y,
				Integer.MIN_VALUE, Integer.MAX_VALUE,
				"Map's minimum position along the y axis in GUI pixels.\n"
						+ "Note that this will change with Minecraft's "
						+ "GUI scale configuration.\n");

		receiver.WIDTH = config.getInt("WIDTH", POSITION, receiver.WIDTH, 0,
				Integer.MAX_VALUE, "Map's width in GUI pixels.\n"
						+ "Note that this will change with Minecraft's "
						+ "GUI scale configuration.\n");

		receiver.HEIGHT = config.getInt("HEIGHT", POSITION, receiver.HEIGHT, 0,
				Integer.MAX_VALUE, "Map's height in GUI pixels.\n"
						+ "Note that this will change with Minecraft's "
						+ "GUI scale configuration.\n");

		receiver.TILE_SIZE = config
				.getInt("TILE_SIZE",
						APPEARANCE,
						receiver.TILE_SIZE,
						1,
						Integer.MAX_VALUE,
						"The size (in GUI pixels) of a map's tile.\n"
								+ "Note that this will change with Minecraft's "
								+ "GUI scale configuration.\n"
								+ "When using a small gui scale, "
								+ "the map may look better with a TILE_SIZE of 16 or more.\n");

		receiver.MARKER_SIZE = config.getInt("MARKER_SIZE", APPEARANCE,
				receiver.MARKER_SIZE, 0, Integer.MAX_VALUE,
				"The size (in GUI pixels) of a marker on the map.\n"
						+ "Note that this will change with Minecraft's "
						+ "GUI scale configuration.\n");

		receiver.PLAYER_ICON_WIDTH = config.getInt("PLAYER_ICON_WIDTH", APPEARANCE,
				receiver.PLAYER_ICON_WIDTH, 0, Integer.MAX_VALUE,
				"The width (in GUI pixels) of the player's icon.\n");
		

		receiver.PLAYER_ICON_HEIGHT = config.getInt("PLAYER_ICON_HEIGHT", APPEARANCE,
				receiver.PLAYER_ICON_HEIGHT, 0, Integer.MAX_VALUE,
				"The height (in GUI pixels) of the player's icon.\n");
		
		receiver.BORDER_X = config
				.getFloat(
						"BORDER_X",
						APPEARANCE,
						receiver.BORDER_X,
						0,
						0.5f,
						"The width of the map border on the left and right "
								+ "sides of the minimap tiles.\n"
								+ "Represented as a fraction of the image width.\n"
								+ "Below a certain threshold, this border will be overtaken " +
								"by the map border graphic.\n");

		receiver.BORDER_Y = config
				.getFloat(
						"BORDER_Y",
						APPEARANCE,
						receiver.BORDER_X,
						0,
						0.5f,
						"The width of the map border on the top and bottom "
								+ "sides of the minimap tiles.\n"
								+ "Represented as a fraction of the image width.\n"
								+ "Below a certain threshold, this border will be overtaken " +
								"by the map border graphic.\n");

		receiver.REQUIRES_HOLD = config
				.getBoolean(
						"REQUIRES_HOLD",
						APPEARANCE,
						receiver.REQUIRES_HOLD,
						"If true, the minimap will show the map of an atlas only while it is held.\n"
								+ "If false, the minimap will show the map of the first atlas in the hotbar.\n");

		config.save();
	}
}
