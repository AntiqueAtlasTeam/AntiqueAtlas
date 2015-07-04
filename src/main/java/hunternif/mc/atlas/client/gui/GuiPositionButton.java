package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

public class GuiPositionButton extends GuiComponentButton {
	public static final int WIDTH = 11;
	public static final int HEIGHT = 11;
	
	public GuiPositionButton() {
		setSize(WIDTH, HEIGHT);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		if (isEnabled()) {
			RenderHelper.disableStandardItemLighting();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			int x = getGuiX(), y = getGuiY();
			if (isMouseOver) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			}
			
			AtlasRenderHelper.drawFullTexture(Textures.BTN_POSITION, x, y, WIDTH, HEIGHT);
			
			GL11.glDisable(GL11.GL_BLEND);
			
			if (isMouseOver) {
				drawTooltip(Arrays.asList(I18n.format("gui.antiqueatlas.followPlayer")), Minecraft.getMinecraft().fontRendererObj);
			}
		}
	}
}
