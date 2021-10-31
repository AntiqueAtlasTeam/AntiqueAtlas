package hunternif.mc.impl.atlas.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
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

    private final RenderType LAYER;

    public ATexture(ResourceLocation texture) {
        this(texture, true);
    }

    public ATexture(ResourceLocation texture, boolean autobind) {
        this.texture = texture;
        this.autobind = autobind;
        this.LAYER = RenderType.text(texture);
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public void bind() {
//        Minecraft.getInstance().getTextureManager().bindTexture(this.texture);
        RenderSystem.setShaderTexture(0, texture);
    }

    public void draw(PoseStack matrices, int x, int y) {
        draw(matrices, x, y, width(), height());
    }

    public void draw(PoseStack matrices, int x, int y, int width, int height) {
        draw(matrices, x, y, width, height, 0, 0, this.width(), this.height());
    }

    public void draw(PoseStack matrices, int x, int y, int u, int v, int regionWidth, int regionHeight) {
        draw(matrices, x, y, regionWidth, regionHeight, u, v, regionWidth, regionHeight);
    }

    public void draw(PoseStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight) {
        if (autobind) {
            bind();
        }
        GuiComponent.blit(matrices, x, y, width, height, u, v, regionWidth, regionHeight, this.width(), this.height());
    }

    public void drawWithLight(MultiBufferSource consumer, PoseStack matrices, int x, int y, int width, int height, int light) {
        drawWithLight(consumer, matrices, x, y, width, height, 0, 0, this.width(), this.height(), light);
    }

    public void drawWithLight(MultiBufferSource consumer, PoseStack matrices, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int light) {
        if (autobind) {
            bind();
        }
        drawTexturedQuadWithLight(consumer, matrices.last().pose(), x, x + width, y, y + height, (u + 0.0F) / (float) this.width(), (u + (float) regionWidth) / (float) this.width(), (v + 0.0F) / (float) this.height(), (v + (float) regionHeight) / (float) this.height(), light);
    }

    private void drawTexturedQuadWithLight(MultiBufferSource vertexConsumer, Matrix4f matrices, int x0, int x1, int y0, int y1, float u0, float u1, float v0, float v1, int light) {
        VertexConsumer consumer = vertexConsumer.getBuffer(this.LAYER);
        consumer.vertex(matrices, (float) x0, (float) y1, 0f).color(255, 255, 255, 255).uv(u0, v1).uv2(light).endVertex();
        consumer.vertex(matrices, (float) x1, (float) y1, 0f).color(255, 255, 255, 255).uv(u1, v1).uv2(light).endVertex();
        consumer.vertex(matrices, (float) x1, (float) y0, 0f).color(255, 255, 255, 255).uv(u1, v0).uv2(light).endVertex();
        consumer.vertex(matrices, (float) x0, (float) y0, 0f).color(255, 255, 255, 255).uv(u0, v0).uv2(light).endVertex();
    }
}
