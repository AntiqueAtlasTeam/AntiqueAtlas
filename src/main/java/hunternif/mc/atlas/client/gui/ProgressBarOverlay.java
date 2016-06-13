package hunternif.mc.atlas.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

public class ProgressBarOverlay implements ExportUpdateListener {
	/** Total width of the progress bar. */
	private final int barWidth;
	
	/** Total height of the progress bar. */
	private final int barHeight;
	
	private float maxProgress; // float so that division isn't rounded;
	private int currentProgress;
	
	private String header;
	private String status;
	private FontRenderer font;
	
	public ProgressBarOverlay(int barWidth, int barHeight) {
		this.barWidth = barWidth;
		this.barHeight = barHeight;
		font = Minecraft.getMinecraft().fontRendererObj;
	}
	
	@Override
	public void setStatusString(String status, Object... data) {
		this.status = I18n.format(status, data);
	}
	
	@Override
	public void setHeaderString(String header, Object... data) {
		this.header = I18n.format(header, data);
	}

	@Override
	public void setProgressMax(int max) {
		maxProgress = max;
		currentProgress = 0;
	}

	@Override
	public void setProgress(int progress) {
		currentProgress = progress;
	}

	@Override
	public void addProgress(int amount) {
		currentProgress += amount;
	}
	
	/** Render progress bar on the screen. */
	public void draw(int x, int y) {
		int headerWidth = font.getStringWidth(header);
		font.drawStringWithShadow(header, x + (barWidth - headerWidth)/2, y-14, 0xffffff);
		int statusWidth = font.getStringWidth(status);
		font.drawStringWithShadow(status, x + (barWidth - statusWidth)/2, y, 0xffffff);
		y += 14;
		
		double p = currentProgress/maxProgress;
		if(maxProgress < 0)
			p = 0;
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
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
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
