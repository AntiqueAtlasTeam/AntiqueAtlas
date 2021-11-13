package hunternif.mc.impl.atlas.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

class ProgressBarOverlay {
    /**
     * Total width of the progress bar.
     */
    private final int barWidth;

    /**
     * Total height of the progress bar.
     */
    private final int barHeight;

    private final Font textRenderer;

    @OnlyIn(Dist.CLIENT)
    public ProgressBarOverlay(int barWidth, int barHeight) {
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        textRenderer = Minecraft.getInstance().font;
    }

    /**
     * Render progress bar on the screen.
     */
    @OnlyIn(Dist.CLIENT)
    public void draw(PoseStack matrices, int x, int y) {
        ExportUpdateListener l = ExportUpdateListener.INSTANCE;

        int headerWidth = this.textRenderer.width(l.header);
        this.textRenderer.draw(matrices, l.header, x + (barWidth - headerWidth) / 2F, y - 14, 0xffffff);
        int statusWidth = this.textRenderer.width(l.status);
        this.textRenderer.draw(matrices, l.status, x + (barWidth - statusWidth) / 2F, y, 0xffffff);
        y += 14;

        double p = l.currentProgress / l.maxProgress;
        if (l.maxProgress < 0)
            p = 0;

        RenderSystem.disableTexture();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder vb = tessellator.getBuilder();

        vb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        vb.vertex(x, y, 0).color(0.5f, 0.5f, 0.5f, 1).endVertex();
        vb.vertex(x, y + barHeight, 0).color(0.5f, 0.5f, 0.5f, 1).endVertex();
        vb.vertex(x + barWidth, y + barHeight, 0).color(0.5f, 0.5f, 0.5f, 1).endVertex();
        vb.vertex(x + barWidth, y, 0).color(0.5f, 0.5f, 0.5f, 1).endVertex();

        vb.vertex(x, y, 0).color(0.5f, 1, 0.5f, 1).endVertex();
        vb.vertex(x, y + barHeight, 0).color(0.5f, 1, 0.5f, 1).endVertex();
        vb.vertex(x + barWidth * p, y + barHeight, 0).color(0.5f, 1, 0.5f, 1).endVertex();
        vb.vertex(x + barWidth * p, y, 0).color(0.5f, 1, 0.5f, 1).endVertex();

        tessellator.end();

        RenderSystem.enableTexture();
    }

}
