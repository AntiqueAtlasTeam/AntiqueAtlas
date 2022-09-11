package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.client.texture.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The minimap render is a bit slow.  The function that really takes time is
 * AtlasRenderHelper.drawAutotileCorner(...).  This class makes it faster by
 * sorting the draw commands by texture, then
 * rendering all of the same textures of a map at once without re-binding.
 */
@Environment(EnvType.CLIENT)
class SetTileRenderer {

    private final HashMap<Identifier, ArrayList<TileCorner>> subjects = new HashMap<>();
    private final MatrixStack matrices;
    private final int tileHalfSize;
    private final int light;
    private final VertexConsumerProvider buffer;

    public SetTileRenderer(VertexConsumerProvider buffer, MatrixStack matrices, int tileHalfSize, int light) {
        this.matrices = matrices;
        this.tileHalfSize = tileHalfSize;
        this.light = light;
        this.buffer = buffer;
    }

    public void addTileCorner(Identifier texture, int x, int y, int u, int v) {
        ArrayList<TileCorner> set = subjects.computeIfAbsent(texture, k -> new ArrayList<>());
        set.add(new TileCorner(x, y, u, v));
    }

    public void draw() {
        for (Identifier key : subjects.keySet()) {
            ArrayList<TileCorner> tca = subjects.get(key);

            ITexture texture = new Texture(key, 4, 6, false);

            for (TileCorner tc : tca) {
                drawInlineAutotileCorner(texture, tc.x, tc.y, tc.u, tc.v);
            }
        }
    }

    private void drawInlineAutotileCorner(ITexture texture, int x, int y, int u, int v) {
        // This is dumb. But because there are drawn four at a time, these chunks prevent rendering outside of our map
        if ((x + tileHalfSize) <= 240 && (x - tileHalfSize >= 0) && (y + tileHalfSize) < 166 && (y - tileHalfSize) >= 0) {
            texture.drawWithLight(this.buffer, this.matrices, x, y, tileHalfSize, tileHalfSize, u, v, 1, 1, this.light);
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
