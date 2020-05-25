package hunternif.mc.atlas.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AtlasRenderHelper {
	public static void drawTexturedRect(ResourceLocation texture, double x, double y, double u, double v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		float minU = (float)u / imageWidth;
		float maxU = (float)(u + width) / imageWidth;
		float minV = (float)v / imageHeight;
		float maxV = (float)(v + height) / imageHeight;
//		After testing, there is no noticeable time difference between raw OpenGL rendering,
//		and using the WorldRenderere
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		renderer.pos((int)(x + scaleX*width), (int)(y + scaleY*height), 0).tex(maxU, maxV).endVertex();
		renderer.pos((int)(x + scaleX*width), (int)y,                   0).tex(maxU, minV).endVertex();
		renderer.pos((int)x,                  (int)y,                   0).tex(minU, minV).endVertex();
		renderer.pos((int)x,                  (int)(y + scaleY*height), 0).tex(minU, maxV).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
		drawTexturedRect(texture, x, y, u, v, width, height, imageWidth, imageHeight, 1, 1);
	}

	private static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height, double scaleX, double scaleY) {
		drawTexturedRect(texture, x, y, 0, 0, width, height, width, height, scaleX, scaleY);
	}

	public static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height) {
		drawFullTexture(texture, x, y, width, height, 1, 1);
	}

	public static void drawAutotileCorner(ResourceLocation texture, int x, int y, double u, double v, int tileHalfSize) {
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		float minU = (float)u / 4;
		float maxU = (float)(u + 1) / 4;
		float minV = (float)v / 6;
		float maxV = (float)(v + 1) / 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		renderer.pos((x + tileHalfSize), (y + tileHalfSize), 0).tex(maxU, maxV).endVertex();
		renderer.pos((x + tileHalfSize),  y,                 0).tex(maxU, minV).endVertex();
		renderer.pos( x,                  y,                 0).tex(minU, minV).endVertex();
		renderer.pos( x,                 (y + tileHalfSize), 0).tex(minU, maxV).endVertex();
		tessellator.draw();
	}
}
