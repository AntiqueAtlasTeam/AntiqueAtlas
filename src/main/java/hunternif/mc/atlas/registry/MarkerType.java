package hunternif.mc.atlas.registry;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;

import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import hunternif.mc.atlas.util.BitMatrix;
import hunternif.mc.atlas.util.Log;

public class MarkerType extends IForgeRegistryEntry.Impl<MarkerType> {

	protected ResourceLocation[] icons;
	protected BitMatrix[] iconPixels;
	protected int[] iconSizes = null;
	protected int viewSize = 2, clipMin = -1000, clipMax = 1000;
	protected boolean alwaysShow = false, isTile = false, isTechnical = false;
	protected double centerX = 0.5, centerY = 0.5;
	
	public MarkerType(ResourceLocation loc, ResourceLocation... icons) {
		setRegistryName(loc);
		this.icons = icons;
	}

	public boolean isTechnical() {
		return false;
	}
	
	/**
	 * Whether the marker should be hidden
	 */
	public boolean shouldHide(boolean isHidingMarkers, int scaleIndex) {
		return shouldClip(scaleIndex) || (!alwaysShow && isHidingMarkers);
	}

	/**
	 * Whether the marker should hide due to the scale clipping
	 */
	public boolean shouldClip(int scaleIndex) {
		return !(scaleIndex >= clipMin && scaleIndex <= clipMax);
	}

	/**
	 * If the cursor is currently hovering over the marker
	 * 
	 * @param x
	 *            The X position in the marker (0-1 is the bounding box of the
	 *            render, though it may be outside that range)
	 * @param y
	 *            The Y position in the marker (0-1 is the bounding box of the
	 *            render, though it may be outside that range)
	 */
	public boolean shouldHover(double x, double y) {
		if(isTechnical() || x > 1 || x < 0 || y > 1 || y < 0)
			return false;
		if(iconPixels == null || iconPixels.length == 0 || iconIndex < 0 )
			return true;
		int iconX = (int)(iconPixels[iconIndex].getWidth()*x);
		int iconY = (int)(iconPixels[iconIndex].getHeight()*y);
		
		return iconPixels[iconIndex].get(iconX, iconY);
	}

	/**
	 * The size of the icon, in chunks
	 */
	public int viewSize() {
		return viewSize;
	}

	/**
	 * Whether the marker is a tile, and as such should scale with the map
	 * 
	 * @return
	 */
	public boolean isTile() {
		return isTile;
	}

	/**
	 * The X position (0-1) of the icon that should be at the marker location
	 * 
	 * @return
	 */
	public double getCenterX() {
		return centerX;
	}

	/**
	 * The Y position (0-1) of the icon that should be at the marker location
	 * 
	 * @return
	 */
	public double getCenterY() {
		return centerY;
	}

	/**
	 * Get the icon for the marker
	 */
	public ResourceLocation getIcon() {
		return icons.length == 0 || iconIndex < 0 ? TextureMap.LOCATION_MISSING_TEXTURE : icons[iconIndex];
	}
	
	protected int iconIndex = 0;
	
	public void calculateMip(double scale, double mapScale, double screenScale) {
		int size = (int) (8 * viewSize() * scale * viewSize());
		if (isTile) {
			size *= mapScale;
		}
		
		if (icons.length > 1) {
			int smallestSide = (int) (size * screenScale);

			int closestValue = Integer.MAX_VALUE;
			int closestIndex = -1;
			for (int i = 0; i < iconSizes.length; i++) {
				if (iconSizes[i] < closestValue && iconSizes[i] >= smallestSide) {
					closestValue = iconSizes[i];
					closestIndex = i;
				}
			}
			if (closestIndex > 0) {
				iconIndex = closestIndex;
			}
		}
	}
	
	public void resetMip() {
		iconIndex = 0;
	}
	
	public MarkerRenderInfo getRenderInfo(double scale, double mapScale, double screenScale) {
		boolean isTile = isTile();

		int size = (int) (8 * viewSize() * scale * viewSize());
		if (isTile) {
			size *= mapScale;
		}
		int x = -(int) (size * getCenterX());
		int y = -(int) (size * getCenterY());

		ResourceLocation icon = getIcon();

		return new MarkerRenderInfo(icon, x, y, size, size);
	}

	public void initMips() {
		iconSizes = new int[icons.length];
		iconPixels = new BitMatrix[icons.length];
		int ALPHA_THRESHOLD = 8;
		for (int i = 0; i < icons.length; i++) {
			iconSizes[i] = -1;
			if (icons[i] == null) {
				Log.warn("Marker %s -- Texture location is null at index %d!", getRegistryName().toString(), i);
			}
			IResource iresource = null;

			try {
				iresource = Minecraft.getMinecraft().getResourceManager().getResource(icons[i]);
				BufferedImage bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
				iconSizes[i] = Math.min(bufferedimage.getWidth(), bufferedimage.getHeight());
				BitMatrix matrix = new BitMatrix(bufferedimage.getWidth(), bufferedimage.getHeight(), false);
				
				for (int x = 0; x < bufferedimage.getWidth(); x++) {
					for (int y = 0; y < bufferedimage.getHeight(); y++) {
						
						int color = bufferedimage.getRGB(x, y);
						int alpha = (color >> 24) & 0xff;
						
						if(alpha >= ALPHA_THRESHOLD) {
							matrix.set(x, y, true);
							
							// sides
							matrix.set(x-1, y, true);
							matrix.set(x+1, y, true);
							matrix.set(x, y-1, true);
							matrix.set(x, y+1, true);
							
							// corners
							matrix.set(x+1, y+1, true);
							matrix.set(x-1, y-1, true);
							matrix.set(x+1, y-1, true);
							matrix.set(x-1, y+1, true);
						}
					}
				}
				iconPixels[i] = matrix;
				
			} catch (IOException e) {
				Log.warn(e, "Marker %s -- Error getting texture size data for index %d - %s",
						getRegistryName().toString(), i, icons[i].toString());
			} finally {
				IOUtils.closeQuietly((Closeable) iresource);
			}
		}
	}

	{ /* Setters */ }

	public MarkerType setSize(int value) {
		this.viewSize = value;
		return this;
	}

	public MarkerType setIsTile(boolean value) {
		this.isTile = value;
		return this;
	}

	public MarkerType setAlwaysShow(boolean value) {
		this.alwaysShow = value;
		return this;
	}

	public MarkerType setClip(int a, int b) {
		this.clipMin = Math.min(a, b);
		this.clipMax = Math.max(a, b);
		return this;
	}

	public MarkerType setCenter(double x, double y) {
		this.centerX = x;
		this.centerY = y;
		return this;
	}
	
	public MarkerType setIsTechnical(boolean value) {
		this.isTechnical = value;
		return this;
	}
}
