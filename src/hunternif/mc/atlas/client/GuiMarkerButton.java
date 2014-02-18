package hunternif.mc.atlas.client;

import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;

import org.lwjgl.opengl.GL11;

public class GuiMarkerButton extends GuiButton {
	
	public GuiMarkerButton(int id, int x, int y, int width, int height) {
		super(id, x, y, width, height, "");
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
			
			AtlasRenderHelper.drawTexturedRect(Textures.MAP_MARKER,
					(width - 11)/2 + xPosition, (height - 17)/2 + 1 + yPosition,
					16-6, 0, 11, 17, 32, 32);
		}
	}
}
