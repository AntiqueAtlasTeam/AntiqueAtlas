package hunternif.mc.atlas.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class AtlasRenderHelper {
	public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		double minU = (double)u / (double)imageWidth;
		double maxU = (double)(u + width) / (double)imageWidth;
		double minV = (double)v / (double)imageHeight;
		double maxV = (double)(v + height) / (double)imageHeight;
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(maxU, maxV);
		GL11.glVertex2d(x + scaleX*(double)width, y + scaleY*(double)height);
		GL11.glTexCoord2d(maxU, minV);
		GL11.glVertex2d(x + scaleX*(double)width, y);
		GL11.glTexCoord2d(minU, minV);
		GL11.glVertex2d(x,y);
		GL11.glTexCoord2d(minU, maxV);
		GL11.glVertex2d(x, y + scaleY*(double)height);
		GL11.glEnd();
//		Tessellator tessellator = Tessellator.getInstance();
//		WorldRenderer renderer = tessellator.getWorldRenderer();
//		renderer.begin(GL11.GL_QUADS, renderer.getVertexFormat());
//		int[] vertexData = {(int)(x + scaleX*(double)width), (int)(y + scaleY*(double)height), 0, (int)maxU, (int)maxV};
//		renderer.addVertexData(vertexData);
//		int[] vertexData2 = {(int)(x + scaleX*(double)width), (int)y, 0, (int)maxU, (int)minV};
//		renderer.addVertexData(vertexData2);
//		int[] vertexData3 = {(int)x, (int)y, 0, (int)minU, (int)minV};
//		renderer.addVertexData(vertexData3);
//		int[] vertexData4 = {(int)x, (int)(y + scaleY*(double)height), 0, (int)minU, (int)maxV};
//		renderer.addVertexData(vertexData4);
////		renderer.startDrawingQuads();
////		renderer.addVertexWithUV(x + scaleX*(double)width, y + scaleY*(double)height, 0, maxU, maxV);
////		renderer.addVertexWithUV(x + scaleX*(double)width, y, 0, maxU, minV);
////		renderer.addVertexWithUV(x, y, 0, minU, minV);
////		renderer.addVertexWithUV(x, y + scaleY*(double)height, 0, minU, maxV);
//		tessellator.draw();
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
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(maxU, maxV);
		GL11.glVertex2d(x + tileHalfSize, y + tileHalfSize);
		GL11.glTexCoord2d(maxU, minV);
		GL11.glVertex2d(x + tileHalfSize, y);
		GL11.glTexCoord2d(minU, minV);
		GL11.glVertex2d(x,y);
		GL11.glTexCoord2d(minU, maxV);
		GL11.glVertex2d(x, y + tileHalfSize);
		GL11.glEnd();
//		Tessellator tessellator = Tessellator.getInstance();
//		WorldRenderer renderer = tessellator.getWorldRenderer();
//		VertexFormat vf = new VertexFormat(renderer.getVertexFormat());
//		vf= new VertexFormat();
//		renderer.begin(GL11.GL_QUADS, vf);
//		int[] vertexData = {(int)(x + tileHalfSize), (int)(y + tileHalfSize), 0, (int)maxU, (int)maxV};
//		renderer.addVertexData(vertexData);
//		int[] vertexData2 = {(int)(x + tileHalfSize), (int)y, 0, (int)maxU, (int)minV};
//		renderer.addVertexData(vertexData2);
//		int[] vertexData3 = {(int)x, (int)y, 0, (int)minU, (int)minV};
//		renderer.addVertexData(vertexData3);
//		int[] vertexData4 = {(int)x, (int)(y + tileHalfSize), 0, (int)minU, (int)maxV};
//		renderer.addVertexData(vertexData4);
//		renderer.endVertex();
////		renderer.startDrawingQuads();
////		renderer.addVertexWithUV(x + tileHalfSize, y + tileHalfSize, 0, maxU, maxV);
////		renderer.addVertexWithUV(x + tileHalfSize, y, 0, maxU, minV);
////		renderer.addVertexWithUV(x, y, 0, minU, minV);
////		renderer.addVertexWithUV(x, y + tileHalfSize, 0, minU, maxV);
//		tessellator.draw();
	}
	
	public static void setGLColor(int color, float alpha) {
		float r = (float)(color >> 16 & 0xff)/256f;
		float g = (float)(color >> 8 & 0xff)/256f;
		float b = (float)(color & 0xff)/256f;
		GL11.glColor4f(r, g, b, alpha);
	}
}
