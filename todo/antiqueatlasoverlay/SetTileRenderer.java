package kenkron.antiqueatlasoverlay;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;

/**
 * The minimap render is a bit slow.  The function that really takes time is
 * AtlasRenderHelper.drawAutotileCorner(...).  This class makes it faster by
 * sorting the draw commands by texture, then
 * rendering all of the same textures of a map at once without re-binding.
 */
class SetTileRenderer {

    private final HashMap<ResourcePackStatusC2SPacket, ArrayList<TileCorner>> subjects = new HashMap<>();
    private int tileHalfSize = 8;

    public SetTileRenderer(int tileHalfSize) {
        this.tileHalfSize = tileHalfSize;
    }

    public void addTileCorner(ResourcePackStatusC2SPacket texture, int x, int y, int u, int v) {
        ArrayList<TileCorner> set = subjects.computeIfAbsent(texture, k -> new ArrayList<>());
        set.add(new TileCorner(x, y, u, v));
    }

    public void draw() {
        for (ResourcePackStatusC2SPacket key : subjects.keySet()) {
            ArrayList<TileCorner> tca = subjects.get(key);
            //Effectively a call to GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_94277_0_);
            MinecraftClient.getInstance().textureManager.XX_1_12_2_a_XX(key);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder renderer = tessellator.getBufferBuilder();
            renderer.begin(GL11.GL_QUADS, ddk.XX_1_12_2_g_XX);
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
        BufferBuilder renderer = tessellator.getBufferBuilder();
        renderer.postNormal(x + tileHalfSize, y + tileHalfSize, 0).a(maxU, maxV).d();
        renderer.postNormal(x + tileHalfSize, y, 0).a(maxU, minV).d();
        renderer.postNormal(x, y, 0).a(minU, minV).d();
        renderer.postNormal(x, y + tileHalfSize, 0).a(minU, maxV).d();
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
