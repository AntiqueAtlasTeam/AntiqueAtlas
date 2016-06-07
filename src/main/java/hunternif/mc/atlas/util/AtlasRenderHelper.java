package hunternif.mc.atlas.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class AtlasRenderHelper {
	public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		double minU = (double)u / (double)imageWidth;
		double maxU = (double)(u + width) / (double)imageWidth;
		double minV = (double)v / (double)imageHeight;
		double maxV = (double)(v + height) / (double)imageHeight;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(x + scaleX*(double)width, y + scaleY*(double)height, 0).tex(maxU, maxV).endVertex();
		renderer.pos(x + scaleX*(double)width, y, 0).tex(maxU, minV).endVertex();
		renderer.pos(x, y, 0).tex(minU, minV).endVertex();
		renderer.pos(x, y + scaleY*(double)height, 0).tex(minU, maxV).endVertex();;
		tessellator.draw();
	}
	
	public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
		drawTexturedRect(texture, x, y, u, v, width, height, imageWidth, imageHeight, 1, 1);
	}
	
	public static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height, double scaleX, double scaleY) {
		drawTexturedRect(texture, x, y, 0, 0, width, height, width, height, scaleX, scaleY);
	}
	
	public static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height) {
		drawFullTexture(texture, x, y, width, height, 1, 1);
	}
	
	public static void drawAutotileCorner(ResourceLocation texture, int x, int y, int u, int v, int tileHalfSize) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		double minU = (double) u / 4d;
		double maxU = (double)(u + 1) / 4d;
		double minV = (double) v / 6d;
		double maxV = (double)(v + 1) / 6d;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(x + tileHalfSize, y + tileHalfSize, 0).tex(maxU, maxV).endVertex();
		renderer.pos(x + tileHalfSize, y, 0).tex(maxU, minV).endVertex();
		renderer.pos(x, y, 0).tex(minU, minV).endVertex();
		renderer.pos(x, y + tileHalfSize, 0).tex(minU, maxV).endVertex();
		tessellator.draw();
	}
	
	public static void setGLColor(int color, float alpha) {
		float r = (float)(color >> 16 & 0xff)/256f;
		float g = (float)(color >> 8 & 0xff)/256f;
		float b = (float)(color & 0xff)/256f;
		GL11.glColor4f(r, g, b, alpha);
	}
}
