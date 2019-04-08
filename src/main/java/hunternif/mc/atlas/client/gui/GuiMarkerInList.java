package hunternif.mc.atlas.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.util.Identifier;


public class GuiMarkerInList extends GuiToggleButton {
	public static final int FRAME_SIZE = 34;
	
	private final MarkerType markerType;
	
	public GuiMarkerInList(MarkerType markerType) {
		this.markerType = markerType;
		setSize(FRAME_SIZE, FRAME_SIZE);
	}
	
	public MarkerType getMarkerType() {
		return markerType;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		GlStateManager.color4f(1, 1, 1, 1);
		AtlasRenderHelper.drawFullTexture(
				isSelected() ? Textures.MARKER_FRAME_ON : Textures.MARKER_FRAME_OFF,
				getGuiX(), getGuiY(), FRAME_SIZE, FRAME_SIZE);
		
		Identifier texture = markerType.getIcon();
		if (texture != null) {
			AtlasRenderHelper.drawFullTexture(texture, getGuiX() + 1, getGuiY() + 1, GuiAtlas.MARKER_SIZE, GuiAtlas.MARKER_SIZE);
		}
		super.render(mouseX, mouseY, partialTick);
	}
}
