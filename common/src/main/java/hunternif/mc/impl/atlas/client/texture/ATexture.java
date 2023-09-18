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

    public Identifier getTexture() {
        return texture;
    }

    public void bind() {
        RenderSystem.setShaderTexture(0, texture);
    }

    public void draw(DrawContext matrices, int x, int y) {
        draw(matrices, x, y, width(), height());
    }

    public void draw(DrawContext drawContext, int x, int y, int width, int height) {
        draw(drawContext, x, y, width, height, 0, 0, this.width(), this.height());
    }

    public void draw(DrawContext drawContext, int x, int y, int u, int v, int regionWidth, int regionHeight) {
        draw(drawContext, x, y, regionWidth, regionHeight, u, v, regionWidth, regionHeight);
    }

    public void draw(DrawContext drawContext, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight) {
        if (autobind) {
            bind();
        }
        drawContext.drawTexture(this.getTexture(), x, y, width, height, u, v, regionWidth, regionHeight, this.width(), this.height());
    }

    public void drawCenteredWithRotation(DrawContext drawContext, int x, int y, int width, int height, float rotation) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180 + rotation));
        matrices.translate(-width / 2f, -height / 2f, 0f);

        draw(drawContext, 0,0, width, height);

        matrices.pop();
    }

    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrixStack, int x, int y, int width, int height, int light) {
        drawWithLight(consumer, matrixStack, x, y, width, height, 0, 0, this.width(), this.height(), light);
    }

    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrixStack, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light) {
        if (autobind) {
            bind();
        }
        drawTexturedQuadWithLight(consumer, matrixStack, x, x + width, y, y + height, (u + 0.0F) / (float) this.width(), (u + (float) regionWidth) / (float) this.width(), (v + 0.0F) / (float) this.height(), (v + (float) regionHeight) / (float) this.height(), light);
    }

    private void drawTexturedQuadWithLight(VertexConsumerProvider vertexConsumer, MatrixStack matrixStack, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int light) {
        VertexConsumer consumer = vertexConsumer.getBuffer(this.LAYER);
        Matrix4f matrices = matrixStack.peek().getPositionMatrix();
        consumer.vertex(matrices, (float) x0, (float) y1, 0f).color(255, 255, 255, 255).texture(u0, v1).light(light).next();
        consumer.vertex(matrices, (float) x1, (float) y1, 0f).color(255, 255, 255, 255).texture(u1, v1).light(light).next();
        consumer.vertex(matrices, (float) x1, (float) y0, 0f).color(255, 255, 255, 255).texture(u1, v0).light(light).next();
        consumer.vertex(matrices, (float) x0, (float) y0, 0f).color(255, 255, 255, 255).texture(u0, v0).light(light).next();
    }
}
