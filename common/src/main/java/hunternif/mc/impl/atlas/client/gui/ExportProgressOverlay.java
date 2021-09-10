package hunternif.mc.impl.atlas.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import hunternif.mc.impl.atlas.util.ExportImageUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

public enum ExportProgressOverlay {
    INSTANCE;

    @Environment(EnvType.CLIENT)
    public void draw(MatrixStack matrices, int scaledWidth, int scaledHeight) {
        int x = scaledWidth - 40, y = scaledHeight - 20, barWidth = 50, barHeight = 2;

        ExportUpdateListener l = ExportUpdateListener.INSTANCE;

        if (!ExportImageUtil.isExporting) {
            return;
        }

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        int s = 2;

        GlStateManager.scaled(1.0 / s, 1.0 / s, 1);

        int headerWidth = font.getWidth(l.header);
        font.draw(matrices, l.header, (x) * s - headerWidth / 2F, (y) * s - 14, 0xffffff);
        int statusWidth = font.getWidth(l.status);
        font.draw(matrices, l.status, (x) * s - statusWidth / 2F, (y) * s, 0xffffff);

        GlStateManager.scaled(s, s, 1);
        y += 7;

        x -= barWidth / 2;
        double p = l.currentProgress / l.maxProgress;
        if (l.maxProgress < 0)
            p = 0;

        GlStateManager.disableTexture();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();

        vb.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

        vb.vertex(x, y, 0).color(0.5f, 0.5f, 0.5f, 1).next();
        vb.vertex(x, y + barHeight, 0).color(0.5f, 0.5f, 0.5f, 1).next();
        vb.vertex(x + barWidth, y + barHeight, 0).color(0.5f, 0.5f, 0.5f, 1).next();
        vb.vertex(x + barWidth, y, 0).color(0.5f, 0.5f, 0.5f, 1).next();

        vb.vertex(x, y, 0).color(0.5f, 1, 0.5f, 1).next();
        vb.vertex(x, y + barHeight, 0).color(0.5f, 1, 0.5f, 1).next();
        vb.vertex(x + barWidth * p, y + barHeight, 0).color(0.5f, 1, 0.5f, 1).next();
        vb.vertex(x + barWidth * p, y, 0).color(0.5f, 1, 0.5f, 1).next();

        tessellator.draw();

        GlStateManager.enableTexture();
    }
}
