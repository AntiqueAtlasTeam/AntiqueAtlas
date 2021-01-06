package hunternif.mc.impl.atlas.client.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import hunternif.mc.impl.atlas.util.ExportImageUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ExportProgressOverlay {
	INSTANCE;

	@OnlyIn(Dist.CLIENT)
	public void draw(MatrixStack matrices, int scaledWidth, int scaledHeight) {
		int x = scaledWidth - 40, y = scaledHeight - 20, barWidth = 50, barHeight = 2;

		ExportUpdateListener l = ExportUpdateListener.INSTANCE;

		if (!ExportImageUtil.isExporting) {
			return;
		}

		FontRenderer font = Minecraft.getInstance().fontRenderer;
		int s = 2;

		GlStateManager.scaled(1.0/s, 1.0/s, 1);

		int headerWidth = font.getStringWidth(l.header);
		font.drawString(matrices, l.header, ( x )*s -headerWidth/2F, ( y )*s - 14, 0xffffff);
		int statusWidth = font.getStringWidth(l.status);
		font.drawString(matrices, l.status, ( x )*s -statusWidth/2F, ( y )*s, 0xffffff);

		GlStateManager.scaled(s, s, 1);
		y += 7;

		x -= barWidth/2;
		double p = l.currentProgress/l.maxProgress;
		if(l.maxProgress < 0)
			p = 0;

		GlStateManager.disableTexture();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		vb.pos(x, y, 0)						.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x, y+barHeight, 0)			.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x+barWidth, y+barHeight, 0)	.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x+barWidth, y, 0)			.color(0.5f, 0.5f, 0.5f, 1).endVertex();

		vb.pos(x, y, 0)						.color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x, y+barHeight, 0)			.color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x+barWidth*p, y+barHeight, 0) .color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x+barWidth*p, y, 0)			.color(0.5f, 1, 0.5f, 1).endVertex();

		tessellator.draw();

		GlStateManager.enableTexture();
	}
}
