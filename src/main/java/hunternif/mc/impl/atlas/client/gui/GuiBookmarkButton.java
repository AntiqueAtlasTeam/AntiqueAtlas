package hunternif.mc.impl.atlas.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.Collections;


/**
 * Bookmark-button in the journal. When a bookmark is selected, it will not
 * bulge on mouseover.
 */
public class GuiBookmarkButton extends GuiToggleButton {
    private static final int WIDTH = 21;
    private static final int HEIGHT = 18;

    private final int colorIndex;
    private ITexture iconTexture;
    private Component title;

    /**
     * @param colorIndex  0=red, 1=blue, 2=yellow, 3=green
     * @param iconTexture the path to the 16x16 texture to be drawn on top of the bookmark.
     * @param title       hovering text.
     */
    GuiBookmarkButton(int colorIndex, ITexture iconTexture, Component title) {
        this.colorIndex = colorIndex;
        setIconTexture(iconTexture);
        setTitle(title);
        setSize(WIDTH, HEIGHT);
    }

    void setIconTexture(ITexture iconTexture) {
        this.iconTexture = iconTexture;
    }

    public Component getTitle() {
        return title;
    }

    void setTitle(Component title) {
        this.title = title;
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Render background:
        int u = colorIndex * WIDTH;
        int v = isMouseOver || isSelected() ? 0 : HEIGHT;
        Textures.BOOKMARKS.draw(matrices, getGuiX(), getGuiY(), u, v, WIDTH, HEIGHT);

        // Render the icon:
        iconTexture.draw(matrices, getGuiX() + (isMouseOver || isSelected() ? 3 : 2), getGuiY() + 1);

        if (isMouseOver) {
            drawTooltip(Collections.singletonList(title), Minecraft.getInstance().font);
        }
    }
}
