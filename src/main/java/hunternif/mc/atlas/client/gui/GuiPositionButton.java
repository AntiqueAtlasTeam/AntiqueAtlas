package hunternif.mc.atlas.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import java.util.Collections;

public class GuiPositionButton extends GuiComponentButton {
	private static final int WIDTH = 11;
	private static final int HEIGHT = 11;

	public GuiPositionButton() {
		setSize(WIDTH, HEIGHT);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (isEnabled()) {
			//GuiLighting.disable();
			RenderHelper.disableStandardItemLighting();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			int x = getGuiX(), y = getGuiY();
			if (isMouseOver) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
			}

			AtlasRenderHelper.drawFullTexture(Textures.BTN_POSITION, x, y, WIDTH, HEIGHT);

			RenderSystem.disableBlend();

			if (isMouseOver) {
				drawTooltip(Collections.singletonList(I18n.format("gui.antiqueatlas.followPlayer")), Minecraft.getInstance().fontRenderer);
			}
		}
	}
}
