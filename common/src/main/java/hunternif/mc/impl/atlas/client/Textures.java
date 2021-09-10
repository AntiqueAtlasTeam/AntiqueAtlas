package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.client.texture.IconTexture;
import hunternif.mc.impl.atlas.client.texture.Texture;
import hunternif.mc.impl.atlas.client.texture.TileTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class Textures {
    public final static Map<Identifier, ITexture> TILE_TEXTURES_MAP  = new HashMap<>();

    private static final String MOD_PREFIX = AntiqueAtlasMod.ID + ":";
    private static final String GUI = MOD_PREFIX + "textures/gui/";
    private static final String GUI_ICONS = GUI + "icons/";
    private static final String GUI_SCALEBAR = GUI + "scalebar/";

    public static final ITexture
            BOOK = gui("book.png", 310, 218),
            BOOK_FRAME = gui("book_frame.png", 310, 218),
            BOOK_FRAME_NARROW = gui("book_frame_narrow.png", 310, 218),
            BTN_ARROWS = gui("navigate_arrows.png", 24, 24),
            BTN_POSITION = gui("position.png", 24, 24),
            BOOKMARKS = gui("bookmarks.png", 84, 36),
            BOOKMARKS_LEFT = gui("bookmarks_l.png", 84, 36),
            PLAYER = gui("player.png", 7, 8),
            SCROLLBAR_HOR = gui("scrollbar_hor.png", 8, 7),
            SCROLLBAR_VER = gui("scrollbar_ver.png", 7,8),
            MARKER_FRAME_ON = gui("marker_frame_on.png", 34, 34),
            MARKER_FRAME_OFF = gui("marker_frame_off.png", 34, 34),
            ERASER = gui("eraser.png", 24, 24),

            SCALEBAR_4 = scaleBar("scalebar_4.png"),
            SCALEBAR_8 = scaleBar("scalebar_8.png"),
            SCALEBAR_16 = scaleBar("scalebar_16.png"),
            SCALEBAR_32 = scaleBar("scalebar_32.png"),
            SCALEBAR_64 = scaleBar("scalebar_64.png"),
            SCALEBAR_128 = scaleBar("scalebar_128.png"),
            SCALEBAR_256 = scaleBar("scalebar_256.png"),
            SCALEBAR_512 = scaleBar("scalebar_512.png"),

            ICON_ADD_MARKER = icon("add_marker.png"),
            ICON_DELETE_MARKER = icon("del_marker.png"),
            ICON_SHOW_MARKERS = icon("show_markers.png"),
            ICON_HIDE_MARKERS = icon("hide_markers.png"),
            ICON_EXPORT = icon("export.png");

    public static final Identifier EXPORTED_BG = new Identifier(GUI + "exported_bg.png");

    // Constructor helpers:
    private static ITexture gui(String fileName, int width, int height) {
        return new Texture(new Identifier(GUI + fileName), width, height);
    }

    private static ITexture scaleBar(String fileName) {
        return new Texture(new Identifier(GUI_SCALEBAR + fileName), 20, 8);
    }

    private static ITexture icon(String fileName) {
        return new IconTexture(new Identifier(GUI_ICONS + fileName));
    }
}
