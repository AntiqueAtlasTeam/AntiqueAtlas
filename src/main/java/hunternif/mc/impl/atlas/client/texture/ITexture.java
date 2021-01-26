package hunternif.mc.impl.atlas.client.texture;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A generic interface for textures. The texture know their own width and height.
 * All method parameters are provided in pixels.
 */
@OnlyIn(Dist.CLIENT)
public interface ITexture {

    ResourceLocation getTexture();

    int width();

    int height();

    void bind();

    void draw(MatrixStack matrices, int x, int y);

    void draw(MatrixStack matrices, int x, int y, int width, int height);

    void draw(MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight);

    void draw(MatrixStack matrices, int x, int y, int u, int v, int regionWidth, int regionHeight);
}