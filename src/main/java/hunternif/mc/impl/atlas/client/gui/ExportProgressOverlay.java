package hunternif.mc.impl.atlas.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import hunternif.mc.impl.atlas.util.ExportImageUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

public enum ExportProgressOverlay {
    INSTANCE;

    @OnlyIn(Dist.CLIENT)
    public void draw(PoseStack matrices, int scaledWidth, int scaledHeight) {
        int x = scaledWidth - 40, y = scaledHeight - 20, barWidth = 50, barHeight = 2;

        ExportUpdateListener l = ExportUpdateListener.INSTANCE;

        if (!ExportImageUtil.isExporting) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        int s = 2;

        int headerWidth = font.width(l.header);
        font.draw(matrices, l.header, (x) * s - headerWidth / 2F, (y) * s - 14, 0xffffff);
        int statusWidth = font.width(l.status);
        font.draw(matrices, l.status, (x) * s - statusWidth / 2F, (y) * s, 0xffffff);

        y += 7;

        x -= barWidth / 2;
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
