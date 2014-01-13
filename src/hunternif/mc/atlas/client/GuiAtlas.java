package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ClientProxy;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import hunternif.mc.atlas.util.ExportImageUtil;
import hunternif.mc.atlas.util.ShortVec2;

import java.io.File;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

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
	
	/** Pause between after the arrow button is pressed and continuous
	 * navigation starts, in ticks. */
	private static final int BUTTON_PAUSE = 8;
	
	/** Arrow buttons for navigating the map view via mouse clicks. */
	private GuiArrowButton btnUp, btnDown, btnLeft, btnRight;
	/** How much the map view is offset, in chunks, per click (or per tick). */
	public static int navigateStep = 2;
	
	/** Button for exporting PNG image of the Atlas's contents. */
	private GuiButton btnExportPng;
	
	/** Button for restoring player's position at the center of the Atlas. */
	private GuiPositionButton btnPosition;
	
	/** The button which is currently being pressed. Used for continuous
	 * navigation using the arrow buttons. */
	private GuiButton selectedButton = null;
	
	/** Time in world ticks when the button was pressed. Used to create a pause
	 * before continuous navigation using the arrow buttons. */
	private long timeButtonPressed = 0;
	
	/** Progress bar for exporting images. */
	private ProgressBarOverlay progressBar = new ProgressBarOverlay(100, 2);
	private volatile boolean isExporting = false;
	
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
		btnExportPng = new GuiButton(6, width - 80, height - 20, 80, 20, "Export Image");
		buttonList.add(btnUp);
		buttonList.add(btnDown);
		buttonList.add(btnLeft);
		buttonList.add(btnRight);
		buttonList.add(btnPosition);
		buttonList.add(btnExportPng);
		navigateMap(0, 0);
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	protected void actionPerformed(GuiButton btn) {
		selectedButton = btn;
		if (btn.equals(btnPosition)) {
			mapOffsetX = 0;
			mapOffsetY = 0;
			btnPosition.drawButton = false;
		} else if (btn.equals(btnExportPng) && stack != null) {
			progressBar.reset();
			new Thread(new Runnable() {
				@Override
				public void run() {
					exportImage(stack.copy());
				}
			}).start();
		}
		
		// Navigate once, before enabling pause:
		navigateByButton(selectedButton);
		timeButtonPressed = player.worldObj.getTotalWorldTime();
	}
	
	/** Opens a dialog window to select which file to save to, then performs
	 * rendering of the map of current dimension into a PNG image. */
	private void exportImage(ItemStack stack) {
		isExporting = true;
		// Default file name is "Atlas <N>.png"
		File file = ExportImageUtil.selectPngFileToSave("Atlas " + stack.getItemDamage());
		if (file != null) {
			AntiqueAtlasMod.logger.info("Exporting image from Atlas #" +
					stack.getItemDamage() +	" to file " + file.getAbsolutePath());
			AtlasData data = ((ItemAtlas) stack.getItem()).getAtlasData(stack, player.worldObj);
			ExportImageUtil.exportPngImage(data.getDimensionData(player.dimension), file, progressBar);
			AntiqueAtlasMod.logger.info("Finished exporting image");
		}
		isExporting = false;
	}
	
	@Override
	public void handleKeyboardInput() {
		if (Keyboard.getEventKeyState()) {
			int key = Keyboard.getEventKey();
			if (key == Keyboard.KEY_UP || key == mc.gameSettings.keyBindForward.keyCode) {
				navigateMap(0, -navigateStep);
			} else if (key == Keyboard.KEY_DOWN || key == mc.gameSettings.keyBindBack.keyCode) {
				navigateMap(0, navigateStep);
			} else if (key == Keyboard.KEY_LEFT || key == mc.gameSettings.keyBindLeft.keyCode) {
				navigateMap(-navigateStep, 0);
			} else if (key == Keyboard.KEY_RIGHT || key == mc.gameSettings.keyBindRight.keyCode) {
				navigateMap(navigateStep, 0);
			}
		}
		super.handleKeyboardInput();
	}
	
	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseState) {
		super.mouseMovedOrUp(mouseX, mouseY, mouseState);
		selectedButton = null;
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		if (player.worldObj.getTotalWorldTime() > timeButtonPressed + BUTTON_PAUSE) {
			navigateByButton(selectedButton);
		}
	}
	
	/** Offset the map view depending on which button was pressed. */
	private void navigateByButton(GuiButton btn) {
		if (btn == null) return;
		if (btn.equals(btnUp)) {
			navigateMap(0, -navigateStep);
		} else if (btn.equals(btnDown)) {
			navigateMap(0, navigateStep);
		} else if (btn.equals(btnLeft)) {
			navigateMap(-navigateStep, 0);
		} else if (btn.equals(btnRight)) {
			navigateMap(navigateStep, 0);
		}
	}
	
	/** Offset the map view by given values. */
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
					ResourceLocation texture = BiomeTextureMap.instance().getTexture(tile);
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
						AtlasRenderHelper.drawAutotileCorner(texture, screenX, screenY, u, v, MAP_TILE_SIZE/2);
						
						// Top right corner:
						if (tile.topRight == MapTile.CONCAVE) { u = 3; v = 0; }
						else if (tile.topRight == MapTile.VERTICAL) { u = 3; v = 4; }
						else if (tile.topRight == MapTile.HORIZONTAL) { u = 1; v = 2; }
						else if (tile.topRight == MapTile.FULL) { u = 1; v = 4; } 
						else if (tile.topRight == MapTile.CONVEX) { u = 3; v = 2; }
						AtlasRenderHelper.drawAutotileCorner(texture, screenX + MAP_TILE_SIZE/2, screenY, u, v, MAP_TILE_SIZE/2);
						
						// Bottom left corner:
						if (tile.bottomLeft == MapTile.CONCAVE) { u = 2; v = 1; }
						else if (tile.bottomLeft == MapTile.VERTICAL) { u = 0; v = 3; }
						else if (tile.bottomLeft == MapTile.HORIZONTAL) { u = 2; v = 5; }
						else if (tile.bottomLeft == MapTile.FULL) { u = 2; v = 3; } 
						else if (tile.bottomLeft == MapTile.CONVEX) { u = 0; v = 5; }
						AtlasRenderHelper.drawAutotileCorner(texture, screenX, screenY + MAP_TILE_SIZE/2, u, v, MAP_TILE_SIZE/2);
						
						// Bottom right corner:
						if (tile.bottomRight == MapTile.CONCAVE) { u = 3; v = 1; }
						else if (tile.bottomRight == MapTile.VERTICAL) { u = 3; v = 3; }
						else if (tile.bottomRight == MapTile.HORIZONTAL) { u = 1; v = 5; }
						else if (tile.bottomRight == MapTile.FULL) { u = 1; v = 3; } 
						else if (tile.bottomRight == MapTile.CONVEX) { u = 3; v = 5; }
						AtlasRenderHelper.drawAutotileCorner(texture, screenX + MAP_TILE_SIZE/2, screenY + MAP_TILE_SIZE/2, u, v, MAP_TILE_SIZE/2);
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
		
		// Draw progress overlay:
		if (isExporting) {
			drawDefaultBackground();
			progressBar.draw((width - 100)/2, height/2 - 20);
			fontRenderer.drawStringWithShadow("Exporting", width/2 - 26, height/2 - 34, 0xffffff);
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
}
