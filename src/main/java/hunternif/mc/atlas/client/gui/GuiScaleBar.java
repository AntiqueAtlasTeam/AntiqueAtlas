package hunternif.mc.atlas.client.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponent;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.Map;


/**
 * A scale bar that displays pixel-to-block ratio. To fit into the overall
 * Atlas style it is rendered at half-scale.
 */
public class GuiScaleBar extends GuiComponent {
	private static final int WIDTH = 20;
	private static final int HEIGHT = 8;

	private static final Map<Double, ResourceLocation> textureMap;
	static {
		Builder<Double, ResourceLocation> builder = ImmutableMap.builder();
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
	/** Pixel-to-block ratio. */
	private double mapScale = 1;
	
	GuiScaleBar() {
		setSize(WIDTH, HEIGHT);
	}

	void setMapScale(double scale) {
		this.mapScale = scale;
	}

	/** Returns the background texture depending on the scale. */
	private ResourceLocation getTexture() {
		return textureMap.get(mapScale);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		ResourceLocation texture = getTexture();
		if (texture == null) return;

		AtlasRenderHelper.drawFullTexture(texture, getGuiX(), getGuiY(), WIDTH, HEIGHT);

		if (isMouseOver) {
			drawTooltip(Collections.singletonList(I18n.format("gui.antiqueatlas.scalebar")), Minecraft.getInstance().fontRenderer);
		}
	}
}
