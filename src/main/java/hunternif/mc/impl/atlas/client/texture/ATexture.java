package hunternif.mc.impl.atlas.client.texture;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An abstract base class, which implements the ITexture interface using
 * the DrawHelper.drawTexture method provided by minecraft code.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ATexture implements ITexture {
    final ResourceLocation texture;
    final boolean autobind;

    public ATexture(ResourceLocation texture) {
        this(texture, true);
    }

    public ATexture(ResourceLocation texture, boolean autobind) {
        this.texture = texture;
        this.autobind = autobind;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public void bind() {
        Minecraft.getInstance().getTextureManager().bindTexture(this.texture);
    }

    public void draw(MatrixStack matrices, int x, int y) {
        draw(matrices, x, y, width(), height());
    }

    public void draw(MatrixStack matrices, int x, int y, int width, int height) {
        draw(matrices, x, y, width, height, 0, 0, this.width(), this.height());
    }

    public void draw(MatrixStack matrices, int x, int y, int u, int v, int regionWidth, int regionHeight) {
        draw(matrices, x, y, regionWidth, regionHeight, u, v, regionWidth, regionHeight);
    }

    public void draw(MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight) {
        if (autobind) {
            bind();
        }
        AbstractGui.blit(matrices, x, y, width, height, u, v, regionWidth, regionHeight, this.width(), this.height());
    }
}