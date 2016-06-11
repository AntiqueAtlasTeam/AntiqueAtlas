package hunternif.mc.atlas.client.gui;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AtlasRenderHelper;

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
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		GL11.glColor4f(1, 1, 1, 1);
		AtlasRenderHelper.drawFullTexture(
				isSelected() ? Textures.MARKER_FRAME_ON : Textures.MARKER_FRAME_OFF,
				getGuiX(), getGuiY(), FRAME_SIZE, FRAME_SIZE);
		
		ResourceLocation texture = markerType.getIcon();
		if (texture != null) {
			AtlasRenderHelper.drawFullTexture(texture, getGuiX() + 1, getGuiY() + 1, GuiAtlas.MARKER_SIZE, GuiAtlas.MARKER_SIZE);
		}
		super.drawScreen(mouseX, mouseY, partialTick);
	}
}
