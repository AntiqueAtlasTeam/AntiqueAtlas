package kenkron.antiqueatlasoverlay;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

/**
 * The minimap render is a bit slow.  The function that really takes time is
 * AtlasRenderHelper.drawAutotileCorner(...).  This class makes it faster by
 * sorting the draw commands by texture, then
 * rendering all of the same textures of a map at once without re-binding.
 */
@Environment(EnvType.CLIENT)
class SetTileRenderer extends DrawableHelper {

    private final HashMap<Identifier, ArrayList<TileCorner>> subjects = new HashMap<>();
    private final MatrixStack matrices;
    private final int tileHalfSize;

    public SetTileRenderer(MatrixStack matrices, int tileHalfSize) {
        this.matrices = matrices;
        this.tileHalfSize = tileHalfSize;
    }

    public void addTileCorner(Identifier texture, int x, int y, int u, int v) {
        ArrayList<TileCorner> set = subjects.computeIfAbsent(texture, k -> new ArrayList<>());
        set.add(new TileCorner(x, y, u, v));
    }

    public void draw() {
        for (Identifier key : subjects.keySet()) {
            ArrayList<TileCorner> tca = subjects.get(key);

            MinecraftClient.getInstance().getTextureManager().bindTexture(key);

            for (TileCorner tc : tca) {
                drawInlineAutotileCorner(tc.x, tc.y, tc.u, tc.v);
            }
        }
    }

    private void drawInlineAutotileCorner(int x, int y, int u, int v) {
        // This is dumb. But because their drawn four at a time, these chunks prevent rendering outside of our map
        if ((x + tileHalfSize) <= 240 && (x - tileHalfSize >= 0) && (y + tileHalfSize) < 166 && (y - tileHalfSize) >= 0) {
            drawTexture(this.matrices, x, y, tileHalfSize, tileHalfSize, u, v, 1, 1, 4, 6);
        }
    }

    public class TileCorner {
        final int x, y, u, v;

        TileCorner(int x, int y, int u, int v) {
            this.x = x;
            this.y = y;
            this.u = u;
            this.v = v;
        }
    }
}
