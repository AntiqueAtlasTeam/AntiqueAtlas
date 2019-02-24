package hunternif.mc.atlas.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

import org.lwjgl.opengl.GL11;

class ProgressBarOverlay {
	/** Total width of the progress bar. */
	private final int barWidth;

	/** Total height of the progress bar. */
	private final int barHeight;

	private final TextRenderer font;

	@Environment(EnvType.CLIENT)
	public ProgressBarOverlay(int barWidth, int barHeight) {
		this.barWidth = barWidth;
		this.barHeight = barHeight;
		font = MinecraftClient.getInstance().textRenderer;
	}

	/** Render progress bar on the screen. */
	@Environment(EnvType.CLIENT)
	public void draw(int x, int y) {
		ExportUpdateListener l = ExportUpdateListener.INSTANCE;

		int headerWidth = font.getStringWidth(l.header);
		font.draw(l.header, x + (barWidth - headerWidth)/2, y-14, 0xffffff);
		int statusWidth = font.getStringWidth(l.status);
		font.draw(l.status, x + (barWidth - statusWidth)/2, y, 0xffffff);
		y += 14;

		double p = l.currentProgress/l.maxProgress;
		if(l.maxProgress < 0)
			p = 0;

		GlStateManager.disableTexture();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBufferBuilder();

		vb.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

		vb.vertex(x, y, 0)						.color(0.5f, 0.5f, 0.5f, 1).next();
		vb.vertex(x, y+barHeight, 0)			.color(0.5f, 0.5f, 0.5f, 1).next();
		vb.vertex(x+barWidth, y+barHeight, 0)	.color(0.5f, 0.5f, 0.5f, 1).next();
		vb.vertex(x+barWidth, y, 0)			.color(0.5f, 0.5f, 0.5f, 1).next();

		vb.vertex(x, y, 0)						.color(0.5f, 1, 0.5f, 1).next();
		vb.vertex(x, y+barHeight, 0)			.color(0.5f, 1, 0.5f, 1).next();
		vb.vertex(x+barWidth*p, y+barHeight, 0).color(0.5f, 1, 0.5f, 1).next();
		vb.vertex(x+barWidth*p, y, 0)			.color(0.5f, 1, 0.5f, 1).next();

		tessellator.draw();

		GlStateManager.enableTexture();
	}

}
