package hunternif.mc.atlas.client.gui;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

import hunternif.mc.atlas.util.ExportImageUtil;

public enum ExportProgressOverlay {
	INSTANCE;
	
	@SubscribeEvent
	public void draw(RenderGameOverlayEvent.Post event) {
		int x = event.getResolution().getScaledWidth() - 40, y = event.getResolution().getScaledHeight() - 20, barWidth = 50, barHeight = 2;
		
		ExportUpdateListener l = ExportUpdateListener.INSTANCE;
		
		if(event.getType() != ElementType.ALL || !ExportImageUtil.isExporting)
			return;
		
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		
		int s = 2;
		
		GlStateManager.scale(1.0/s, 1.0/s, 1);
		
		int headerWidth = font.getStringWidth(l.header);
		font.drawStringWithShadow(l.header, ( x )*s -headerWidth/2, ( y )*s - 14, 0xffffff);
		int statusWidth = font.getStringWidth(l.status);
		font.drawStringWithShadow(l.status, ( x )*s -statusWidth/2, ( y )*s, 0xffffff);
		
		GlStateManager.scale(s, s, 1);
		y += 7;
		
		x -= barWidth/2;
		double p = l.currentProgress/l.maxProgress;
		if(l.maxProgress < 0)
			p = 0;
		
		GlStateManager.disableTexture2D();
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
		vb.pos(x, y, 0)						.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x, y+barHeight, 0)			.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x+barWidth, y+barHeight, 0)	.color(0.5f, 0.5f, 0.5f, 1).endVertex();
		vb.pos(x+barWidth, y, 0)			.color(0.5f, 0.5f, 0.5f, 1).endVertex();

		vb.pos(x, y, 0)						.color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x, y+barHeight, 0)			.color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x+barWidth*p, y+barHeight, 0).color(0.5f, 1, 0.5f, 1).endVertex();
		vb.pos(x+barWidth*p, y, 0)			.color(0.5f, 1, 0.5f, 1).endVertex();
		
		tessellator.draw();
		
		GlStateManager.enableTexture2D();
	}
	
}
