package hunternif.mc.atlas;

import blue.endless.jankson.Comment;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.impl.fiber.serialization.FiberSerialization;
import javafx.geometry.Pos;
import me.shedaniel.fiber2cloth.api.ClothSetting;
import net.fabricmc.loader.api.FabricLoader;

import javax.swing.text.Position;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Settings
public class SettingsConfig {
    @Setting.Group()
    public Gameplay gameplay;

    @Setting.Group()
    public UserInterface userInterface;

    @Setting.Group()
    public Performance performance;

    @Setting.Group()
    public OverlayPosition position;

    @Setting.Group()
    public OverlayAppearance appearance;


    //============= Gameplay settings =============
    public static class Gameplay {
        @Comment(value="Whether to remember last open browsing position and zoom level for each dimension in every atlas.\nIf disabled, all dimensions and all atlases will be \"synchronized\" at the same coordinates and zoom level, and map will \"follow\" player by default.")
        public boolean doSaveBrowsingPos = true;

        @Comment(value="Whether to add local marker for the spot where the player died.")
        public boolean autoDeathMarker = true;

        @Comment(value="Whether to add global markers for NPC villages.")
        public boolean autoVillageMarkers = true;

        @Comment(value="Whether to add global markers for Nether Portals.")
        public boolean autoNetherPortalMarkers = true;

        @Comment(value="Player will need to craft atlas item to use atlas.")
        public boolean itemNeeded = true;
    }

    //============ Interface settings =============
    public static class UserInterface {
        public boolean doScaleMarkers = false;

        @Comment("Default zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.")
        @Setting.Constrain.Range(min = 0.001953125, max = 16.0)
        public double defaultScale = 0.5f;

        @Comment("Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2. Smaller values may decrease performance!")
        @Setting.Constrain.Range(min = 0.001953125, max = 16.0)
        public double minScale = 1.0 / 32.0;

        @Comment("Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.")
        @Setting.Constrain.Range(min = 0.001953125, max = 16.0)
        public double maxScale = 4;

        @Comment("If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.\nIf true, then the direction is reversed.")
        public boolean doReverseWheelZoom = false;
    }

    //=========== Performance settings ============
    public static class Performance {
        @Comment("The radius of the area around the player which is scanned by the Atlas at regular intervals.\nNote that this will not force faraway chunks to load, unless force_chunk_loading is enabled.\nLower value gives better performance.")
        public int scanRadius = 11;

        @Comment("Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.\nEnabling this may SEVERELY decrease performance!")
        public boolean forceChunkLoading = false;

        @Comment("Time in seconds between two scans of the area.\nHigher value gives better performance.")
        public float newScanInterval = 1f;

        @Comment("Whether to rescan chunks in the area that have been previously mapped. This is useful in case of changes in coastline (including small ponds of water and lava), or if land disappears completely (for sky worlds).\nDisable for better performance.")
        public boolean doRescan = true;

        @Comment("The number of area scans between full rescans.\nHigher value gives better performance.")
        @Setting.Constrain.Range(min = 1, max = 1000)
        public int rescanRate = 4;

        @Comment("The maximum number of markers a particular atlas can hold.")
        @Setting.Constrain.Range(min = 0, max = 2147483647)
        public int markerLimit = 1024;

        @Comment("Whether to perform additional scanning to locate small ponds of water or lava.\nDisable for better performance.")
        public boolean doScanPonds = true;

        @Comment("Whether to perform additional scanning to locate ravines.\nDisable for better performance.")
        public boolean doScanRavines = true;

        @Comment("If true, map render time will be output.")
        public boolean debugRender = false;
    }

    public static class OverlayPosition {
        @Comment("If true, the map position's x axis will align 0 to the right\nof the screen, increasing towards the left.")
        public boolean alignRight = false;

        @Comment("If true, the map position's y axis will align 0 to the bottom\nof the screen, increasing towards the top.")
        public boolean alignBottom = false;

        @Comment("Map's minimum position along the x axis in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        public int xPosition = 2;

        @Comment("Map's minimum position along the y axis in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        @Setting.Constrain.Range(min = 0)
        public int yPosition = 2;

        @Comment("Map's width in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        @Setting.Constrain.Range(min = 0)
        public int width = GuiAtlas.WIDTH / 2;

        @Comment("Map's height in GUI pixels.\nNote that this will change with Minecraft's GUI scale configuration.")
        @Setting.Constrain.Range(min = 0)
        public int height = GuiAtlas.HEIGHT / 2;
    }

    public static class OverlayAppearance {
        @Comment("The size (in GUI pixels) of a map's tile.\nNote that this will change with Minecraft's GUI scale configuration.\nWhen using a small gui scale, the map may look better with a TILE_SIZE of 16 or more.")
        @Setting.Constrain.Range(min = 1, max = 10)
        public int tileSize = 8;

        @Comment("The size (in GUI pixels) of a marker on the map.\nNote that this will change with Minecraft's GUI scale configuration.")
        @Setting.Constrain.Range(min = 0)
        public int markerSize = GuiAtlas.MARKER_SIZE / 2;

        @Comment("The width (in GUI pixels) of the player's icon.")
        @Setting.Constrain.Range(min = 0)
        public int playerIconWidth = 14;

        @Comment("The height (in GUI pixels) of the player's icon.")
        @Setting.Constrain.Range(min = 0)
        public int playerIconHeight = 16;

        @Comment("The width of the map border on the left and right sides of the minimap tiles.\nRepresented as a fraction of the image width.\nBelow a certain threshold, this border will be overtaken by the map border graphic.")
        @Setting.Constrain.Range(min = 0.0, max = 0.5)
        public float borderX = 0.05F;

        @Comment("The width of the map border on the top and bottom sides of the minimap tiles.\nRepresented as a fraction of the image width.\nBelow a certain threshold, this border will be overtaken by the map border graphic.")
        @Setting.Constrain.Range(min = 0.0, max = 0.5)
        public float borderY = 0.05F;

        @Comment("If true, the minimap will show the map of an atlas only while it is held.\nIf false, the minimap will show the map of the first atlas in the hotbar.")
        public boolean requiresHold = true;

        @Comment("Set true to enable minimap")
        public boolean enabled = false;
    }


    protected SettingsConfig() {
        this.gameplay = new Gameplay();
        this.userInterface = new UserInterface();
        this.performance = new Performance();
        this.position = new OverlayPosition();
        this.appearance = new OverlayAppearance();
        AntiqueAtlasMod.CONFIG_TREE = ConfigTree.builder().withName(AntiqueAtlasMod.ID).applyFromPojo(this).build();
    }

    public static SettingsConfig deserialize() {
        SettingsConfig config = new SettingsConfig();
        File inputFile = new File(FabricLoader.getInstance().getConfigDirectory(), "antiqueatlas.json");

        try {
            FiberSerialization.deserialize(AntiqueAtlasMod.CONFIG_TREE, new FileInputStream(inputFile), new JanksonValueSerializer(false));
        } catch (IOException | ValueDeserializationException e) {
            e.printStackTrace();
        }

        return config;
    }

    public void serialize() {
        try {
            File outputFile = new File(FabricLoader.getInstance().getConfigDirectory(), "antiqueatlas.json");
            FiberSerialization.serialize(AntiqueAtlasMod.CONFIG_TREE, new FileOutputStream(outputFile), new JanksonValueSerializer(false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}