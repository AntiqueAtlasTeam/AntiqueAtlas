package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.BiomeTextureMap;
import hunternif.mc.atlas.core.MapTile;
import hunternif.mc.atlas.marker.GlobalMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkerTextureMap;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import hunternif.mc.atlas.util.ExportImageUtil;
import hunternif.mc.atlas.util.ShortVec2;

import java.io.File;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiAtlas extends GuiScreen {
	public static final int WIDTH = 310;
	public static final int HEIGHT = 218;
	private static final int CONTENT_X = 17;
	private static final int CONTENT_Y = 11;
	
	private static final int MAP_WIDTH = WIDTH - 17*2;
	private static final int MAP_HEIGHT = 194;
	private static final int MAP_TILE_SIZE = 8;
	private static final double BLOCK_TO_PIXEL_RATIO = 16d /*Chunk size*/ / MAP_TILE_SIZE;
	private static final int MAP_WIDTH_IN_TILES = MAP_WIDTH / MAP_TILE_SIZE;
	private static final int MAP_HEIGHT_IN_TILES = MAP_HEIGHT / MAP_TILE_SIZE;
	private static final float PLAYER_ROTATION_STEPS = 16;
	private static final int PLAYER_ICON_WIDTH = 7;
	private static final int PLAYER_ICON_HEIGHT = 8;
	
	private static final int MARKER_ICON_WIDTH = 16;
	private static final int MARKER_ICON_HEIGHT = 16;
	
	/** Pause between after the arrow button is pressed and continuous
	 * navigation starts, in ticks. */
	private static final int BUTTON_PAUSE = 8;
	
	/** Arrow buttons for navigating the map view via mouse clicks. */
	private GuiArrowButton btnUp, btnDown, btnLeft, btnRight;
	/** How much the map view is offset, in blocks, per click (or per tick). */
	public static int navigateStep = 24;
	
	/** Button for exporting PNG image of the Atlas's contents. */
	private GuiButton btnExportPng;
	
	/** Button for placing a marker at current position, local to this Atlas instance. */
	private GuiMarkerButton btnMarker;
	
	/** Button for restoring player's position at the center of the Atlas. */
	private GuiPositionButton btnPosition;
	
	/** The button which is currently being pressed. Used for continuous
	 * navigation using the arrow buttons. */
	private GuiButton selectedButton = null;
	
	/** Time in world ticks when the button was pressed. Used to create a pause
	 * before continuous navigation using the arrow buttons. */
	private long timeButtonPressed = 0;
	
	/** Set to true when dragging the map view. */
	private boolean isDragging = false;
	/** The starting cursor position when dragging. */
	private int dragMouseX, dragMouseY;
	/** Map offset at the beginning of drag. */
	private int dragMapOffsetX, dragMapOffsetY;
	
	/** Offset of the top left corner of the tile at (0, 0) from the center of
	 * the map drawing area, in pixels. */
	private int mapOffsetX, mapOffsetY;
	/** If true, the player's icon will be in the center of the GUI, and the
	 * offset of the tiles will be calculated accordingly. Otherwise it's the
	 * position of the player that will be calculated with respect to the
	 * offset. */
	private boolean followPlayer = true;
	
	/** Temporary set for loading markers in chunk. */
	private final SortedSet<Marker> tempMarkers = new TreeSet<Marker>();
	
	/** Progress bar for exporting images. */
	private ProgressBarOverlay progressBar = new ProgressBarOverlay(100, 2);
	private volatile boolean isExporting = false;
	
	private EntityPlayer player;
	private ItemStack stack;
	/** Coordinates of the top left corner of the GUI on the screen. */
	private int guiLeft, guiTop;
	/** Coordinate scale factor relative to the actual screen size. */
	private int scale;
	
	public GuiAtlas() {
		followPlayer = true;
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
		btnPosition.drawButton = !followPlayer;
		btnExportPng = new GuiButton(6, width - 80, height - 20, 80, 20, "Export Image");
		btnMarker = new GuiMarkerButton(7, width-20, height-42, 20, 20);
		buttonList.add(btnUp);
		buttonList.add(btnDown);
		buttonList.add(btnLeft);
		buttonList.add(btnRight);
		buttonList.add(btnPosition);
		buttonList.add(btnExportPng);
		buttonList.add(btnMarker);
		Keyboard.enableRepeatEvents(true);
		scale = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight).getScaleFactor();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseState) {
		// If clicked on the map, start dragging
		int mapX = (width - MAP_WIDTH)/2;
		int mapY = (height - MAP_HEIGHT)/2;
		if (mouseX >= mapX && mouseX <= mapX + MAP_WIDTH &&
				mouseY >= mapY && mouseY <= mapY + MAP_HEIGHT) {
			isDragging = true;
			dragMouseX = mouseX;
			dragMouseY = mouseY;
			dragMapOffsetX = mapOffsetX;
			dragMapOffsetY = mapOffsetY;
		}
		// If clicked on a button, dragging will be cancelled in actionPerformed()
		super.mouseClicked(mouseX, mouseY, mouseState);
	}
	
	@Override
	protected void actionPerformed(GuiButton btn) {
		isDragging = false;
		selectedButton = btn;
		if (btn.equals(btnPosition)) {
			followPlayer = true;
			btnPosition.drawButton = false;
		} else if (btn.equals(btnExportPng) && stack != null) {
			progressBar.reset();
			new Thread(new Runnable() {
				@Override
				public void run() {
					exportImage(stack.copy());
				}
			}).start();
		} else if (btn.equals(btnMarker) && stack != null) {
			AtlasAPI.getMarkerAPI().putMarker(player.worldObj, player.dimension, stack.getItemDamage(),
					"red_x", "Test marker", (int)player.posX, (int)player.posZ);
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
		File file = ExportImageUtil.selectPngFileToSave("Atlas " + stack.getItemDamage(), progressBar);
		if (file != null) {
			AntiqueAtlasMod.logger.info("Exporting image from Atlas #" +
					stack.getItemDamage() +	" to file " + file.getAbsolutePath());
			AtlasData data = AntiqueAtlasMod.itemAtlas.getAtlasData(stack, player.worldObj);
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
				navigateMap(0, navigateStep);
			} else if (key == Keyboard.KEY_DOWN || key == mc.gameSettings.keyBindBack.keyCode) {
				navigateMap(0, -navigateStep);
			} else if (key == Keyboard.KEY_LEFT || key == mc.gameSettings.keyBindLeft.keyCode) {
				navigateMap(navigateStep, 0);
			} else if (key == Keyboard.KEY_RIGHT || key == mc.gameSettings.keyBindRight.keyCode) {
				navigateMap(-navigateStep, 0);
			}
		}
		super.handleKeyboardInput();
	}
	
	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseState) {
		super.mouseMovedOrUp(mouseX, mouseY, mouseState);
		selectedButton = null;
		isDragging = false;
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int lastMouseButton, long timeSinceMouseClick) {
		super.mouseClickMove(mouseX, mouseY, lastMouseButton, timeSinceMouseClick);
		if (isDragging) {
			followPlayer = false;
			btnPosition.drawButton = true;
			mapOffsetX = dragMapOffsetX + mouseX - dragMouseX;
			mapOffsetY = dragMapOffsetY + mouseY - dragMouseY;
		}
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		if (followPlayer) {
			mapOffsetX = (int)(- player.posX / BLOCK_TO_PIXEL_RATIO);
			mapOffsetY = (int)(- player.posZ / BLOCK_TO_PIXEL_RATIO);
		}
		if (player.worldObj.getTotalWorldTime() > timeButtonPressed + BUTTON_PAUSE) {
			navigateByButton(selectedButton);
		}
	}
	
	/** Offset the map view depending on which button was pressed. */
	private void navigateByButton(GuiButton btn) {
		if (btn == null) return;
		if (btn.equals(btnUp)) {
			navigateMap(0, navigateStep);
		} else if (btn.equals(btnDown)) {
			navigateMap(0, -navigateStep);
		} else if (btn.equals(btnLeft)) {
			navigateMap(navigateStep, 0);
		} else if (btn.equals(btnRight)) {
			navigateMap(-navigateStep, 0);
		}
	}
	
	/** Offset the map view by given values, in blocks. */
	public void navigateMap(int dx, int dy) {
		mapOffsetX += dx;
		mapOffsetY += dy;
		followPlayer = false;
		btnPosition.drawButton = true;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		GL11.glColor4f(1, 1, 1, 1);
		AtlasRenderHelper.drawFullTexture(Textures.BOOK, guiLeft, guiTop, WIDTH, HEIGHT);
		
		if (stack == null) return;
		AtlasData data = AntiqueAtlasMod.itemAtlas.getAtlasData(stack, player.worldObj);
		if (data == null) return;
		
		GlobalMarkersData globalMarkers = AntiqueAtlasMod.globalMarkersData.getData();
		//TODO: retrieve local markers data properly
		MarkersData localMarkers = AntiqueAtlasMod.itemAtlas.getClientMarkersData(stack.getItemDamage());
		
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((guiLeft + CONTENT_X)*scale,
				mc.displayHeight - (guiTop + CONTENT_Y + MAP_HEIGHT)*scale,
				MAP_WIDTH*scale, MAP_HEIGHT*scale);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Map<ShortVec2, MapTile> tiles = data.getSeenChunksInDimension(player.dimension);
		// Find chunk coordinates of the top left corner of the map:
		ShortVec2 mapStartCoords = new ShortVec2(
				Math.round(-((double)MAP_WIDTH/2d + mapOffsetX) * BLOCK_TO_PIXEL_RATIO) >> 4,
				Math.round(-((double)MAP_HEIGHT/2d + mapOffsetY) * BLOCK_TO_PIXEL_RATIO) >> 4);
		ShortVec2 chunkCoords = new ShortVec2(mapStartCoords);
		int screenX = guiLeft + WIDTH/2 + (int)((mapStartCoords.x << 4) / BLOCK_TO_PIXEL_RATIO) + mapOffsetX;
		int screenY;
		int u = 0;
		int v = 0;
		for (int x = 0; x < MAP_WIDTH_IN_TILES + 2; x++) {
			screenY = guiTop + HEIGHT/2 + (int)((mapStartCoords.y << 4) / BLOCK_TO_PIXEL_RATIO) + mapOffsetY;
			chunkCoords.y = mapStartCoords.y;
			for (int z = 0; z < MAP_HEIGHT_IN_TILES + 2; z++) {
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
					
					// Render global markers
					//TODO consider optimizing loading the markers:
					tempMarkers.addAll(globalMarkers.getMarkersAtChunk(player.dimension, chunkCoords));
					tempMarkers.addAll(localMarkers.getMarkersAtChunk(player.dimension, chunkCoords));
					for (Marker marker : tempMarkers) {
						AtlasRenderHelper.drawFullTexture(
								MarkerTextureMap.instance().getTexture(marker.getType()),
								screenX + marker.getInChunkX()/BLOCK_TO_PIXEL_RATIO - (double)MARKER_ICON_WIDTH/2,
								screenY + marker.getInChunkY()/BLOCK_TO_PIXEL_RATIO - MARKER_ICON_HEIGHT/2,
								MARKER_ICON_WIDTH, MARKER_ICON_HEIGHT, 1);
						// TODO render label tooltip on mouseover
					}
					tempMarkers.clear();
				}
				chunkCoords.y++;
				screenY += MAP_TILE_SIZE;
			}
			chunkCoords.x++;
			screenX += MAP_TILE_SIZE;
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		
		// Overlay the frame so that edges of the map are smooth:
		AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, guiLeft, guiTop, WIDTH, HEIGHT);
		
		// How much the player has moved from the top left corner of the map, in pixels:
		int playerOffsetX = (int)(player.posX / BLOCK_TO_PIXEL_RATIO) + mapOffsetX;
		int playerOffsetZ = (int)(player.posZ / BLOCK_TO_PIXEL_RATIO) + mapOffsetY;
		if (playerOffsetX < -MAP_WIDTH/2) playerOffsetX = -MAP_WIDTH/2;
		if (playerOffsetX > MAP_WIDTH/2) playerOffsetX = MAP_WIDTH/2;
		if (playerOffsetZ < -MAP_HEIGHT/2) playerOffsetZ = -MAP_HEIGHT/2;
		if (playerOffsetZ > MAP_HEIGHT/2 - 2) playerOffsetZ = MAP_HEIGHT/2 - 2;
		// Draw player icon:
		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft + WIDTH/2 + playerOffsetX, guiTop + HEIGHT/2 + playerOffsetZ, 0);
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
			progressBar.draw((width - 100)/2, height/2 - 34);
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
