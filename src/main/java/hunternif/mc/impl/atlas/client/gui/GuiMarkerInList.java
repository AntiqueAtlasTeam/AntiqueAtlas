package hunternif.mc.impl.atlas.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.impl.atlas.registry.MarkerType;
import hunternif.mc.impl.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;


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
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
		AtlasRenderHelper.drawFullTexture(matrices, isSelected() ? Textures.MARKER_FRAME_ON : Textures.MARKER_FRAME_OFF, getGuiX() + 1, getGuiY() + 1, FRAME_SIZE);

		ResourceLocation texture = markerType.getIcon();
		if (texture != null) {
			AtlasRenderHelper.drawFullTexture(matrices, texture, getGuiX() + 1, getGuiY() + 1, GuiAtlas.MARKER_SIZE);

		}

		super.render(matrices, mouseX, mouseY, partialTick);
	}
}
