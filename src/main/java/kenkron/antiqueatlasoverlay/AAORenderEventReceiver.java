package kenkron.antiqueatlasoverlay;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.SubTile;
import hunternif.mc.atlas.client.SubTileQuartet;
import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.TileRenderIterator;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import hunternif.mc.atlas.util.Rect;

public class AAORenderEventReceiver {

	/**
	 * I know public variables can be messed with, but that's a risk I'm willing
	 * to take. Fraction of image devoted to each border.
	 */
	public float BORDER_X = 0.0f, BORDER_Y = 0.0f;
	public int TILE_SIZE = 8;

	/** Position of the minimap relative to it's corner. */
	public int X = 2, Y = 2;

	/** Dimensions of the minimap */
	public int WIDTH = GuiAtlas.WIDTH / 2, HEIGHT = GuiAtlas.HEIGHT / 2;

	/** Determines which corner to align to */
	public boolean ALIGN_RIGHT = true, ALIGN_BOTTOM = false;

	/**
	 * If true, the minimap will render only while the atlas is held, instead of
	 * rendering whenever it's in the hotbar.
	 */
	public boolean REQUIRES_HOLD = true;

	/**
	 * If true, the minimap will show
	 * If false, it will not
	 */
	public boolean ENABLED = true;
	
	/** Size of markers on the minimap */
	public int MARKER_SIZE = GuiAtlas.MARKER_SIZE / 2;


	public int PLAYER_ICON_WIDTH = 7;
	
	public int PLAYER_ICON_HEIGHT = 8;
	
	/**
	 * Number of blocks per chunk in minecraft. This is certianly stored
	 * somewhere else, but I couldn't be bothered to find it.
	 */
	public static final int CHUNK_SIZE = 16;
	
	/**new ScaledResolution(mc).getScaleFactor();*/
	private int screenScale = 1;

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void eventHandler(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
			return;
		}
		if (!ENABLED){
			return;
		}
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Integer atlas = null;
		if (REQUIRES_HOLD) {
			ItemStack stack = player.getHeldItemMainhand();
			ItemStack stack2 = player.getHeldItemOffhand();
			if (stack != null && stack.getItem() == AntiqueAtlasMod.itemAtlas) {
				atlas = new Integer(stack.getItemDamage());
			}else if (stack2 != null && stack2.getItem() == AntiqueAtlasMod.itemAtlas) {
				atlas = new Integer(stack2.getItemDamage());
			}
		} else {
			atlas = getPlayerAtlas(player);
		}
		if (atlas != null) {
			int gamewidth = event.getResolution().getScaledWidth();
			int gameheight = event.getResolution().getScaledHeight();
			// remember, y=0 is at the top
			Rect bounds = new Rect().setOrigin(X, Y);
			if (ALIGN_RIGHT) {
				bounds.minX = gamewidth - (WIDTH + X);
			}
			if (ALIGN_BOTTOM) {
				bounds.minY = gameheight - (HEIGHT + Y);
			}
			bounds.setSize(WIDTH, HEIGHT);
			drawMinimap(bounds, atlas.intValue(), player.getPositionVector(), player.getRotationYawHead(),
					player.dimension, event.getResolution());
		}
	}

	public void drawMinimap(Rect shape, int atlasID, Vec3d position, float rotation,
			int dimension, ScaledResolution res) {
		screenScale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0); // So light detail on tiles is
												// visible
		AtlasRenderHelper.drawFullTexture(Textures.BOOK, shape.minX,
				shape.minY, shape.getWidth(), shape.getHeight());
		Rect innerShape = new Rect(
				// stop it eclipse
				shape.minX + Math.round(BORDER_X * shape.getWidth()),
				shape.minY + Math.round(BORDER_Y * shape.getHeight()),
				shape.maxX - Math.round(BORDER_X * shape.getWidth()),
				shape.maxY - Math.round(BORDER_Y * shape.getHeight()));
		drawTiles(innerShape, atlasID, position, dimension, res);
		if (MARKER_SIZE>0){
			drawMarkers(innerShape, atlasID, position, dimension, res);
			int shapeMiddleX = (shape.minX + shape.maxX) / 2;
			int shapeMiddleY = (shape.minY + shape.maxY) / 2;
			drawPlayer(shapeMiddleX, shapeMiddleY, position, rotation);
		}
		// Overlay the frame so that edges of the map are smooth:
		GlStateManager.color(1, 1, 1, 1);
		AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, shape.minX,
		shape.minY, shape.getWidth(), shape.getHeight());
		GlStateManager.disableBlend();
	}

	public void drawTiles(Rect shape, int atlasID, Vec3d position,
			int dimension, ScaledResolution res) {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		// glScissor uses the default window coordinates,
		// the display window does not. We need to fix this
		glScissorGUI(shape, res);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
				atlasID, Minecraft.getMinecraft().theWorld).getDimensionData(
				dimension);

		TileRenderIterator iter = new TileRenderIterator(biomeData);
		Rect iteratorScope = getChunkCoverage(position, shape);
		iter.setScope(iteratorScope);

		iter.setStep(1);
		Vec3d chunkPosition =new Vec3d(position.xCoord
				/ CHUNK_SIZE, position.yCoord / CHUNK_SIZE, position.zCoord
				/ CHUNK_SIZE);
		int shapeMiddleX = (shape.minX + shape.maxX) / 2;
		int shapeMiddleY = (shape.minY + shape.maxY) / 2;
		SetTileRenderer renderer = new SetTileRenderer(TILE_SIZE / 2);

		while (iter.hasNext()) {
			SubTileQuartet subtiles = iter.next();
			for (SubTile subtile : subtiles) {
				if (subtile == null || subtile.tile == null)
					continue;
				// Position of this subtile (measured in chunks) relative to the
				// player
				float relativeChunkPositionX = (float) (subtile.x / 2.0
						+ iteratorScope.minX - chunkPosition.xCoord);
				float relativeChunkPositionY = (float) (subtile.y / 2.0
						+ iteratorScope.minY - chunkPosition.zCoord);
				renderer.addTileCorner(
						BiomeTextureMap.instance().getTexture(subtile.tile),
						shapeMiddleX
								+ (int) Math.floor(relativeChunkPositionX
										* TILE_SIZE),
						shapeMiddleY
								+ (int) Math.floor(relativeChunkPositionY
										* TILE_SIZE), subtile.getTextureU(),
						subtile.getTextureV());
			}
		}
		renderer.draw();
		// get GL back to normal
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.color(1, 1, 1, 1);
	}

	public void drawMarkers(Rect shape, int atlasID, Vec3d position,
			int dimension, ScaledResolution res) {

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		// glScissor uses the default window coordinates,
		// the display window does not. We need to fix this
		glScissorGUI(shape, res);

		// biomeData needed to prevent undiscovered markers from appearing
		DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
				atlasID, Minecraft.getMinecraft().theWorld).getDimensionData(
				dimension);
		DimensionMarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData
				.getData().getMarkersDataInDimension(dimension);

		// Draw global markers:
		drawMarkersData(globalMarkersData, shape, biomeData, position);

		MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(
				atlasID, Minecraft.getMinecraft().theWorld);
		DimensionMarkersData localMarkersData = null;
		if (markersData != null) {
			localMarkersData = markersData.getMarkersDataInDimension(dimension);
		}

		// Draw local markers:
		drawMarkersData(localMarkersData, shape, biomeData, position);

		// get GL back to normal
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.color(1, 1, 1, 1);
	}
	
	public void drawPlayer(float x, float y, Vec3d posisiton, float rotation){
		// Draw player icon:
		
		GlStateManager.pushMatrix(); 
		GlStateManager.translate(x, y, 0);
		GlStateManager.rotate(180 + rotation, 0, 0, 1);
		GlStateManager.translate(-PLAYER_ICON_WIDTH/ 2, -PLAYER_ICON_HEIGHT/2, 0);
		AtlasRenderHelper.drawFullTexture(Textures.PLAYER, 0, 0, PLAYER_ICON_WIDTH, PLAYER_ICON_HEIGHT); 
		GlStateManager.popMatrix();
		GlStateManager.color(1, 1, 1, 1);
	}

	protected void drawMarkersData(DimensionMarkersData markersData,
			Rect shape, DimensionData biomeData, Vec3d position) {
		
		//this will be large enough to include markers that are larger than tiles
		Rect markerShape = new Rect(shape.minX-MARKER_SIZE/2, shape.minY-MARKER_SIZE/2,
				shape.maxX+MARKER_SIZE/2, shape.maxY+MARKER_SIZE/2);
		
		Rect mcchunks = getChunkCoverage(position, markerShape);
		Rect chunks = new Rect((int)Math.floor(mcchunks.minX/MarkersData.CHUNK_STEP),
				(int)Math.floor(mcchunks.minY/MarkersData.CHUNK_STEP),
				(int)Math.ceil(mcchunks.maxX/MarkersData.CHUNK_STEP),
				(int)Math.ceil(mcchunks.maxY/MarkersData.CHUNK_STEP));
		
		int shapeMiddleX = (shape.minX + shape.maxX) / 2;
		int shapeMiddleY = (shape.minY + shape.maxY) / 2;

		for (int x = chunks.minX; x <= chunks.maxX; x++) {
			for (int z = chunks.minY; z <= chunks.maxY; z++) {
				//A marker chunk is greater than a Minecraft chunk
				List<Marker> markers = markersData.getMarkersAtChunk(
						Math.round(x), 
						Math.round(z));
				if (markers == null)
					continue;
				for (Marker marker : markers) {
					// Position of this marker relative to the player
					// Rounded to the nearest even number
					int relativeChunkPositionX = TILE_SIZE*(marker.getX() - 2*(int)Math.floor(position.xCoord/2))
							/ CHUNK_SIZE;
					int relativeChunkPositionY = TILE_SIZE*(marker.getZ() - 2*(int)Math.floor(position.zCoord/2))
							/ CHUNK_SIZE;
					int guiX = (int)Math.floor(shapeMiddleX - MARKER_SIZE/2 + relativeChunkPositionX);
					int guiY = (int)Math.floor(shapeMiddleY - MARKER_SIZE/2 + relativeChunkPositionY);
					renderMarker(marker, guiX, guiY, biomeData);
				}
			}
		}
	}

	protected void renderMarker(Marker marker, int x, int y,
			DimensionData biomeData) {
		if (!marker.isVisibleAhead()
				&& !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
			return;
		}
		GlStateManager.color(1, 1, 1, 1);
		MarkerRenderInfo info = marker.getType().getRenderInfo(1, TILE_SIZE, screenScale);
		AtlasRenderHelper.drawFullTexture(info.tex, x, y, MARKER_SIZE,
				MARKER_SIZE);
//		 AtlasRenderHelper.drawFullTexture(MarkerTextureMap.instance()
//		 .getTexture(marker.getType()), x, y, MARKER_SIZE, MARKER_SIZE);
	}

	protected Rect getChunkCoverage(Vec3d position, Rect windowShape) {
		int CHUNKSIZE = 16;
		int minChunkX = (int) Math.floor(position.xCoord / CHUNKSIZE
				- windowShape.getWidth() / (2f * TILE_SIZE));
		minChunkX -= 1;// IDK
		int minChunkY = (int) Math.floor(position.zCoord / CHUNKSIZE
				- windowShape.getHeight() / (2f * TILE_SIZE));
		minChunkY -= 1;// IDK
		int maxChunkX = (int) Math.ceil(position.xCoord / CHUNKSIZE
				+ windowShape.getWidth() / (2f * TILE_SIZE));
		maxChunkX += 1;
		int maxChunkY = (int) Math.ceil(position.zCoord / CHUNKSIZE
				+ windowShape.getHeight() / (2f * TILE_SIZE));
		maxChunkY += 1;
		return new Rect(minChunkX, minChunkY, maxChunkX, maxChunkY);
	}

	/** Calls GL11.glScissor, but uses GUI coordinates */
	protected void glScissorGUI(Rect shape, ScaledResolution res) {
		// glScissor uses the default window coordinates,
		// the display window does not. We need to fix this
		int mcHeight = Minecraft.getMinecraft().displayHeight;
		float scissorScaleX = Minecraft.getMinecraft().displayWidth * 1.0f
				/ res.getScaledWidth();
		float scissorScaleY = mcHeight * 1.0f / res.getScaledHeight();
		GL11.glScissor((int) (shape.minX * scissorScaleX),
				(int) (mcHeight - shape.maxY * scissorScaleY),
				(int) (shape.getWidth() * scissorScaleX),
				(int) (shape.getHeight() * scissorScaleY));
	}

	/**
	 * Convenience method that returns the first atlas ID for all atlas items
	 * the player is currently carrying in the hotbar. Returns null if there are
	 * none.
	 **/
	public static Integer getPlayerAtlas(EntityPlayer player) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = player.inventory.mainInventory[i];
			if (stack != null && stack.getItem() == AntiqueAtlasMod.itemAtlas) {
				return new Integer(stack.getItemDamage());
			}
		}
		return null;
	}
}
