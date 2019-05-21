package kenkron.antiqueatlasoverlay;

import hunternif.mc.atlas.client.gui.GuiAtlas;

// TODO FABRIC

//@Config(modid = AntiqueAtlasOverlayMod.MODID, name = "antiqueatlas/atlas_overlay", category = "")
//@Mod.EventBusSubscriber(modid = AntiqueAtlasOverlayMod.MODID)
public class AAOConfig {

    //@Config.Comment({ "These settings change the location and size." })
    public static Position position = new Position();
    //@Config.Comment({ "These settings change what the map shows, or how it is shown." })
    public static Appearance appearance = new Appearance();

    public static class Position {
        //@Config.Comment({ "If true, the map position's x axis will align 0 to the right", "of the screen, increasing towards the left." })
        public boolean alignRight = true;
        //@Config.Comment({ "If true, the map position's y axis will align 0 to the bottom", "of the screen, increasing towards the top." })
        public boolean alignBottom = false;
        //@Config.Comment({ "Map's minimum position along the x axis in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration." })
        //@Config.RangeInt
        public int xPosition = 2;
        //@Config.Comment({ "Map's minimum position along the y axis in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration." })
        //@Config.RangeInt
        public int yPosition = 2;
        //@Config.Comment({ "Map's width in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration." })
        //@Config.RangeInt(min = 0)
        public int width = GuiAtlas.WIDTH / 2;
        //@Config.Comment({ "Map's height in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration." })
        //@Config.RangeInt(min = 0)
        public int height = GuiAtlas.HEIGHT / 2;
    }

    public static class Appearance {
        //@Config.Comment({ "The size (in GUI pixels) of a map's tile.", "Note that this will change with Minecraft's GUI scale configuration.", "When using a small gui scale, the map may look better with a TILE_SIZE of 16 or more." })
        //@Config.RangeInt(min = 1)
        public int tileSize = 8;
        //@Config.Comment({ "The size (in GUI pixels) of a marker on the map.", "Note that this will change with Minecraft's GUI scale configuration." })
        //@Config.RangeInt(min = 0)
        public int markerSize = GuiAtlas.MARKER_SIZE / 2;
        //@Config.Comment({ "The width (in GUI pixels) of the player's icon." })
        //@Config.RangeInt(min = 0)
        public int playerIconWidth = 7;
        //@Config.Comment({ "The height (in GUI pixels) of the player's icon." })
        //@Config.RangeInt(min = 0)
        public int playerIconHeight = 8;
        //@Config.Comment({ "The width of the map border on the left and right sides of the minimap tiles.", "Represented as a fraction of the image width.", "Below a certain threshold, this border will be overtaken by the map border graphic." })
        //@Config.RangeDouble(min = 0.0F, max = 0.5F)
        public float borderX = 0.05F;
        //@Config.Comment({ "The width of the map border on the top and bottom sides of the minimap tiles.", "Represented as a fraction of the image width.", "Below a certain threshold, this border will be overtaken by the map border graphic.\n" })
        //@Config.RangeDouble(min = 0.0F, max = 0.5F)
        public float borderY = 0.05F;
        //@Config.Comment({ "If true, the minimap will show the map of an atlas only while it is held.", "If false, the minimap will show the map of the first atlas in the hotbar." })
        public boolean requiresHold = true;
        //@Config.Comment({ "Set false to disable minimap" })
        public boolean enabled = true;
    }

    // TODO - Remove when Forge fixes it's own syncing
    //@SubscribeEvent
//    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
//        if (event.getModID().equals(AntiqueAtlasOverlayMod.MODID))
//            ConfigManager.sync(AntiqueAtlasOverlayMod.MODID, Config.Type.INSTANCE);
//    }
}
