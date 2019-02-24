package hunternif.mc.atlas.registry;


import net.minecraft.util.Identifier;

public class MarkerRenderInfo {
	public final Identifier tex;
	public final double x, y;
	public final int width, height;
	
	public MarkerRenderInfo(Identifier tex, double x, double y, int width, int height) {
		this.tex = tex;
		this.x = x; this.y = y;
		this.width = width; this.height = height;
	}
}
