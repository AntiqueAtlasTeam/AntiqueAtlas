package hunternif.mc.atlas.client.gui;

import java.util.Map;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * A scale bar that displays pixel-to-block ratio. To fit into the overall
 * Atlas style it is rendered at half-scale.
 */
public class GuiScaleBar extends GuiComponent {
	public static final int WIDTH = 40, HEIGHT = 24;
	
	private static Map<Double, ResourceLocation> textureMap;
	{
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
	
	public void setMapScale(double scale) {
		this.mapScale = scale;
	}
	
	/** Returns the background texture depending on the scale. */
	private ResourceLocation getTexture() {
		return textureMap.get(mapScale);
	}
	
	@Override
	public void initGui() {
		super.initGui();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		ResourceLocation texture = getTexture();
		if (texture == null) return;
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 1);
		AtlasRenderHelper.drawFullTexture(texture, getGuiX()*2, getGuiY()*2, WIDTH, HEIGHT);
		if (fontRendererObj.getUnicodeFlag()) {
			// If the font is "small", don't downscale it:
			GL11.glPopMatrix();
			fontRendererObj.drawString(I18n.format("gui.antiqueatlas.scalebar"), getGuiX() - 1, getGuiY() - 1, 0x000000);
			//TODO: it doesn't actually fit!
		} else {
			fontRendererObj.drawString(I18n.format("gui.antiqueatlas.scalebar"), getGuiX()*2 + 3, getGuiY()*2 + 3, 0x000000);
			GL11.glPopMatrix();
		}
	}
}
