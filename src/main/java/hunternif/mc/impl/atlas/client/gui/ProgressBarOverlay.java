package hunternif.mc.impl.atlas.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

class ProgressBarOverlay {
    /**
     * Total width of the progress bar.
     */
    private final int barWidth;

    /**
     * Total height of the progress bar.
     */
    private final int barHeight;

    private final TextRenderer textRenderer;

    @Environment(EnvType.CLIENT)
    public ProgressBarOverlay(int barWidth, int barHeight) {
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    /**
     * Render progress bar on the screen.
     */
    @Environment(EnvType.CLIENT)
    public void draw(MatrixStack matrices, int x, int y) {
        ExportUpdateListener l = ExportUpdateListener.INSTANCE;

        int headerWidth = this.textRenderer.getWidth(l.header);
        this.textRenderer.draw(matrices, l.header, x + (barWidth - headerWidth) / 2F, y - 14, 0xffffff);
        int statusWidth = this.textRenderer.getWidth(l.status);
        this.textRenderer.draw(matrices, l.status, x + (barWidth - statusWidth) / 2F, y, 0xffffff);
        y += 14;

        double p = l.currentProgress / l.maxProgress;
        if (l.maxProgress < 0)
            p = 0;

        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();

        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        vb.vertex(x, y, 0).color(0.5f, 0.5f, 0.5f, 1).next();
        vb.vertex(x, y + barHeight, 0).color(0.5f, 0.5f, 0.5f, 1).next();
        vb.vertex(x + barWidth, y + barHeight, 0).color(0.5f, 0.5f, 0.5f, 1).next();
        vb.vertex(x + barWidth, y, 0).color(0.5f, 0.5f, 0.5f, 1).next();

        vb.vertex(x, y, 0).color(0.5f, 1, 0.5f, 1).next();
        vb.vertex(x, y + barHeight, 0).color(0.5f, 1, 0.5f, 1).next();
        vb.vertex(x + barWidth * p, y + barHeight, 0).color(0.5f, 1, 0.5f, 1).next();
        vb.vertex(x + barWidth * p, y, 0).color(0.5f, 1, 0.5f, 1).next();

        tessellator.draw();

        RenderSystem.enableTexture();
    }

}
