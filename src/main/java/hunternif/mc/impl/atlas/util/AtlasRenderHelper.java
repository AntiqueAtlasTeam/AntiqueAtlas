package hunternif.mc.impl.atlas.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class AtlasRenderHelper {
	public static void drawTexturedRect(MatrixStack matrices, ResourceLocation texture, double x, double y, double u, double v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		AbstractGui.blit(matrices, (int)x, (int)y, (int)(width*scaleX), (int)(height*scaleY), (int)u, (int)v, width, height, imageWidth, imageHeight);
	}

	public static void drawTexturedRect(MatrixStack matrices, ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
		drawTexturedRect(matrices, texture, x, y, u, v, width, height, imageWidth, imageHeight, 1, 1);
	}

	public static void drawFullTexture(MatrixStack matrices, ResourceLocation texture, double x, double y, int width, int height) {
		drawTexturedRect(matrices, texture, x, y, 0,0,width, height, width, height, 1, 1);
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

	public static void drawFullTexture(MatrixStack matrices, ResourceLocation texture, int x, int y, int size) {
		drawFullTexture(matrices, texture, x, y, size, size);
	}
}
