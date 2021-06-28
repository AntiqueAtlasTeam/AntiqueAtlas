package hunternif.mc.impl.atlas.client.gui.core;

import com.mojang.blaze3d.systems.RenderSystem;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

/**
 * Displays a texture that changes alpha at regular intervals.
 * By default the texture file is assumed to be full image, but that behavior
 * can be altered by overriding the method {@link #drawImage(MatrixStack)}.
 *
 * @author Hunternif
 */
public class GuiBlinkingImage extends GuiComponent {
    private ITexture texture;
    /**
     * The number of milliseconds the icon spends visible or invisible.
     */
    private long blinkTime = 500;
    private float visibleAlpha = 1;
    private float invisibleAlpha = 0.25f;

    private long lastTickTime;
    /**
     * The flag that switches value every "blink".
     */
    private boolean isVisible;

    public void setTexture(ITexture texture, int width, int height) {
        this.texture = texture;
        setSize(width, height);
        // Set up the timer so that the image appears visible at the first moment:
        lastTickTime = 0;
        isVisible = false;
    }

    /**
     * The number of milliseconds the icon spends visible or invisible.
     */
    public void setBlinkTime(long blinkTime) {
        this.blinkTime = blinkTime;
    }

    public void setVisibleAlpha(float visibleAlpha) {
        this.visibleAlpha = visibleAlpha;
    }

    public void setInvisibleAlpha(float invisibleAlpha) {
        this.invisibleAlpha = invisibleAlpha;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        long currentTime = System.currentTimeMillis();
        if (lastTickTime + blinkTime < currentTime) {
            lastTickTime = currentTime;
            isVisible = !isVisible;
        }
        RenderSystem.setShaderColor(1, 1, 1, isVisible ? visibleAlpha : invisibleAlpha);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawImage(matrices);

        RenderSystem.disableBlend();
    }

    private void drawImage(MatrixStack matrices) {
        texture.draw(matrices, getGuiX(), getGuiY(), getWidth(), getHeight());
    }
}
