package hunternif.mc.impl.atlas.client.gui;

import java.util.Collections;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.gui.core.GuiComponentButton;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiPositionButton extends GuiComponentButton {
	private static final int WIDTH = 11;
	private static final int HEIGHT = 11;

	public GuiPositionButton() {
		setSize(WIDTH, HEIGHT);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
		if (isEnabled()) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			int x = getGuiX(), y = getGuiY();
			if (isMouseOver) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			} else {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
			}

			Textures.BTN_POSITION.draw(matrices, x, y, WIDTH, HEIGHT);

			GlStateManager.disableBlend();

			if (isMouseOver) {
				drawTooltip(Collections.singletonList(new TranslationTextComponent("gui.antiqueatlas.followPlayer")), Minecraft.getInstance().fontRenderer);
			}
		}
	}
}
