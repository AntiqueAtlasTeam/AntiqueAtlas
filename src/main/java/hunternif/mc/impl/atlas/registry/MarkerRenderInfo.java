package hunternif.mc.impl.atlas.registry;


import hunternif.mc.impl.atlas.client.texture.ITexture;

public class MarkerRenderInfo {
	public final ITexture tex;
	public final int x, y;
	public final int width, height;
	
	public MarkerRenderInfo(ITexture tex, int x, int y, int width, int height) {
		this.tex = tex;
		this.x = x; this.y = y;
		this.width = width; this.height = height;
	}
}
