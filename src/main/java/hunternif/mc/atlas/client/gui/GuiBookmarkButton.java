package hunternif.mc.atlas.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collections;


/** Bookmark-button in the journal. When a bookmark is selected, it will not
 * bulge on mouseover. */
public class GuiBookmarkButton extends GuiToggleButton {
	private static final int IMAGE_WIDTH = 84;
	private static final int IMAGE_HEIGHT = 36;
	private static final int WIDTH = 21;
	private static final int HEIGHT = 18;
	private static final int ICON_WIDTH = 16;
	private static final int ICON_HEIGHT = 16;

	private final int colorIndex;
	private ResourceLocation iconTexture;
	private String title;

	/**
	 * @param colorIndex 0=red, 1=blue, 2=yellow, 3=green
	 * @param iconTexture the path to the 16x16 texture to be drawn on top of the bookmark.
	 * @param title hovering text.
	 */
	GuiBookmarkButton(int colorIndex, ResourceLocation iconTexture, String title) {
		this.colorIndex = colorIndex;
		setIconTexture(iconTexture);
		setTitle(title);
		setSize(WIDTH, HEIGHT);
	}

	void setIconTexture(ResourceLocation iconTexture) {
		this.iconTexture = iconTexture;
	}

	void setTitle(String title) {
		this.title = title;
	}

	public ITextComponent getTitle() {
		return new StringTextComponent(title);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		//GuiLighting.disable();
		RenderHelper.disableStandardItemLighting();

		// Render background:
		int u = colorIndex * WIDTH;
		int v = isMouseOver || isSelected() ? 0 : HEIGHT;
		AtlasRenderHelper.drawTexturedRect(Textures.BOOKMARKS, getGuiX(), getGuiY(), u, v, WIDTH, HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);

		// Render the icon:
		AtlasRenderHelper.drawFullTexture(iconTexture,
				getGuiX() + (isMouseOver || isSelected() ? 3 : 2),
				getGuiY() + 1, ICON_WIDTH, ICON_HEIGHT);

		if (isMouseOver) {
			drawTooltip(Collections.singletonList(title), Minecraft.getInstance().fontRenderer);
		}
	}
}
