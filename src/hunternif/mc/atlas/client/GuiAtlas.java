package hunternif.mc.atlas.client;

import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiAtlas extends GuiScreen {
	public static final int WIDTH = 310;
	public static final int HEIGHT = 218;
	private static final int CONTENT_X = 18;
	private static final int CONTENT_Y = 12;
	
	private static final int MAP_WIDTH = WIDTH - 18*2;
	private static final int MAP_HEIGHT = 192;
	private static final int MAP_TILE_SIZE = 8;
	private static final double MAP_BLOCK_PIXEL_RATIO = 16d /*Chunk size*/ / MAP_TILE_SIZE;
	private static final int MAP_WIDTH_IN_TILES = MAP_WIDTH / MAP_TILE_SIZE;
	private static final int MAP_HEIGHT_IN_TILES = MAP_HEIGHT / MAP_TILE_SIZE;
	private static final float PLAYER_ROTATION_STEPS = 16;
	private static final int PLAYER_ICON_WIDTH = 7;
	private static final int PLAYER_ICON_HEIGHT = 8;
	
	private GuiArrowButton btnUp;
	private GuiArrowButton btnDown;
	private GuiArrowButton btnLeft;
	private GuiArrowButton btnRight;
	public static int navigateStep = 2;
	
	private GuiPositionButton btnPosition;
	
	private EntityPlayer player;
	private ItemStack stack;
	private int guiLeft;
	private int guiTop;
	
	private int mapOffsetX = 0;
	private int mapOffsetY = 0;
	
	public GuiAtlas() {
		mapOffsetX = 0;
		mapOffsetY = 0;
	}
	public GuiAtlas setAtlasItemStack(ItemStack stack) {
		this.player = Minecraft.getMinecraft().thePlayer;
		this.stack = stack;
		return this;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (this.width - WIDTH) / 2;
		guiTop = (this.height - HEIGHT) / 2;
		btnUp = GuiArrowButton.up(1, guiLeft + 148, guiTop + 10);
		btnDown = GuiArrowButton.down(2, guiLeft + 148, guiTop + 194);
		btnLeft = GuiArrowButton.left(3, guiLeft + 15, guiTop + 100);
		btnRight = GuiArrowButton.right(4, guiLeft + 283, guiTop + 100);
		btnPosition = new GuiPositionButton(5, guiLeft + 283, guiTop + 194, "Reset position");
		buttonList.add(btnUp);
		buttonList.add(btnDown);
		buttonList.add(btnLeft);
		buttonList.add(btnRight);
		buttonList.add(btnPosition);
		navigateMap(0, 0);
	}
	
	@Override
	protected void actionPerformed(GuiButton btn) {
		if (btn.equals(btnUp)) {
			navigateMap(0, -navigateStep);
		} else if (btn.equals(btnDown)) {
			navigateMap(0, navigateStep);
		} else if (btn.equals(btnLeft)) {
			navigateMap(-navigateStep, 0);
		} else if (btn.equals(btnRight)) {
			navigateMap(navigateStep, 0);
		} else if (btn.equals(btnPosition)) {
			mapOffsetX = 0;
			mapOffsetY = 0;
			btnPosition.drawButton = false;
		}
	}
	
	@Override
	public void handleKeyboardInput() {
		if (Keyboard.getEventKeyState()) {
			int key = Keyboard.getEventKey();
			if (key == Keyboard.KEY_UP) {
				navigateMap(0, -navigateStep);
			} else if (key == Keyboard.KEY_DOWN) {
				navigateMap(0, navigateStep);
			} else if (key == Keyboard.KEY_LEFT) {
				navigateMap(-navigateStep, 0);
			} else if (key == Keyboard.KEY_RIGHT) {
				navigateMap(navigateStep, 0);
			}
		}
		super.handleKeyboardInput();
	}
	
	public void navigateMap(int dx, int dy) {
		mapOffsetX += dx;
		mapOffsetY += dy;
		if (btnPosition != null) {
			if (mapOffsetX != 0 || mapOffsetY != 0) {
				btnPosition.drawButton = true;
			} else {
				btnPosition.drawButton = false;
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		AtlasRenderHelper.drawFullTexture(Textures.BOOK, guiLeft, guiTop, WIDTH, HEIGHT);
		
		if (stack == null) return;
		AtlasData data = ((ItemAtlas) stack.getItem()).getAtlasData(stack, player.worldObj);
		if (data == null) return;
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Map<ShortVec2, MapTile> tiles = data.getSeenChunksInDimension(player.dimension);
		int playerChunkX = MathHelper.floor_double(player.posX) >> 4;
		int playerChunkZ = MathHelper.floor_double(player.posZ) >> 4;
		// Find chunk coordinates of the top left corner of the map:
		ShortVec2 mapStartCoords = new ShortVec2(
				playerChunkX + mapOffsetX - MAP_WIDTH_IN_TILES/2,
				playerChunkZ + mapOffsetY - MAP_HEIGHT_IN_TILES/2);
		ShortVec2 chunkCoords = new ShortVec2(mapStartCoords);
		int screenX = guiLeft + CONTENT_X;
		int screenY;
		int u = 0;
		int v = 0;
		for (int x = 0; x < MAP_WIDTH_IN_TILES; x++) {
			screenY = guiTop + CONTENT_Y;
			chunkCoords.y = mapStartCoords.y;
			for (int z = 0; z < MAP_HEIGHT_IN_TILES; z++) {
				MapTile tile = tiles.get(chunkCoords);
				if (tile != null) {
					String texture = BiomeTextureMap.instance().getTexture(tile);
					if (tile.isSingleObject()) {
						AtlasRenderHelper.drawTexturedRect(texture, screenX, screenY, 0, 0,
								MAP_TILE_SIZE, MAP_TILE_SIZE, MAP_TILE_SIZE*2, MAP_TILE_SIZE*3);
					} else {
						// Top left corner:
						if (tile.topLeft == MapTile.CONCAVE) { u = 2; v = 0; }
						else if (tile.topLeft == MapTile.VERTICAL) { u = 0; v = 4; }
						else if (tile.topLeft == MapTile.HORIZONTAL) { u = 2; v = 2; }
						else if (tile.topLeft == MapTile.FULL) { u = 2; v = 4; } 
						else if (tile.topLeft == MapTile.CONVEX) { u = 0; v = 2; }
						drawCorner(texture, screenX, screenY, u, v);
						
						// Top right corner:
						if (tile.topRight == MapTile.CONCAVE) { u = 3; v = 0; }
						else if (tile.topRight == MapTile.VERTICAL) { u = 3; v = 4; }
						else if (tile.topRight == MapTile.HORIZONTAL) { u = 1; v = 2; }
						else if (tile.topRight == MapTile.FULL) { u = 1; v = 4; } 
						else if (tile.topRight == MapTile.CONVEX) { u = 3; v = 2; }
						drawCorner(texture, screenX + MAP_TILE_SIZE/2, screenY, u, v);
						
						// Bottom left corner:
						if (tile.bottomLeft == MapTile.CONCAVE) { u = 2; v = 1; }
						else if (tile.bottomLeft == MapTile.VERTICAL) { u = 0; v = 3; }
						else if (tile.bottomLeft == MapTile.HORIZONTAL) { u = 2; v = 5; }
						else if (tile.bottomLeft == MapTile.FULL) { u = 2; v = 3; } 
						else if (tile.bottomLeft == MapTile.CONVEX) { u = 0; v = 5; }
						drawCorner(texture, screenX, screenY + MAP_TILE_SIZE/2, u, v);
						
						// Bottom right corner:
						if (tile.bottomRight == MapTile.CONCAVE) { u = 3; v = 1; }
						else if (tile.bottomRight == MapTile.VERTICAL) { u = 3; v = 3; }
						else if (tile.bottomRight == MapTile.HORIZONTAL) { u = 1; v = 5; }
						else if (tile.bottomRight == MapTile.FULL) { u = 1; v = 3; } 
						else if (tile.bottomRight == MapTile.CONVEX) { u = 3; v = 5; }
						drawCorner(texture, screenX + MAP_TILE_SIZE/2, screenY + MAP_TILE_SIZE/2, u, v);
					}
				}
				chunkCoords.y++;
				screenY += MAP_TILE_SIZE;
			}
			chunkCoords.x++;
			screenX += MAP_TILE_SIZE;
		}
		// Overlay the frame so that edges of the map are smooth:
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
		AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, guiLeft, guiTop, WIDTH, HEIGHT);
		
		// How much the player has moved from the top left corner of the map, in pixels:
		int playerOffsetX = MathHelper.floor_double((player.posX - (mapStartCoords.x << 4)) / MAP_BLOCK_PIXEL_RATIO);
		int playerOffsetZ = MathHelper.floor_double((player.posZ - (mapStartCoords.y << 4)) / MAP_BLOCK_PIXEL_RATIO);
		if (playerOffsetX < 0) playerOffsetX = 0;
		if (playerOffsetX > MAP_WIDTH) playerOffsetX = MAP_WIDTH;
		if (playerOffsetZ < 0) playerOffsetZ = 0;
		if (playerOffsetZ > MAP_HEIGHT) playerOffsetZ = MAP_HEIGHT;
		// Draw player icon:
		GL11.glPushMatrix();
		GL11.glTranslated(CONTENT_X + guiLeft + playerOffsetX, guiTop + CONTENT_Y + playerOffsetZ, 0);
		float playerRotation = (float) Math.round(player.rotationYaw / 360f * PLAYER_ROTATION_STEPS) / PLAYER_ROTATION_STEPS * 360f;
		GL11.glRotatef(180 + playerRotation, 0, 0, 1);
		GL11.glTranslatef(-(float)PLAYER_ICON_WIDTH/2f, -(float)PLAYER_ICON_HEIGHT/2f, 0);
		AtlasRenderHelper.drawFullTexture(Textures.MAP_PLAYER, 0, 0, PLAYER_ICON_WIDTH, PLAYER_ICON_HEIGHT);
		GL11.glPopMatrix();
		
		// Draw buttons:
		super.drawScreen(mouseX, mouseY, par3);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void drawCorner(String texture, int x, int y, int u, int v) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		double minU = (double) u / 4d;
		double maxU = (double)(u + 1) / 4d;
		double minV = (double) v / 6d;
		double maxV = (double)(v + 1) / 6d;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + MAP_TILE_SIZE/2, y + MAP_TILE_SIZE/2, 0, maxU, maxV);
		tessellator.addVertexWithUV(x + MAP_TILE_SIZE/2, y, 0, maxU, minV);
		tessellator.addVertexWithUV(x, y, 0, minU, minV);
		tessellator.addVertexWithUV(x, y + MAP_TILE_SIZE/2, 0, minU, maxV);
		tessellator.draw();
	}
}
