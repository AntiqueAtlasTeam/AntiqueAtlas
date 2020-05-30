package hunternif.mc.atlas.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

class ProgressBarOverlay {
	/** Total width of the progress bar. */
	private final int barWidth;

	/** Total height of the progress bar. */
	private final int barHeight;

	private final FontRenderer font;

	@OnlyIn(Dist.CLIENT)
	public ProgressBarOverlay(int barWidth, int barHeight) {
		this.barWidth = barWidth;
		this.barHeight = barHeight;
		font = Minecraft.getInstance().fontRenderer;
	}

	/** Render progress bar on the screen. */
	@OnlyIn(Dist.CLIENT)
	public void draw(int x, int y) {
		ExportUpdateListener l = ExportUpdateListener.INSTANCE;

		int headerWidth = font.getStringWidth(l.header);
		font.drawString(l.header, x + (barWidth - headerWidth)/2, y-14, 0xffffff);
		int statusWidth = font.getStringWidth(l.status);
		font.drawString(l.status, x + (barWidth - statusWidth)/2, y, 0xffffff);
		y += 14;

		double p = l.currentProgress/l.maxProgress;
		if(l.maxProgress < 0)
			p = 0;

		RenderSystem.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

		vb.pos(x, y, 0)						.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x, y+barHeight, 0)			.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x+barWidth, y+barHeight, 0)	.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x+barWidth, y, 0)			.color(0.5f, 0.5f, 0.5f, 1).endVertex();

		vb.pos(x, y, 0)						.color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x, y+barHeight, 0)			.color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x+barWidth*p, y+barHeight, 0).color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x+barWidth*p, y, 0)			.color(0.5f, 1, 0.5f, 1).endVertex();

		tessellator.draw();

		RenderSystem.enableTexture();
	}

}
