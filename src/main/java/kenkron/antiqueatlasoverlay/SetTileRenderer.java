package kenkron.antiqueatlasoverlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;

/**The minimap render is a bit slow.  The function that really takes time is
 * AtlasRenderHelper.drawAutotileCorner(...).  This class makes it faster by
 * sorting the draw commands by texture, then
 * rendering all of the same textures of a map at once without re-binding.*/
public class SetTileRenderer {

	public class TileCorner{
		public int x, y, u, v;
		public TileCorner(int x, int y, int u, int v){
			this.x = x; this.y = y; this.u = u; this.v = v;
		}
	}

	public int tileHalfSize=8;

	HashMap<ResourceLocation, ArrayList<TileCorner>> subjects;

	public SetTileRenderer(int tileHalfSize){
		this.tileHalfSize=tileHalfSize;
		subjects = new HashMap<ResourceLocation, ArrayList<TileCorner>>();
	}

	public void addTileCorner(ResourceLocation texture, int x, int y, int u, int v){
		ArrayList<TileCorner> set = subjects.get(texture);
		if (set == null){
			set = new ArrayList<TileCorner>();
			subjects.put(texture, set);
		}
		set.add(new TileCorner(x, y, u, v));
	}

	@SideOnly(Side.CLIENT)
	public void draw(){
		for (ResourceLocation key: subjects.keySet()){
			ArrayList<TileCorner> tca = subjects.get(key);
			//Effectively a call to GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_94277_0_);
			Minecraft.getMinecraft().renderEngine.bindTexture(key);

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer renderer = tessellator.getBuffer();
			renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (TileCorner tc: tca){
				drawInlineAutotileCorner(tc.x, tc.y, tc.u, tc.v);
			}
			tessellator.draw();
		}
	}

	@SideOnly(Side.CLIENT)
	protected void drawInlineAutotileCorner(int x, int y, int u, int v) {
		float minU = u / 4f;
		float maxU =(u + 1) / 4f;
		float minV = v / 6f;
		float maxV =(v + 1) / 6f;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.pos(x+tileHalfSize, y+ tileHalfSize,0).tex(maxU, maxV).endVertex();
		renderer.pos(x+tileHalfSize,y,0).tex(maxU, minV).endVertex();
		renderer.pos(x,y,0).tex(minU, minV).endVertex();
		renderer.pos(x,y+ tileHalfSize,0).tex(minU, maxV).endVertex();
	}
}
