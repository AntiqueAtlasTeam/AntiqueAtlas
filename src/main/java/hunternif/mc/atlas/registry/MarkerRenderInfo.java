package hunternif.mc.atlas.registry;


import net.minecraft.util.ResourceLocation;

public class MarkerRenderInfo {
	public final ResourceLocation tex;
	public final double x, y;
	public final int width, height;
	
	public MarkerRenderInfo(ResourceLocation tex, double x, double y, int width, int height) {
		this.tex = tex;
		this.x = x; this.y = y;
		this.width = width; this.height = height;
	}
}
