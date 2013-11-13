package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.opengl.GL11;

public class GuiArrowButton extends GuiButton {
	public static final int WIDTH = 12;
	public static final int HEIGHT = 12;
	private static final int IMAGE_WIDTH = 24;
	private static final int IMAGE_HEIGHT = 24;
	
	public static enum ArrowDirection {
		UP("Up"), DOWN("Down"), LEFT("Left"), RIGHT("Right");
		
		public String description;
		ArrowDirection(String text) {
			this.description = text;
		}
	}
	
	public ArrowDirection direction;
	
	public GuiArrowButton(int id, int x, int y, ArrowDirection direction) {
		super(id, x, y, WIDTH, HEIGHT, direction.description);
		this.direction = direction;
	}
	
	public static GuiArrowButton up(int id, int x, int y) {
		return new GuiArrowButton(id, x, y, ArrowDirection.UP);
	}
	public static GuiArrowButton down(int id, int x, int y) {
		return new GuiArrowButton(id, x, y, ArrowDirection.DOWN);
	}
	public static GuiArrowButton left(int id, int x, int y) {
		return new GuiArrowButton(id, x, y, ArrowDirection.LEFT);
	}
	public static GuiArrowButton right(int id, int x, int y) {
		return new GuiArrowButton(id, x, y, ArrowDirection.RIGHT);
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
			
			int u = 0, v = 0;
			switch (direction) {
			case LEFT: u = 0; v = 0; break;
			case RIGHT: u = 0; v = 12; break;
			case UP: u = 12; v = 0; break;
			case DOWN: u = 12; v = 12; break;
			}
			AtlasRenderHelper.drawTexturedRect(Textures.BTN_ARROWS, xPosition, yPosition, u, v, WIDTH, HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
			
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
