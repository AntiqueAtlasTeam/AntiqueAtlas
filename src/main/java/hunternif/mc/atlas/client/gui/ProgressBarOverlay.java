package hunternif.mc.atlas.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

public class ProgressBarOverlay implements ExportUpdateListener {
	/** Total width of the progress bar. */
	private final int barWidth;
	
	/** Total height of the progress bar. */
	private final int barHeight;
	
	/** With of the currently completed part of the bar. */
	private int completedWidth;
	
	private String status;
	private FontRenderer font;
	
	public ProgressBarOverlay(int barWidth, int barHeight) {
		this.barWidth = barWidth;
		this.barHeight = barHeight;
		font = Minecraft.getMinecraft().fontRendererObj;
	}
	
	@Override
	public void setStatusString(String status) {
		this.status = status;
	}
	
	@Override
	public void update(float percentage) {
		if (percentage < 0) percentage = 0;
		if (percentage > 1) percentage = 1;
		completedWidth = Math.round(percentage * (float) barWidth);
	}
	
	/** Render progress bar on the screen. */
	public void draw(int x, int y) {
		int statusWidth = font.getStringWidth(status);
		font.drawStringWithShadow(status, x + (barWidth - statusWidth)/2, y, 0xffffff);
		y += 14;

		GlStateManager.disableTexture2D();
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		GlStateManager.color(0.5f, 0.5f, 0.5f, 1f);
		renderer.pos(x,y,0).endVertex();
		renderer.pos(x,y+barHeight,0).endVertex();
		renderer.pos(x + barWidth, y + barHeight, 0).endVertex();
		renderer.pos(x + barWidth, y, 0).endVertex();
		tessellator.draw();
		
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		GlStateManager.color(0.5f, 1f, 0.5f, 1f);
		renderer.pos(x,y,0).endVertex();
		renderer.pos(x,y+barHeight,0).endVertex();
		renderer.pos(x + completedWidth, y + barHeight, 0).endVertex();
		renderer.pos(x + completedWidth, y, 0).endVertex();
		tessellator.draw();
		
		GlStateManager.enableTexture2D();
	}

	public void reset() {
		completedWidth = 0;
	}

}
