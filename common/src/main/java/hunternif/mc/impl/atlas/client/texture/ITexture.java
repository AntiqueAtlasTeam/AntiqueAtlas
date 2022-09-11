package hunternif.mc.impl.atlas.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * A generic interface for textures. The texture know their own width and height.
 * All method parameters are provided in pixels.
 */
@Environment(EnvType.CLIENT)
public interface ITexture {

    /**
     * Returns path of the texture used for drawing
     *
     * @return the physical path to the image file used as texture
     */
    Identifier getTexture();

    int width();

    int height();

    void bind();

    void draw(MatrixStack matrices, int x, int y);

    void draw(MatrixStack matrices, int x, int y, int width, int height);

    void draw(MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight);

    void draw(MatrixStack matrices, int x, int y, int u, int v, int regionWidth, int regionHeight);

    void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int light);

    void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light);
}
