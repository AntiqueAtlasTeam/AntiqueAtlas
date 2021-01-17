package hunternif.mc.impl.atlas.client.gui.core;

import net.minecraft.client.util.math.MatrixStack;

public class GuiVScrollbar extends AGuiScrollbar {

    public GuiVScrollbar(GuiViewport viewport) {
        super(viewport);
    }

    @Override
    protected void drawAnchor(MatrixStack matrices) {
        // Draw top cap:
        texture.draw(matrices, getGuiX(), getGuiY() + anchorPos, textureWidth, capLength, 0, 0, textureWidth, capLength);

        // Draw body:
        texture.draw(matrices, getGuiX(), getGuiY() + anchorPos + capLength, textureWidth, anchorSize, 0, capLength, textureWidth, textureBodyLength);

        // Draw bottom cap:
        texture.draw(matrices, getGuiX(),getGuiY() + anchorPos + capLength + anchorSize, 0, textureHeight - capLength, textureWidth, capLength);
    }

    @Override
    protected int getTextureLength() {
        return textureHeight;
    }

    @Override
    protected int getScrollbarLength() {
        return getHeight();
    }

    @Override
    protected int getViewportSize() {
        return viewport.getHeight();
    }

    @Override
    protected int getContentSize() {
        return viewport.contentHeight;
    }

    @Override
    protected int getMousePos(int mouseX, int mouseY) {
        return mouseY - getGuiY();
    }

    @Override
    protected void updateContentPos() {
        viewport.content.setRelativeCoords(viewport.content.getRelativeX(), -scrollPos);
    }

    @Override
    protected void setScrollbarWidth(int textureWidth, int textureHeight) {
        setSize(textureWidth, getHeight());
    }

}
