package hunternif.mc.atlas.marker;

import java.util.Arrays;

import net.minecraft.util.ResourceLocation;

public class MarkerTypeData {
	
	public int size, baseTextureSize;
	public int minScale = -100, maxScale = 100;
	public float offsetX, offsetY;
	public boolean showAsTile, canHover, alwaysShow, shouldClipZoom;
	public ResourceLocation[] mips;
	
	public MarkerTypeData(boolean canHover, boolean alwaysShow, boolean showAsTile) {
		this.canHover = canHover;
		this.alwaysShow = alwaysShow;
	}

	public void setShowAsTileData(int size, float offsetX, float offsetY, int baseTextureSize, ResourceLocation... mips) {
		this.size = size;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.baseTextureSize = baseTextureSize;
		this.mips = mips;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!(obj instanceof MarkerTypeData))
			return false;
		
		MarkerTypeData other = (MarkerTypeData)obj;
		if( this.size == other.size &&
			this.offsetX == other.offsetX &&
			this.offsetY == other.offsetY &&
			this.canHover == other.canHover &&
			this.alwaysShow == other.alwaysShow &&
			this.baseTextureSize == other.baseTextureSize &&
			this.shouldClipZoom == other.shouldClipZoom &&
			this.minScale == other.minScale &&
			this.maxScale == other.maxScale &&
			Arrays.equals(this.mips, other.mips)
		) {
			return true;
		}
		
		return false;
	}

	public MarkerTypeData setSize(int size) {
		this.size = size;
		return this;
	}

	public MarkerTypeData setBaseTextureSize(int baseTextureSize) {
		this.baseTextureSize = baseTextureSize;
		return this;
	}

	public MarkerTypeData setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public MarkerTypeData setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public MarkerTypeData setShowAsTile(boolean showAsTile) {
		this.showAsTile = showAsTile;
		return this;
	}

	public MarkerTypeData setCanHover(boolean canHover) {
		this.canHover = canHover;
		return this;
	}

	public MarkerTypeData setAlwaysShow(boolean alwaysShow) {
		this.alwaysShow = alwaysShow;
		return this;
	}

	public MarkerTypeData setMips(ResourceLocation... mips) {
		this.mips = mips;
		return this;
	}
	
	/**
	 * Scale level 0 is 1x, 1 is 2x, and -1 is 1/2x
	 * 
	 * Automatically selects the min and max values
	 */
	public MarkerTypeData setClip(int a, int b) {
		minScale = Math.min(a, b); maxScale = Math.max(a, b);
		return this;
	}
	
	public MarkerTypeData setShouldClip(boolean shouldClip) {
		this.shouldClipZoom = shouldClip;
		return this;
	}
}
