package hunternif.mc.atlas.client;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class ProgressBarOverlay implements ExportUpdateListener {
	/** Total width of the progress bar. */
	private final int barWidth;
	
	/** Total height of the progress bar. */
	private final int barHeight;
	
	/** With of the currently completed part of the bar. */
	private int completedWidth;
	
	public ProgressBarOverlay(int barWidth, int barHeight) {
		this.barWidth = barWidth;
		this.barHeight = barHeight;
	}
	
	@Override
	public void update(float percentage) {
		completedWidth = (int)(percentage * (float) barWidth);
	}
	
	/** Render progress bar on the screen. */
	public void draw(int x, int y) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(8421504);
        tessellator.addVertex((double)x, (double)y, 0.0D);
        tessellator.addVertex((double)x, (double)(y + barHeight), 0.0D);
        tessellator.addVertex((double)(x + barWidth), (double)(y + barHeight), 0.0D);
        tessellator.addVertex((double)(x + barWidth), (double)y, 0.0D);
        tessellator.setColorOpaque_I(8454016);
        tessellator.addVertex((double)x, (double)y, 0.0D);
        tessellator.addVertex((double)x, (double)(y + barHeight), 0.0D);
        tessellator.addVertex((double)(x + completedWidth), (double)(y + barHeight), 0.0D);
        tessellator.addVertex((double)(x + completedWidth), (double)y, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void reset() {
		completedWidth = 0;
	}

}
