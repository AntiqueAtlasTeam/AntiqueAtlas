package hunternif.mc.impl.atlas.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

/**
 * An abstract base class, which implements the ITexture interface using
 * the DrawHelper.drawTexture method provided by minecraft code.
 */
@Environment(EnvType.CLIENT)
public abstract class ATexture implements ITexture {
    final Identifier texture;
    final boolean autobind;
    private boolean flipped;

    private final RenderLayer LAYER;

    public ATexture(Identifier texture) {
        this(texture, true);
    }

    public ATexture(Identifier texture, boolean autobind) {
        this(texture, autobind, false);
    }

    public ATexture(Identifier texture, boolean autobind, boolean flipped) {
        this.texture = texture;
        this.autobind = autobind;
        this.flipped = flipped;
        this.LAYER = RenderLayer.getText(texture);
    }

    public Identifier getTexture() {
        return texture;
    }

    public void bind() {
        RenderSystem.setShaderTexture(0, texture);
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
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, this.width(), this.height());
    }

    public void drawCenteredWithRotation(MatrixStack matrices, int x, int y, int width, int height, float rotation) {
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180 + rotation));
        matrices.translate(-width / 2f, -height / 2f, 0f);

        draw(matrices, 0,0, width, height);

        matrices.pop();
    }

    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int light) {
        drawWithLight(consumer, matrices, x, y, width, height, 0, 0, this.width(), this.height(), light);
    }

    public void drawWithLight(VertexConsumerProvider consumer, MatrixStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light) {
        if (autobind) {
            bind();
        }
        drawTexturedQuadWithLight(consumer, matrices.peek().getPositionMatrix(), x, x + width, y, y + height, (u + 0.0F) / (float) this.width(), (u + (float) regionWidth) / (float) this.width(), (v + 0.0F) / (float) this.height(), (v + (float) regionHeight) / (float) this.height(), light);
    }

    private void drawTexturedQuadWithLight(VertexConsumerProvider vertexConsumer, Matrix4f matrices, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int light) {
        VertexConsumer consumer = vertexConsumer.getBuffer(this.LAYER);
        consumer.vertex(matrices, (float) x0, flipped ? y0 : y1, 0f).color(255, 255, 255, 255).texture(u0, flipped ? v0 : v1).light(light).next();
        consumer.vertex(matrices, (float) x1, flipped ? y0 : y1, 0f).color(255, 255, 255, 255).texture(u1, flipped ? v0 : v1).light(light).next();
        consumer.vertex(matrices, (float) x1, flipped ? y1 : y0, 0f).color(255, 255, 255, 255).texture(u1, flipped ? v1 : v0).light(light).next();
        consumer.vertex(matrices, (float) x0, flipped ? y1 : y0, 0f).color(255, 255, 255, 255).texture(u0, flipped ? v1 : v0).light(light).next();
    }
}
