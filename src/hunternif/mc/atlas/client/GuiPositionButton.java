package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.opengl.GL11;

public class GuiPositionButton extends GuiButton {
	public static final int WIDTH = 11;
	public static final int HEIGHT = 11;
	
	public GuiPositionButton(int id, int x, int y, String text) {
		super(id, x, y, WIDTH, HEIGHT, text);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.drawButton) {
			RenderHelper.disableStandardItemLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			boolean isMouseOver = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			if (isMouseOver) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			}
			
			AtlasRenderHelper.drawFullTexture(Textures.BTN_POSITION, xPosition, yPosition, WIDTH, HEIGHT);
			
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
