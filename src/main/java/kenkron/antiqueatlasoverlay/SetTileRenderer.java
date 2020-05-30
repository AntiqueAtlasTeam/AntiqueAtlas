package kenkron.antiqueatlasoverlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The minimap render is a bit slow.  The function that really takes time is
 * AtlasRenderHelper.drawAutotileCorner(...).  This class makes it faster by
 * sorting the draw commands by texture, then
 * rendering all of the same textures of a map at once without re-binding.
 */
class SetTileRenderer {

    private final HashMap<ResourceLocation, ArrayList<TileCorner>> subjects = new HashMap<>();
    private int tileHalfSize = 8;

    public SetTileRenderer(int tileHalfSize) {
        this.tileHalfSize = tileHalfSize;
    }

    public void addTileCorner(ResourceLocation texture, int x, int y, int u, int v) {
        ArrayList<TileCorner> set = subjects.computeIfAbsent(texture, k -> new ArrayList<>());
        set.add(new TileCorner(x, y, u, v));
    }

    public void draw() {
        for (ResourceLocation key : subjects.keySet()) {
            ArrayList<TileCorner> tca = subjects.get(key);
            //Effectively a call to GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_94277_0_);
            Minecraft.getInstance().getTextureManager().bindTexture(key);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder renderer = tessellator.getBuffer();
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            for (TileCorner tc : tca) {
                drawInlineAutotileCorner(tc.x, tc.y, tc.u, tc.v);
            }
            tessellator.draw();
        }
    }

    private void drawInlineAutotileCorner(int x, int y, int u, int v) {
        float minU = u / 4f;
        float maxU = (u + 1) / 4f;
        float minV = v / 6f;
        float maxV = (v + 1) / 6f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.pos(x + tileHalfSize, y + tileHalfSize, 0).tex(maxU, maxV).endVertex();
        renderer.pos(x + tileHalfSize, y, 0).tex(maxU, minV).endVertex();
        renderer.pos(x, y, 0).tex(minU, minV).endVertex();
        renderer.pos(x, y + tileHalfSize, 0).tex(minU, maxV).endVertex();
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
