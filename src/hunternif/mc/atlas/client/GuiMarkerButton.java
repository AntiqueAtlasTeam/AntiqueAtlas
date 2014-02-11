package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.opengl.GL11;

public class GuiMarkerButton extends GuiButton {
	public static final int BTN_WIDTH = 22;
	public static final int BTN_HEIGHT = 22;
	public static final int ICON_WIDTH = 11;
	public static final int ICON_HEIGHT = 17;
	
	public GuiMarkerButton(int id, int x, int y) {
		super(id, x, y, BTN_WIDTH, BTN_HEIGHT, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		super.drawButton(mc, mouseX, mouseY);
		if (this.drawButton) {
			RenderHelper.disableStandardItemLighting();
			
			boolean isMouseOver = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			if (isMouseOver) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			}
			
			AtlasRenderHelper.drawFullTexture(Textures.MAP_MARKER,
					(width - ICON_WIDTH)/2 + xPosition,
					(height - ICON_HEIGHT)/2 + yPosition, ICON_WIDTH, ICON_HEIGHT);
		}
	}
}
