package hunternif.mc.impl.atlas.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;


/**
 * Bookmark-button in the journal. When a bookmark is selected, it will not
 * bulge on mouseover.
 */
public class GuiMarkerBookmark extends GuiComponentButton {
    private static final int WIDTH = 21;
    private static final int HEIGHT = 18;

    private final int colorIndex;
    private ITexture iconTexture;
    private Marker marker;

    GuiMarkerBookmark(Marker marker) {
        this.colorIndex = 3;
        this.marker = marker;

        MarkerType type = MarkerType.REGISTRY.get(marker.getType());
        setIconTexture(type.getTexture());

        setSize(WIDTH, HEIGHT);
    }

    void setIconTexture(ITexture iconTexture) {
        this.iconTexture = iconTexture;
    }

    public Text getTitle() {
        return marker.getLabel();
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Render background:
        int u = colorIndex * WIDTH;
        int v = isMouseOver ? 0 : HEIGHT;
        Textures.BOOKMARKS_LEFT.draw(matrices, getGuiX(), getGuiY(), u, v, WIDTH, HEIGHT);

        // Render the icon:
        iconTexture.draw(matrices, getGuiX() - (isMouseOver ? 3 : 2), getGuiY()-3, 24,24);

        if (isMouseOver && !getTitle().getString().isEmpty()) {
            drawTooltip(Collections.singletonList(getTitle()), MinecraftClient.getInstance().textRenderer);
        }
    }
}
