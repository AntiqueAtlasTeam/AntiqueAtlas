package hunternif.mc.atlas.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class AtlasRenderHelper {
	public static void drawTexturedRect(String texture, int x, int y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		double minU = (double)u / (double)imageWidth;
		double maxU = (double)(u + width) / (double)imageWidth;
		double minV = (double)v / (double)imageHeight;
		double maxV = (double)(v + height) / (double)imageHeight;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + width, y + height, 0, maxU, maxV);
		tessellator.addVertexWithUV(x + width, y, 0, maxU, minV);
		tessellator.addVertexWithUV(x, y, 0, minU, minV);
		tessellator.addVertexWithUV(x, y + height, 0, minU, maxV);
		tessellator.draw();
	}
	
	public static void drawFullTexture(String texture, int x, int y, int width, int height) {
		drawTexturedRect(texture, x, y, 0, 0, width, height, width, height);
	}
	
	public static void setGLColor(int color, float alpha) {
		float r = (float)(color >> 16 & 0xff)/256f;
		float g = (float)(color >> 8 & 0xff)/256f;
		float b = (float)(color & 0xff)/256f;
		GL11.glColor4f(r, g, b, alpha);
	}
}
