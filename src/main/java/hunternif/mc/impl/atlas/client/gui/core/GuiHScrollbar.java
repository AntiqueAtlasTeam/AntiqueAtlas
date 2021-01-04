package hunternif.mc.impl.atlas.client.gui.core;

import hunternif.mc.impl.atlas.util.AtlasRenderHelper;
import net.minecraft.client.util.math.MatrixStack;

public class GuiHScrollbar extends AGuiScrollbar {

    public GuiHScrollbar(GuiViewport viewport) {
        super(viewport);
    }

    @Override
    protected void drawAnchor(MatrixStack matrices) {
        // Draw top cap:
        AtlasRenderHelper.drawTexturedRect(matrices, texture, getGuiX() + anchorPos, getGuiY(), 0, 0,
                capLength, textureHeight, textureWidth, textureHeight);

        // Draw body:
        AtlasRenderHelper.drawTexturedRect(matrices, texture, getGuiX() + anchorPos + capLength, getGuiY(),
                capLength, 0, textureBodyLength, textureHeight, textureWidth, textureHeight, bodyTextureScale, 1);

        // Draw bottom cap:
        AtlasRenderHelper.drawTexturedRect(matrices, texture,
                getGuiX() + anchorPos + anchorSize - capLength, getGuiY(),
                textureWidth - capLength, 0,
                capLength, textureHeight, textureWidth, textureHeight);
    }

    @Override
    protected int getTextureLength() {
        return textureWidth;
    }

    @Override
    protected int getScrollbarLength() {
        return getWidth();
    }

    @Override
    protected int getViewportSize() {
        return viewport.getWidth();
    }

    @Override
    protected int getContentSize() {
        return viewport.contentWidth;
    }

    @Override
    protected int getMousePos(int mouseX, int mouseY) {
        return mouseX - getGuiX();
    }

    @Override
    protected void updateContentPos() {
        viewport.content.setRelativeCoords(-scrollPos, viewport.content.getRelativeY());
    }

    @Override
    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        setSize(getWidth(), textureHeight);
    }

}
