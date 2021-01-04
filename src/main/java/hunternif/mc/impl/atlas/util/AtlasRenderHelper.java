package hunternif.mc.impl.atlas.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class AtlasRenderHelper {
    public static void drawTexturedRect(MatrixStack matrices, Identifier texture, double x, double y, double u, double v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        DrawableHelper.drawTexture(matrices, (int) x, (int) y, (int) (width * scaleX), (int) (height * scaleY), (int) u, (int) v, width, height, imageWidth, imageHeight);
    }

    public static void drawTexturedRect(MatrixStack matrices, Identifier texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
        drawTexturedRect(matrices, texture, x, y, u, v, width, height, imageWidth, imageHeight, 1, 1);
    }

    public static void drawFullTexture(MatrixStack matrices, Identifier texture, double x, double y, int width, int height) {
        drawTexturedRect(matrices, texture, x, y, 0, 0, width, height, width, height, 1, 1);
    }

    public static void drawAutotileCorner(Identifier texture, int x, int y, double u, double v, int tileHalfSize) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        float minU = (float) u / 4;
        float maxU = (float) (u + 1) / 4;
        float minV = (float) v / 6;
        float maxV = (float) (v + 1) / 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        renderer.vertex((x + tileHalfSize), (y + tileHalfSize), 0).texture(maxU, maxV).next();
        renderer.vertex((x + tileHalfSize), y, 0).texture(maxU, minV).next();
        renderer.vertex(x, y, 0).texture(minU, minV).next();
        renderer.vertex(x, (y + tileHalfSize), 0).texture(minU, maxV).next();
        tessellator.draw();
    }

    public static void drawFullTexture(MatrixStack matrices, Identifier texture, int x, int y, int size) {
        drawFullTexture(matrices, texture, x, y, size, size);
    }
}
