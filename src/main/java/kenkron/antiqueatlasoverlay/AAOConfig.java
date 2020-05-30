package kenkron.antiqueatlasoverlay;

import hunternif.mc.atlas.client.gui.GuiAtlas;

public class AAOConfig {

    public static Position position = new Position();
    public static Appearance appearance = new Appearance();

    public static void loadConfig() {
        //position = ConfigManager.loadConfig(Position.class);
        //appearance = ConfigManager.loadConfig(Appearance.class);
    }

    //@ConfigFile(name="antiqueatlas/overlay_position")
    public static class Position {
        //@Comment("If true, the map position's x axis will align 0 to the right\nof the screen, increasing towards the left.")
        public boolean alignRight = false;

        //@Comment("If true, the map position's y axis will align 0 to the bottom\nof the screen, increasing towards the top.")
        public boolean alignBottom = false;

        //@Comment("Map's minimum position along the x axis in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        //@Config.RangeInt
        public int xPosition = 2;

        //@Comment("Map's minimum position along the y axis in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        //@Config.RangeInt
        public int yPosition = 2;

        //@Comment("Map's width in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        //@Config.RangeInt(min = 0)
        public int width = GuiAtlas.WIDTH / 2;

        //@Comment("Map's height in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        //@Config.RangeInt(min = 0)
        public int height = GuiAtlas.HEIGHT / 2;
    }

    //@ConfigFile(name="antiqueatlas/overlay_appearance")
    public static class Appearance {
        //@Comment("The size (in GUI pixels) of a map's tile.\nNote that this will change with Minecraft's GUI scale configuration.\nWhen using a small gui scale, the map may look better with a TILE_SIZE of 16 or more.")
        //@Config.RangeInt(min = 1)
        public int tileSize = 8;

        //@Comment("The size (in GUI pixels) of a marker on the map.\nNote that this will change with Minecraft's GUI scale configuration.")
        //@Config.RangeInt(min = 0)
        public int markerSize = GuiAtlas.MARKER_SIZE / 2;

        //@Comment("The width (in GUI pixels) of the player's icon.")
        //@Config.RangeInt(min = 0)
        public int playerIconWidth = 7;

        //@Comment("The height (in GUI pixels) of the player's icon.")
        //@Config.RangeInt(min = 0)
        public int playerIconHeight = 8;

        //@Comment("The width of the map border on the left and right sides of the minimap tiles.\nRepresented as a fraction of the image width.\nBelow a certain threshold, this border will be overtaken by the map border graphic.")
        //@Config.RangeDouble(min = 0.0F, max = 0.5F)
        public float borderX = 0.05F;

        //@Comment("The width of the map border on the top and bottom sides of the minimap tiles.\nRepresented as a fraction of the image width.\nBelow a certain threshold, this border will be overtaken by the map border graphic.")
        //@Config.RangeDouble(min = 0.0F, max = 0.5F)
        public float borderY = 0.05F;

        //@Comment("If true, the minimap will show the map of an atlas only while it is held.\nIf false, the minimap will show the map of the first atlas in the hotbar.")
        public boolean requiresHold = true;

        //@Comment("Set true to enable minimap")
        public boolean enabled = false;
    }
}
