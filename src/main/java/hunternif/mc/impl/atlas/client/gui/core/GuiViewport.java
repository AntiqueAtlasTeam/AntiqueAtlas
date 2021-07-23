package hunternif.mc.impl.atlas.client.gui.core;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

/**
 * The children of this component are rendered and process input only inside
 * the viewport frame. Use {@link #setSize(int, int)} to set its bounds.
 *
 * @author Hunternif
 */
public class GuiViewport extends GuiComponent {
    /**
     * The container component for content.
     */
    final GuiComponent content = new GuiComponent();

    /**
     * Coordinate scale factor relative to the actual screen size.
     */
    private double screenScale;

    public GuiViewport() {
        this.addChild(content);
    }

    /**
     * Add scrolling content. Use removeContent to remove it.
     *
     * @return the child added
     */
    public GuiComponent addContent(GuiComponent child) {
        return content.addChild(child);
    }

    /**
     * @return the child removed
     */
    public GuiComponent removeContent(GuiComponent child) {
        return content.removeChild(child);
    }

    public void removeAllContent() {
        content.removeAllChildren();
    }

    @Override
    public void init(MinecraftClient client, int w, int h) {
        super.init(client, w, h);
        screenScale = client.getWindow().getScaleFactor();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float par3) {
        RenderSystem.enableScissor((int) (getGuiX() * screenScale),
                (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() - (getGuiY() + properHeight) * screenScale),
                (int) (properWidth * screenScale), (int) (properHeight * screenScale));

        // Draw the content (child GUIs):
        super.render(matrices, mouseX, mouseY, par3);

        RenderSystem.disableScissor();
    }

    @Override
    boolean iterateMouseInput(UiCall callMethod) {
        return iterateInput(callMethod);
    }

    @Override
    public int getWidth() {
        return properWidth;
    }

    @Override
    public int getHeight() {
        return properHeight;
    }

    @Override
    protected void validateSize() {
        super.validateSize();
        // Update the clipping flag on content's child components:
        for (GuiComponent child : this.getChildren()) {
            if (child.getGuiY() > getGuiY() + properHeight ||
                    child.getGuiY() + child.getHeight() < getGuiY() ||
                    child.getGuiX() > getGuiX() + properWidth ||
                    child.getGuiX() + child.getWidth() < getGuiX()) {
                child.setClipped(true);
            } else {
                child.setClipped(false);
            }
        }
    }
}
