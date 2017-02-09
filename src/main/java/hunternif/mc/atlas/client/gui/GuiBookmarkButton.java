package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

/** Bookmark-button in the journal. When a bookmark is selected, it will not
 * bulge on mouseover. */
public class GuiBookmarkButton extends GuiToggleButton {
	private static final int IMAGE_WIDTH = 84;
	private static final int IMAGE_HEIGHT = 36;
	public static final int WIDTH = 21;
	public static final int HEIGHT = 18;
	public static final int ICON_WIDTH = 16;
	public static final int ICON_HEIGHT = 16;
	
	private final int colorIndex;
	private ResourceLocation iconTexture;
	private String title;
	
	/**
	 * @param colorIndex 0=red, 1=blue, 2=yellow, 3=green
	 * @param iconTexture the path to the 16x16 texture to be drawn on top of the bookmark.
	 * @param title hovering text.
	 */
	public GuiBookmarkButton(int colorIndex, ResourceLocation iconTexture, String title) {
		this.colorIndex = colorIndex;
		setIconTexture(iconTexture);
		setTitle(title);
		setSize(WIDTH, HEIGHT);
	}
	
	public void setIconTexture(ResourceLocation iconTexture) {
		this.iconTexture = iconTexture;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
			drawTooltip(Arrays.asList(title), Minecraft.getMinecraft().fontRenderer);
		}
	}
}
