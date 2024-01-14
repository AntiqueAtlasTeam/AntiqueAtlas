package hunternif.mc.impl.atlas.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

/**
 * An abstract base class, which implements the ITexture interface using
 * the DrawHelper.drawTexture method provided by minecraft code.
 */
@Environment(EnvType.CLIENT)
public abstract class ATexture implements ITexture {
    final Identifier texture;
    final boolean autobind;

    private final RenderLayer LAYER;

    public ATexture(Identifier texture) {
        this(texture, true);
    }

    public ATexture(Identifier texture, boolean autobind) {
        this.texture = texture;
        this.autobind = autobind;
        this.LAYER = RenderLayer.getText(texture);
    }

    @Override
    public Identifier getTexture() {
        return texture;
    }

    @Override
    public void bind() {
        RenderSystem.setShaderTexture(0, texture);
    }

    @Override
    public void draw(DrawContext context, int x, int y) {
        draw(context, x, y, width(), height());
    }

    @Override
    public void draw(DrawContext context, int x, int y, int width, int height) {
        draw(context, x, y, width, height, 0, 0, this.width(), this.height());
    }

    @Override
    public void draw(DrawContext context, int x, int y, int u, int v, int regionWidth, int regionHeight) {
        draw(context, x, y, regionWidth, regionHeight, u, v, regionWidth, regionHeight);
    }

    @Override
    public void draw(DrawContext context, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight) {
        if (autobind) {
            bind();
        }
        context.drawTexture(getTexture(), x, y, width, height, u, v, regionWidth, regionHeight, this.width(), this.height());
    }

    @Override
    public void drawCenteredWithRotation(DrawContext context, int x, int y, int width, int height, float rotation) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180 + rotation));
        context.getMatrices().translate(-width / 2f, -height / 2f, 0f);

        draw(context, 0,0, width, height);

        context.getMatrices().pop();
    }

    @Override
    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int light) {
        drawWithLight(consumer, matrices, x, y, width, height, 0, 0, this.width(), this.height(), light);
    }

    @Override
    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light) {
        if (autobind) {
            bind();
        }
        drawTexturedQuadWithLight(consumer, matrices.peek().getPositionMatrix(), x, x + width, y, y + height, (u + 0.0F) / (float) this.width(), (u + (float) regionWidth) / (float) this.width(), (v + 0.0F) / (float) this.height(), (v + (float) regionHeight) / (float) this.height(), light);
    }

    private void drawTexturedQuadWithLight(VertexConsumerProvider vertexConsumer, Matrix4f matrices, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int light) {
        VertexConsumer consumer = vertexConsumer.getBuffer(this.LAYER);
        consumer.vertex(matrices, (float) x0, (float) y1, 0f).color(255, 255, 255, 255).texture(u0, v1).light(light).next();
        consumer.vertex(matrices, (float) x1, (float) y1, 0f).color(255, 255, 255, 255).texture(u1, v1).light(light).next();
        consumer.vertex(matrices, (float) x1, (float) y0, 0f).color(255, 255, 255, 255).texture(u1, v0).light(light).next();
        consumer.vertex(matrices, (float) x0, (float) y0, 0f).color(255, 255, 255, 255).texture(u0, v0).light(light).next();
    }
}
