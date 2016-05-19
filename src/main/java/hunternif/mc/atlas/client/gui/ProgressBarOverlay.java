package hunternif.mc.atlas.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
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


		
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glColor4f(0.5f, 0.5f, 0.5f, 1);
//		GL11.glVertex2d(x,y);
//		GL11.glVertex2d(x,y+barHeight);
//		GL11.glVertex2d(x + barWidth, y + barHeight);
//		GL11.glVertex2d(x + barWidth, y);
//		GL11.glColor4f(0.5f, 1, 0.5f, 1);
//		GL11.glVertex2d(x,y);
//		GL11.glVertex2d(x,y+barHeight);
//		GL11.glVertex2d(x + completedWidth, y + barHeight);
//		GL11.glVertex2d(x + completedWidth, y);
//		GL11.glEnd();
		
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		renderer.color(128, 128, 128, 255);
		renderer.pos(x,y,0).endVertex();
		renderer.pos(x,y+barHeight,0);
		renderer.pos(x + barWidth, y + barHeight, 0);
		renderer.pos(x + barWidth, y, 0);
		renderer.color(128, 255, 128, 255);
		int[] vertexData5 = {x,y,0};
		renderer.addVertexData(vertexData5);
		int[] vertexData6 = {x,y+barHeight,0};
		renderer.addVertexData(vertexData6);
		int[] vertexData7 = {x + completedWidth, y + barHeight, 0};
		renderer.addVertexData(vertexData7);
		int[] vertexData8 = {x + completedWidth, y, 0};
		renderer.addVertexData(vertexData8);
		renderer.endVertex();
//		renderer.startDrawingQuads();
//		renderer.setColorOpaque_I(8421504);
//		renderer.addVertex((double)x, (double)y, 0.0D);
//		renderer.addVertex((double)x, (double)(y + barHeight), 0.0D);
//		renderer.addVertex((double)(x + barWidth), (double)(y + barHeight), 0.0D);
//		renderer.addVertex((double)(x + barWidth), (double)y, 0.0D);
//		renderer.setColorOpaque_I(8454016);
//		renderer.addVertex((double)x, (double)y, 0.0D);
//		renderer.addVertex((double)x, (double)(y + barHeight), 0.0D);
//		renderer.addVertex((double)(x + completedWidth), (double)(y + barHeight), 0.0D);
//		renderer.addVertex((double)(x + completedWidth), (double)y, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void reset() {
		completedWidth = 0;
	}

}
