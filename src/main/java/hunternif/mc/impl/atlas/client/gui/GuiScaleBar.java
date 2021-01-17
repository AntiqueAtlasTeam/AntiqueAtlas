package hunternif.mc.impl.atlas.client.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.gui.core.GuiComponent;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.Collections;
import java.util.Map;


/**
 * A scale bar that displays pixel-to-block ratio. To fit into the overall
 * Atlas style it is rendered at half-scale.
 */
public class GuiScaleBar extends GuiComponent {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 8;

    private static final Map<Double, ITexture> textureMap;

    static {
        Builder<Double, ITexture> builder = ImmutableMap.builder();
        builder.put(0.0625, Textures.SCALEBAR_512);
        builder.put(0.125, Textures.SCALEBAR_256);
        builder.put(0.25, Textures.SCALEBAR_128);
        builder.put(0.5, Textures.SCALEBAR_64);
        builder.put(1.0, Textures.SCALEBAR_32);
        builder.put(2.0, Textures.SCALEBAR_16);
        builder.put(4.0, Textures.SCALEBAR_8);
        builder.put(8.0, Textures.SCALEBAR_4);
        textureMap = builder.build();
    }

    /**
     * Pixel-to-block ratio.
     */
    private double mapScale = 1;

    GuiScaleBar() {
        setSize(WIDTH, HEIGHT);
    }

    void setMapScale(double scale) {
        this.mapScale = scale;
    }

    /**
     * Returns the background texture depending on the scale.
     */
    private ITexture getTexture() {
        return textureMap.get(mapScale);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        ITexture texture = getTexture();
        if (texture == null) return;

        texture.draw(matrices, getGuiX(), getGuiY());

        if (isMouseOver) {
            drawTooltip(Collections.singletonList(new TranslatableText("gui.antiqueatlas.scalebar")), MinecraftClient.getInstance().textRenderer);
        }
    }
}
