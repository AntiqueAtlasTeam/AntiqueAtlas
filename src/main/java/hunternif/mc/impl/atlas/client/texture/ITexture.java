package hunternif.mc.impl.atlas.client.texture;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A generic interface for textures. The texture know their own width and height.
 * All method parameters are provided in pixels.
 */
@OnlyIn(Dist.CLIENT)
public interface ITexture {

    /**
     * Returns path of the texture used for drawing
     *
     * @return the physical path to the image file used as texture
     */
    ResourceLocation getTexture();

    int width();

    int height();

    void bind();

    void draw(PoseStack matrices, int x, int y);

    void draw(PoseStack matrices, int x, int y, int width, int height);

    void draw(PoseStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight);

    void draw(PoseStack matrices, int x, int y, int u, int v, int regionWidth, int regionHeight);

    void drawWithLight(MultiBufferSource consumer, PoseStack matrices, int x, int y, int width, int height, int light);

    void drawWithLight(MultiBufferSource consumer, PoseStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light);
}
