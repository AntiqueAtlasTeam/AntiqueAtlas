package hunternif.mc.impl.atlas.client.gui.core;

import net.minecraft.client.gui.DrawContext;

public class GuiHScrollbar extends AGuiScrollbar {

    public GuiHScrollbar(GuiViewport viewport) {
        super(viewport);
    }

    @Override
    protected void drawAnchor(DrawContext drawContext) {
        // Draw left cap:
        texture.draw(drawContext, getGuiX() + anchorPos, getGuiY(), capLength, textureHeight, 0, 0, capLength, textureHeight);

        // Draw body:
        texture.draw(drawContext, getGuiX() + anchorPos + capLength, getGuiY(), anchorSize, textureHeight, capLength, 0, textureBodyLength, textureHeight);

        // Draw right cap:
        texture.draw(drawContext, getGuiX() + anchorPos + capLength + anchorSize, getGuiY(), textureWidth - capLength, 0, capLength, textureHeight);
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
