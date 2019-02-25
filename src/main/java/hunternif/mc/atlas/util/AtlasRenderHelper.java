package hunternif.mc.atlas.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class AtlasRenderHelper {
	public static void drawTexturedRect(Identifier texture, double x, double y, double u, double v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		double minU = u / imageWidth;
		double maxU = (u + width) / imageWidth;
		double minV = v / imageHeight;
		double maxV = (v + height) / imageHeight;
//		After testing, there is no noticeable time difference between raw OpenGL rendering,
//		and using the WorldRenderere
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBufferBuilder();
		renderer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
		renderer.vertex((int)(x + scaleX*width), (int)(y + scaleY*height), 0).texture(maxU, maxV).next();
		renderer.vertex((int)(x + scaleX*width), (int)y,                   0).texture(maxU, minV).next();
		renderer.vertex((int)x,                  (int)y,                   0).texture(minU, minV).next();
		renderer.vertex((int)x,                  (int)(y + scaleY*height), 0).texture(minU, maxV).next();
		tessellator.draw();
	}
	
	public static void drawTexturedRect(Identifier texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
		drawTexturedRect(texture, x, y, u, v, width, height, imageWidth, imageHeight, 1, 1);
	}
	
	private static void drawFullTexture(Identifier texture, double x, double y, int width, int height, double scaleX, double scaleY) {
		drawTexturedRect(texture, x, y, 0, 0, width, height, width, height, scaleX, scaleY);
	}
	
	public static void drawFullTexture(Identifier texture, double x, double y, int width, int height) {
		drawFullTexture(texture, x, y, width, height, 1, 1);
	}

	public static void drawAutotileCorner(Identifier texture, int x, int y, double u, double v, int tileHalfSize) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		double minU =  u / 4;
		double maxU = (u + 1) / 4;
		double minV =  v / 6;
		double maxV = (v + 1) / 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBufferBuilder();
		renderer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV);
		renderer.vertex((x + tileHalfSize), (y + tileHalfSize), 0).texture(maxU, maxV).next();
		renderer.vertex((x + tileHalfSize),  y,                 0).texture(maxU, minV).next();
		renderer.vertex( x,                  y,                 0).texture(minU, minV).next();
		renderer.vertex( x,                 (y + tileHalfSize), 0).texture(minU, maxV).next();
		tessellator.draw();
	}
	
	public static void setGLColor(int color, float alpha) {
		float r = (float)(color >> 16 & 0xff)/256f;
		float g = (float)(color >> 8 & 0xff)/256f;
		float b = (float)(color & 0xff)/256f;
		GlStateManager.color4f(r, g, b, alpha);
	}
}
