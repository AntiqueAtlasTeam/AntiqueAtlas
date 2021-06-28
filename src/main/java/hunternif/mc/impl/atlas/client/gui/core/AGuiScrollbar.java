package hunternif.mc.impl.atlas.client.gui.core;

import com.mojang.blaze3d.systems.RenderSystem;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;


public abstract class AGuiScrollbar extends GuiComponent {
    ITexture texture;
    int textureWidth;
    int textureHeight;
    /**
     * Length of the non-scaling caps at the beginning and end of the anchor.
     */
    int capLength;
    int textureBodyLength;

    /**
     * In pixels.
     */
    private static int scrollStep = 18;

    boolean visible = false;
    /**
     * True if the anchor is being dragged
     */
    private boolean isDragged = false;
    /**
     * True if the left mouse button was held down last time drawScreen was called.
     */
    private boolean wasClicking = false;

    private boolean usesWheel = true;

    /**
     * Size of the viewport / size of content.
     */
    private float contentRatio = 1;

    /**
     * Anchor position / available length.
     */
    private float scrollRatio = 0;


    int anchorPos = 0;
    int anchorSize;
    /**
     * How much to scale the texture vertically to draw the body of the anchor.
     */
    double bodyTextureScale = 1;
    /**
     * How much the content of the viewport is displaced.
     */
    int scrollPos = 0;

    /**
     * The attached viewport that this scrollbar scrolls.
     */
    final GuiViewport viewport;

    AGuiScrollbar(GuiViewport viewport) {
        this.viewport = viewport;
    }

    /**
     * @param texture   texture of the anchor
     * @param width     width of the texture image
     * @param height    height of the texture image
     * @param capLength length of the non-scaling caps at the beginning and end of the anchor
     */
    public void setTexture(ITexture texture, int width, int height, int capLength) {
        this.texture = texture;
        this.textureWidth = width;
        this.textureHeight = height;
        this.capLength = capLength;
        this.textureBodyLength = getTextureLength() - capLength * 2;
        setScrollbarWidth(width, height);
    }

    public void setUsesWheel(boolean value) {
        this.usesWheel = value;
    }

    /**
     * Recalculate anchor size and position.
     */
    public void updateContent() {
        this.contentRatio = (float) getViewportSize() / (float) getContentSize();
        this.visible = contentRatio < 1;
        updateAnchorSize();
        updateAnchorPos();
    }

    /**
     * Offset of the viewport's content in pixels. This method forces
     * validation of the viewport and its content in order to work correctly
     * during initGui().
     */
    public void setScrollPos(int scrollPos) {
        viewport.content.validateSize();
        viewport.validateSize();
        doSetScrollPos(scrollPos);
    }

    /**
     * Offset of the viewport's content in pixels. This will only work
     * correctly after the viewport's size has been validated.
     */
    private void doSetScrollPos(int scrollPos) {
        scrollPos = Math.max(0, Math.min(scrollPos, getContentSize() - getViewportSize()));
        this.scrollPos = scrollPos;
        scrollRatio = (float) scrollPos / (float) (getContentSize() - getViewportSize());
        updateAnchorPos();
    }

    /**
     * Amount scrolled (0.0 = top, 1.0 = bottom). This method forces
     * validation of the viewport and its content in order to work correctly
     * during initGui().
     */
    public void setScrollRatio(float scrollRatio) {
        viewport.content.validateSize();
        viewport.validateSize();
        doSetScrollRatio(scrollRatio);
    }

    /**
     * Amount scrolled (0.0 = top, 1.0 = bottom). This will only work
     * correctly after the viewport's size has been validated.
     */
    private void doSetScrollRatio(float scrollRatio) {
        if (scrollRatio < 0) scrollRatio = 0;
        if (scrollRatio > 1) scrollRatio = 1;
        this.scrollRatio = scrollRatio;
        scrollPos = Math.round(scrollRatio * (float) (getContentSize() - getViewportSize()));
        updateAnchorPos();
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double wheelMove) {
        if (usesWheel) {
            if (wheelMove != 0 && this.visible) {
                wheelMove = wheelMove > 0 ? -1 : 1;
                doSetScrollPos((int) (scrollPos + wheelMove * scrollStep));
                return true;
            }
        }

        return super.mouseScrolled(mx, my, wheelMove);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isMouseOver) {
                if (!wasClicking) {
                    isDragged = true;
                }
                wasClicking = true;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (wasClicking) {
                isDragged = false;
                wasClicking = false;
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        // Don't draw the anchor if there's nothing to scroll:
        if (!visible) {
            isDragged = false;
            return;
        }

        if (isDragged) {
            doSetScrollRatio((float) (getMousePos(mouseX, mouseY) - anchorSize / 2)
                    / (float) (getScrollbarLength() - anchorSize));
        }

        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        drawAnchor(matrices);

        RenderSystem.disableBlend();
    }

    private void updateAnchorSize() {
        anchorSize = Math.max(capLength * 2, Math.round(Math.min(1, contentRatio) * getScrollbarLength()));
        bodyTextureScale = (double) (anchorSize - capLength * 2) / (double) textureBodyLength;
    }

    private void updateAnchorPos() {
        anchorPos = Math.round(scrollRatio * (float) (getViewportSize() - anchorSize));
        updateContentPos();
    }

    // Retrieving axis-related data

    /**
     * The length along the scrolling axis.
     */
    protected abstract int getTextureLength();

    /**
     * The total length available for scrolling.
     */
    protected abstract int getScrollbarLength();

    protected abstract int getViewportSize();

    protected abstract int getContentSize();

    protected abstract int getMousePos(int mouseX, int mouseY);

    // Modifying axis-related data
    protected abstract void drawAnchor(MatrixStack matrices);

    protected abstract void updateContentPos();

    /**
     * The width is perpendicular to the scrolling axis.
     */
    protected abstract void setScrollbarWidth(int textureWidth, int textureHeight);
}
