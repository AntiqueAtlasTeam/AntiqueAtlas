package hunternif.mc.atlas.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.client.*;
import hunternif.mc.atlas.client.gui.core.*;
import hunternif.mc.atlas.client.gui.core.GuiStates.IState;
import hunternif.mc.atlas.client.gui.core.GuiStates.SimpleState;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.server.BrowsingPositionPacket;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GuiAtlas extends GuiComponent {
    public static final int WIDTH = 310;
    public static final int HEIGHT = 218;
    private static final int CONTENT_X = 17;
    private static final int CONTENT_Y = 11;

    private static final int MAP_WIDTH = WIDTH - 17 * 2;
    private static final int MAP_HEIGHT = 194;
    private static final float PLAYER_ROTATION_STEPS = 16;
    private static final int PLAYER_ICON_WIDTH = 7;
    private static final int PLAYER_ICON_HEIGHT = 8;

    public static final int MARKER_SIZE = 32;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * If the map scale goes below this value, the tiles will not scale down
     * visually, but will instead span greater area.
     */
    private static final double MIN_SCALE_THRESHOLD = 0.5;

    private final long[] renderTimes = new long[30];

    private int renderTimesIndex = 0;

    // States ==================================================================

    private final GuiStates state = new GuiStates();

    /**
     * If on, navigate the map normally.
     */
    private final IState NORMAL = new SimpleState();

    /**
     * If on, all markers as well as the player icon are hidden.
     */
    private final IState HIDING_MARKERS = new IState() {
        @Override
        public void onEnterState() {
            // Set the button as not selected so that it can be clicked again:
            btnShowMarkers.setSelected(false);
            btnShowMarkers.setTitle(I18n.format("gui.antiqueatlas.showMarkers"));
            btnShowMarkers.setIconTexture(Textures.ICON_SHOW_MARKERS);
        }

        @Override
        public void onExitState() {
            btnShowMarkers.setSelected(false);
            btnShowMarkers.setTitle(I18n.format("gui.antiqueatlas.hideMarkers"));
            btnShowMarkers.setIconTexture(Textures.ICON_HIDE_MARKERS);
        }
    };

    /**
     * If on, a semi-transparent marker is attached to the cursor, and the
     * player's icon becomes semi-transparent as well.
     */
    private final IState PLACING_MARKER = new IState() {
        @Override
        public void onEnterState() {
            btnMarker.setSelected(true);
        }

        @Override
        public void onExitState() {
            btnMarker.setSelected(false);
        }
    };

    /**
     * If on, the closest marker will be deleted upon mouseclick.
     */
    private final IState DELETING_MARKER = new IState() {
        @Override
        public void onEnterState() {
            // GuiComponent.v.a();
            addChild(eraser);
            btnDelMarker.setSelected(true);
        }

        @Override
        public void onExitState() {
            // mc.v.b();
            removeChild(eraser);
            btnDelMarker.setSelected(false);
        }
    };
    private final GuiCursor eraser = new GuiCursor();

    private final IState EXPORTING_IMAGE = new IState() {
        @Override
        public void onEnterState() {
            btnExportPng.setSelected(true);
        }

        @Override
        public void onExitState() {
            btnExportPng.setSelected(false);
        }
    };

    // Buttons =================================================================

    /**
     * Arrow buttons for navigating the map view via mouse clicks.
     */
    private final GuiArrowButton btnUp, btnDown, btnLeft, btnRight;

    /**
     * Button for exporting PNG image of the Atlas's contents.
     */
    private final GuiBookmarkButton btnExportPng;

    /**
     * Button for placing a marker at current position, local to this Atlas instance.
     */
    private final GuiBookmarkButton btnMarker;

    /**
     * Button for deleting local markers.
     */
    private final GuiBookmarkButton btnDelMarker;

    /**
     * Button for showing/hiding all markers.
     */
    private final GuiBookmarkButton btnShowMarkers;

    /**
     * Button for restoring player's position at the center of the Atlas.
     */
    private final GuiPositionButton btnPosition;


    // Navigation ==============================================================

    /**
     * Pause between after the arrow button is pressed and continuous
     * navigation starts, in ticks.
     */
    private static final int BUTTON_PAUSE = 8;

    /**
     * How much the map view is offset, in blocks, per click (or per tick).
     */
    private static final int navigateStep = 24;

    /**
     * The button which is currently being pressed. Used for continuous
     * navigation using the arrow buttons. Also used to prevent immediate
     * canceling of placing marker.
     */
    private GuiComponentButton selectedButton = null;

    /**
     * Time in world ticks when the button was pressed. Used to create a pause
     * before continuous navigation using the arrow buttons.
     */
    private long timeButtonPressed = 0;

    /**
     * Set to true when dragging the map view.
     */
    private boolean isDragging = false;

    /**
     * Offset to the top left corner of the tile at (0, 0) from the center of
     * the map drawing area, in pixels.
     */
    private int mapOffsetX, mapOffsetY;
    /**
     * If true, the player's icon will be in the center of the GUI, and the
     * offset of the tiles will be calculated accordingly. Otherwise it's the
     * position of the player that will be calculated with respect to the
     * offset.
     */
    private boolean followPlayer = true;

    private final GuiScaleBar scaleBar = new GuiScaleBar();

    /**
     * Pixel-to-block ratio.
     */
    private double mapScale;
    /**
     * The visual size of a tile in pixels.
     */
    private int tileHalfSize;
    /**
     * The number of chunks a tile spans.
     */
    private int tile2ChunkScale;


    // Markers =================================================================

    /**
     * Local markers in the current dimension
     */
    private DimensionMarkersData localMarkersData;
    /**
     * Global markers in the current dimension
     */
    private DimensionMarkersData globalMarkersData;
    /**
     * The marker highlighted by the eraser. Even though multiple markers may
     * be highlighted at the same time, only one of them will be deleted.
     */
    private Marker hoveredMarker;

    private final GuiMarkerFinalizer markerFinalizer = new GuiMarkerFinalizer();
    /**
     * Displayed where the marker is about to be placed when the Finalizer GUI is on.
     */
    private final GuiBlinkingMarker blinkingIcon = new GuiBlinkingMarker();

    // Misc stuff ==============================================================

    private PlayerEntity player;
    private ItemStack stack;
    private DimensionData biomeData;

    /**
     * Coordinate scale factor relative to the actual screen size.
     */
    private double screenScale;

    /**
     * Progress bar for exporting images.
     */
    private final ProgressBarOverlay progressBar = new ProgressBarOverlay(100, 2);

    private long lastUpdateMillis = System.currentTimeMillis();
    private int scaleAlpha = 255;
    private int scaleClipIndex = 0;
    private final int zoomLevelOne = 8;
    private int zoomLevel = zoomLevelOne;
    private final String[] zoomNames = new String[]{"256", "128", "64", "32", "16", "8", "4", "2", "1", "1/2", "1/4", "1/8", "1/16", "1/32", "1/64", "1/128", "1/256"};

    private Thread exportThread;

    @SuppressWarnings("rawtypes")
    public GuiAtlas() {
        setSize(WIDTH, HEIGHT);
        setMapScale(0.5);
        followPlayer = true;
        setInterceptKeyboard(true);

        btnUp = GuiArrowButton.up();
        addChild(btnUp).offsetGuiCoords(148, 10);
        btnDown = GuiArrowButton.down();
        addChild(btnDown).offsetGuiCoords(148, 194);
        btnLeft = GuiArrowButton.left();
        addChild(btnLeft).offsetGuiCoords(15, 100);
        btnRight = GuiArrowButton.right();
        addChild(btnRight).offsetGuiCoords(283, 100);
        btnPosition = new GuiPositionButton();
        btnPosition.setEnabled(!followPlayer);
        addChild(btnPosition).offsetGuiCoords(283, 194);
        IButtonListener positionListener = button -> {
            selectedButton = button;
            if (button.equals(btnPosition)) {
                followPlayer = true;
                btnPosition.setEnabled(false);
            } else {
                // Navigate once, before enabling pause:
                navigateByButton(selectedButton);
                timeButtonPressed = player.getEntityWorld().getGameTime();
            }
        };
        btnUp.addListener(positionListener);
        btnDown.addListener(positionListener);
        btnLeft.addListener(positionListener);
        btnRight.addListener(positionListener);
        btnPosition.addListener(positionListener);

        btnExportPng = new GuiBookmarkButton(1, Textures.ICON_EXPORT, I18n.format("gui.antiqueatlas.exportImage")) {
            @Override
            public boolean isEnabled() {
                return !ExportImageUtil.isExporting;
            }
        };
        addChild(btnExportPng).offsetGuiCoords(300, 75);
        btnExportPng.addListener(button -> {
            if (stack != null || !SettingsConfig.itemNeeded) {
                exportThread = new Thread(() -> exportImage(getAtlasID()), "Atlas file export thread");
                exportThread.start();
            }
        });

        btnMarker = new GuiBookmarkButton(0, Textures.ICON_ADD_MARKER, I18n.format("gui.antiqueatlas.addMarker"));
        addChild(btnMarker).offsetGuiCoords(300, 14);
        btnMarker.addListener(button -> {
            if (state.is(PLACING_MARKER)) {
                selectedButton = null;
                state.switchTo(NORMAL);
            } else if (stack != null || !SettingsConfig.itemNeeded) {
                selectedButton = button;
                state.switchTo(PLACING_MARKER);

                // While holding shift, we create a marker on the player's position
                if (hasShiftDown()) {
                    markerFinalizer.setMarkerData(player.getEntityWorld(),
                            getAtlasID(), player.dimension,
                            (int) player.getPosX(), (int) player.getPosZ());
                    addChild(markerFinalizer);

                    blinkingIcon.setTexture(markerFinalizer.selectedType.getIcon(),
                            MARKER_SIZE, MARKER_SIZE);
                    addChildBehind(markerFinalizer, blinkingIcon)
                            .setRelativeCoords(worldXToScreenX((int) player.getPosX()) - getGuiX() - MARKER_SIZE / 2,
                                    worldZToScreenY((int) player.getPosZ()) - getGuiY() - MARKER_SIZE / 2);

                    // Need to intercept keyboard events to type in the label:
                    setInterceptKeyboard(true);

                    // Un-press all keys to prevent player from walking infinitely:
                    KeyBinding.unPressAllKeys();

                    selectedButton = null;
                    state.switchTo(NORMAL);
                }
            }
        });
        btnDelMarker = new GuiBookmarkButton(2, Textures.ICON_DELETE_MARKER, I18n.format("gui.antiqueatlas.delMarker"));
        addChild(btnDelMarker).offsetGuiCoords(300, 33);
        btnDelMarker.addListener(button -> {
            if (state.is(DELETING_MARKER)) {
                selectedButton = null;
                state.switchTo(NORMAL);
            } else if (stack != null || !SettingsConfig.itemNeeded) {
                selectedButton = button;
                state.switchTo(DELETING_MARKER);
            }
        });
        btnShowMarkers = new GuiBookmarkButton(3, Textures.ICON_HIDE_MARKERS, I18n.format("gui.antiqueatlas.hideMarkers"));
        addChild(btnShowMarkers).offsetGuiCoords(300, 52);
        btnShowMarkers.addListener(button -> {
            selectedButton = null;
            if (state.is(HIDING_MARKERS)) {
                state.switchTo(NORMAL);
            } else if (stack != null || !SettingsConfig.itemNeeded) {
                selectedButton = null;
                state.switchTo(HIDING_MARKERS);
            }
        });

        addChild(scaleBar).offsetGuiCoords(20, 198);
        scaleBar.setMapScale(1);

        markerFinalizer.addMarkerListener(blinkingIcon);

        eraser.setTexture(Textures.ERASER, 12, 14, 2, 11);
    }

    public GuiAtlas prepareToOpen(ItemStack stack) {
        this.stack = stack;

        return prepareToOpen();
    }

    public GuiAtlas prepareToOpen() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            this.player = Minecraft.getInstance().player;
            updateAtlasData();
            if (!followPlayer && SettingsConfig.doSaveBrowsingPos) {
                loadSavedBrowsingPosition();
            }
        });
        return this;
    }

    public void loadSavedBrowsingPosition() {
        // Apply zoom first, because browsing position depends on it:
        setMapScale(biomeData.getBrowsingZoom());
        mapOffsetX = biomeData.getBrowsingX();
        mapOffsetY = biomeData.getBrowsingY();
        isDragging = false;
    }

    @Override
    protected void init() {
        super.init();
        if (state.is(EXPORTING_IMAGE)) {
            state.switchTo(NORMAL); //TODO: his causes the Export PNG progress bar to disappear when resizing game window
        }

        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        screenScale = Minecraft.getInstance().getMainWindow().getGuiScaleFactor();
        setCentered();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseState) {
        boolean result = super.mouseClicked(mouseX, mouseY, mouseState);
        if (state.is(EXPORTING_IMAGE)) {
            return result;
        }

        if (result) {
            return true;
        }

        // If clicked on the map, start dragging
        int mapX = (width - MAP_WIDTH) / 2;
        int mapY = (height - MAP_HEIGHT) / 2;
        boolean isMouseOverMap = mouseX >= mapX && mouseX <= mapX + MAP_WIDTH &&
                mouseY >= mapY && mouseY <= mapY + MAP_HEIGHT;
        if (!state.is(NORMAL) && !state.is(HIDING_MARKERS)) {
            int atlasID = getAtlasID();

            if (state.is(PLACING_MARKER) // If clicked on the map, place marker:
                    && isMouseOverMap && mouseState == 0 /* left click */) {
                markerFinalizer.setMarkerData(player.getEntityWorld(),
                        atlasID, player.dimension,
                        screenXToWorldX((int) mouseX), screenYToWorldZ((int) mouseY));
                addChild(markerFinalizer);

                blinkingIcon.setTexture(markerFinalizer.selectedType.getIcon(),
                        MARKER_SIZE, MARKER_SIZE);
                addChildBehind(markerFinalizer, blinkingIcon)
                        .setRelativeCoords((int) mouseX - getGuiX() - MARKER_SIZE / 2,
                                (int) mouseY - getGuiY() - MARKER_SIZE / 2);

                // Need to intercept keyboard events to type in the label:
                setInterceptKeyboard(true);

                // Un-press all keys to prevent player from walking infinitely:
                KeyBinding.unPressAllKeys();

                state.switchTo(NORMAL);
                return true;
            } else if (state.is(DELETING_MARKER) // If clicked on a marker, delete it:
                    && hoveredMarker != null && !hoveredMarker.isGlobal() && isMouseOverMap && mouseState == 0) {
                AtlasAPI.markers.deleteMarker(player.getEntityWorld(),
                        atlasID, hoveredMarker.getId());
            }
            state.switchTo(NORMAL);
        } else if (isMouseOverMap && selectedButton == null) {
            //if(hoveredMarker == null || !MarkerClickedCallback.EVENT.invoker().onClicked(player, hoveredMarker, mouseState)) {
            if (hoveredMarker == null) {
                isDragging = true;
                return true;
            }
        }

        return result;
    }

    /**
     * Opens a dialog window to select which file to save to, then performs
     * rendering of the map of current dimension into a PNG image.
     */
    private void exportImage(int atlasID) {
        boolean showMarkers = !state.is(HIDING_MARKERS);
        state.switchTo(EXPORTING_IMAGE);
        // Default file name is "Atlas <N>.png"
        ExportImageUtil.isExporting = true;

        File screenshot_folder = new File(Minecraft.getInstance().gameDir, "screenshots");
        if (!screenshot_folder.isDirectory()) {
            screenshot_folder.mkdir();
        }

        String outputname = "atlas-" + DATE_FORMAT.format(new Date());

        File file = new File(screenshot_folder, outputname + ".png");
        for (int i = 1; file.exists(); i++) {
            file = new File(screenshot_folder, outputname + "_" + i + ".png");
        }

        if (file != null) {
            try {
                Log.info("Exporting image from Atlas #%d to file %s", atlasID, file.getAbsolutePath());
                ExportImageUtil.exportPngImage(biomeData, globalMarkersData, localMarkersData, file, showMarkers);
                Log.info("Finished exporting image");
            } catch (OutOfMemoryError e) {
                Log.warn(e, "Image is too large, trying to export in strips");
                try {
                    ExportImageUtil.exportPngImageTooLarge(biomeData, globalMarkersData, localMarkersData, file, showMarkers);
                } catch (OutOfMemoryError e2) {
                    int minX = (biomeData.getScope().minX - 1) * ExportImageUtil.TILE_SIZE;
                    int minY = (biomeData.getScope().minY - 1) * ExportImageUtil.TILE_SIZE;
                    int outWidth = (biomeData.getScope().maxX + 2) * ExportImageUtil.TILE_SIZE - minX;
                    int outHeight = (biomeData.getScope().maxY + 2) * ExportImageUtil.TILE_SIZE - minY;

                    Log.error(e2, "Image is STILL too large, how massive is this map?! Answer: (%dx%d)", outWidth, outHeight);

                    ExportUpdateListener.INSTANCE.setStatusString(I18n.format("gui.antiqueatlas.export.tooLarge"));
                    ExportImageUtil.isExporting = false;
                    return; //Don't switch to normal state yet so that the error message can be read.
                }
            }
        }
        ExportImageUtil.isExporting = false;
        state.switchTo(showMarkers ? NORMAL : HIDING_MARKERS);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_UP) {
            navigateMap(0, navigateStep);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            navigateMap(0, -navigateStep);
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            navigateMap(navigateStep, 0);
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            navigateMap(-navigateStep, 0);
        } else if (keyCode == GLFW.GLFW_KEY_EQUAL || keyCode == GLFW.GLFW_KEY_KP_ADD) {
            setMapScale(mapScale * 2);
        } else if (keyCode == GLFW.GLFW_KEY_MINUS || keyCode == GLFW.GLFW_KEY_KP_SUBTRACT) {
            setMapScale(mapScale / 2);
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
        } else {
            KeyBinding[] hotbarKeys = minecraft.gameSettings.keyBindsHotbar;
            for (KeyBinding bind : hotbarKeys) {
                // only handle hotbarkeys when marker gui isn't shown1
                if (bind.matchesKey(keyCode, scanCode) && this.markerFinalizer.getParent() == null) {
                    onClose();
                    // if we close the gui, then don't handle the event
                    return false;
                }
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double wheelMove) {
        double origWheelMove = wheelMove;

        boolean handled = super.mouseScrolled(mx, my, origWheelMove);

        if (!handled && wheelMove != 0) {
            wheelMove = wheelMove > 0 ? 1 : -1;
            if (SettingsConfig.doReverseWheelZoom) {
                wheelMove *= -1;
            }

            double mouseOffsetX = minecraft.getMainWindow().getFramebufferWidth() / screenScale / 2 - getMouseX();
            double mouseOffsetY = minecraft.getMainWindow().getFramebufferHeight() / screenScale / 2 - getMouseY();
            double newScale = mapScale * Math.pow(2, wheelMove);
            double addOffsetX = 0;
            double addOffsetY = 0;
            if (Math.abs(mouseOffsetX) < MAP_WIDTH / 2f && Math.abs(mouseOffsetY) < MAP_HEIGHT / 2f) {
                addOffsetX = mouseOffsetX * wheelMove;
                addOffsetY = mouseOffsetY * wheelMove;

                if (wheelMove > 0) {
                    addOffsetX *= mapScale / newScale;
                    addOffsetY *= mapScale / newScale;
                }
            }

            setMapScale(newScale, (int) addOffsetX, (int) addOffsetY);

            return true;
        }

        return handled;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseState) {
        boolean result = false;
        if (mouseState != -1) {
            result = selectedButton != null || isDragging;
            selectedButton = null;
            isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, mouseState) || result;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int lastMouseButton, double deltaX, double deltaY) {
        boolean result = false;
        if (isDragging) {
            followPlayer = false;
            btnPosition.setEnabled(true);
            mapOffsetX += (int) deltaX;
            mapOffsetY += (int) deltaY;
            result = true;
        }
        return super.mouseDragged(mouseX, mouseY, lastMouseButton, deltaX, deltaY) || result;
    }

    @Override
    public void tick() {
        super.tick();
        if (player == null) return;
        if (followPlayer) {
            mapOffsetX = (int) (-player.getPosX() * mapScale);
            mapOffsetY = (int) (-player.getPosZ() * mapScale);
        }
        if (player.getEntityWorld().getGameTime() > timeButtonPressed + BUTTON_PAUSE) {
            navigateByButton(selectedButton);
        }

        updateAtlasData();
    }

    /**
     * Update {@link #biomeData}, {@link #localMarkersData},
     * {@link #globalMarkersData}
     */
    private void updateAtlasData() {
        int atlasID = getAtlasID();

        biomeData = AntiqueAtlasMod.atlasData
                .getAtlasData(atlasID, player.getEntityWorld())
                .getDimensionData(player.dimension);
        globalMarkersData = AntiqueAtlasMod.globalMarkersData.getData()
                .getMarkersDataInDimension(player.dimension);
        MarkersData markersData = AntiqueAtlasMod.markersData
                .getMarkersData(atlasID, player.getEntityWorld());
        if (markersData != null) {
            localMarkersData = markersData
                    .getMarkersDataInDimension(player.dimension);
        } else {
            localMarkersData = null;
        }
    }

    /**
     * Offset the map view depending on which button was pressed.
     */
    private void navigateByButton(GuiComponentButton btn) {
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

    /**
     * Offset the map view by given values, in blocks.
     */
    private void navigateMap(int dx, int dy) {
        mapOffsetX += dx;
        mapOffsetY += dy;
        followPlayer = false;
        btnPosition.setEnabled(true);
    }

    /**
     * Set the pixel-to-block ratio, maintaining the current center of the screen.
     */
    public void setMapScale(double scale) {
        setMapScale(scale, 0, 0);
    }

    /**
     * Set the pixel-to-block ratio, maintaining the current center of the screen with additional offset.
     */
    private void setMapScale(double scale, int addOffsetX, int addOffsetY) {
        double oldScale = mapScale;
        mapScale = Math.min(Math.max(scale, SettingsConfig.minScale), SettingsConfig.maxScale);

        // Scaling not needed
        if (oldScale == mapScale) {
            return;
        }

        if (mapScale >= MIN_SCALE_THRESHOLD) {
            tileHalfSize = (int) Math.round(8 * mapScale);
            tile2ChunkScale = 1;
        } else {
            tileHalfSize = (int) Math.round(8 * MIN_SCALE_THRESHOLD);
            tile2ChunkScale = (int) Math.round(MIN_SCALE_THRESHOLD / mapScale);
        }

        // Times 2 because the contents of the Atlas are rendered at resolution 2 times smaller:
        scaleBar.setMapScale(mapScale * 2);
        mapOffsetX = (int) ((mapOffsetX + addOffsetX) * (mapScale / oldScale));
        mapOffsetY = (int) ((mapOffsetY + addOffsetY) * (mapScale / oldScale));
        scaleClipIndex = MathHelper.log2((int) (mapScale * 8192)) + 1 - 13;
        zoomLevel = -scaleClipIndex + zoomLevelOne;
        scaleAlpha = 255;

        if (followPlayer && (addOffsetX != 0 || addOffsetY != 0)) {
            followPlayer = false;
            btnPosition.setEnabled(true);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float par3) {
        long currentMillis = System.currentTimeMillis();
        long deltaMillis = currentMillis - lastUpdateMillis;
        lastUpdateMillis = currentMillis;

        if (SettingsConfig.debugRender) {
            renderTimes[renderTimesIndex++] = System.currentTimeMillis();
            if (renderTimesIndex == renderTimes.length) {
                renderTimesIndex = 0;
                double elapsed = 0;
                for (int i = 0; i < renderTimes.length - 1; i++) {
                    elapsed += renderTimes[i + 1] - renderTimes[i];
                }
                System.out.printf("GuiAtlas avg. render time: %.3f\n", elapsed / renderTimes.length);
            }
        }

        super.renderBackground();

        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0); // So light detail on tiles is visible
        AtlasRenderHelper.drawFullTexture(Textures.BOOK, getGuiX(), getGuiY(), WIDTH, HEIGHT);

        if ((stack == null && SettingsConfig.itemNeeded) || biomeData == null)
            return;

        if (state.is(DELETING_MARKER)) {
            RenderSystem.color4f(1, 1, 1, 0.5f);
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) ((getGuiX() + CONTENT_X) * screenScale),
                (int) ((minecraft.getMainWindow().getFramebufferHeight() - (getGuiY() + CONTENT_Y + MAP_HEIGHT) * screenScale)),
                (int) (MAP_WIDTH * screenScale), (int) (MAP_HEIGHT * screenScale));
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // Find chunk coordinates of the top left corner of the map.
        // The 'roundToBase' is required so that when the map scales below the
        // threshold the tiles don't change when map position changes slightly.
        // The +-2 at the end provide margin so that tiles at the edges of
        // the page have their stitched texture correct.
        int mapStartX = MathUtil.roundToBase((int) Math.floor(-((double) MAP_WIDTH / 2d + mapOffsetX + 2 * tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapStartZ = MathUtil.roundToBase((int) Math.floor(-((double) MAP_HEIGHT / 2d + mapOffsetY + 2 * tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapEndX = MathUtil.roundToBase((int) Math.ceil(((double) MAP_WIDTH / 2d - mapOffsetX + 2 * tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapEndZ = MathUtil.roundToBase((int) Math.ceil(((double) MAP_HEIGHT / 2d - mapOffsetY + 2 * tileHalfSize) / mapScale / 16d), tile2ChunkScale);
        int mapStartScreenX = getGuiX() + WIDTH / 2 + (int) ((mapStartX << 4) * mapScale) + mapOffsetX;
        int mapStartScreenY = getGuiY() + HEIGHT / 2 + (int) ((mapStartZ << 4) * mapScale) + mapOffsetY;
        TileRenderIterator iter = new TileRenderIterator(biomeData);
        iter.setScope(new Rect().setOrigin(mapStartX, mapStartZ).
                set(mapStartX, mapStartZ, mapEndX, mapEndZ));
        iter.setStep(tile2ChunkScale);
        int v = 1;
        while (iter.hasNext()) {
            SubTileQuartet subtiles = iter.next();
            for (SubTile subtile : subtiles) {
                if (subtile == null || subtile.tile == null) continue;
                AtlasRenderHelper.drawAutotileCorner(
                        BiomeTextureMap.instance().getTexture(subtile.variationNumber, subtile.tile),
                        mapStartScreenX + subtile.x * tileHalfSize,
                        mapStartScreenY + subtile.y * tileHalfSize,
                        subtile.getTextureU(), subtile.getTextureV(), tileHalfSize);
            }
        }

        int markersStartX = MathUtil.roundToBase(mapStartX, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP - 1;
        int markersStartZ = MathUtil.roundToBase(mapStartZ, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP - 1;
        int markersEndX = MathUtil.roundToBase(mapEndX, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP + 1;
        int markersEndZ = MathUtil.roundToBase(mapEndZ, MarkersData.CHUNK_STEP) / MarkersData.CHUNK_STEP + 1;
        double iconScale = getIconScale();

        // Draw global markers:
        for (int x = markersStartX; x <= markersEndX; x++) {
            for (int z = markersStartZ; z <= markersEndZ; z++) {
                List<Marker> markers = globalMarkersData.getMarkersAtChunk(x, z);
                if (markers == null) continue;
                for (Marker marker : markers) {
                    renderMarker(mouseX, mouseY, marker, iconScale);
                }
            }
        }

        // Draw local markers:
        if (localMarkersData != null) {
            for (int x = markersStartX; x <= markersEndX; x++) {
                for (int z = markersStartZ; z <= markersEndZ; z++) {
                    List<Marker> markers = localMarkersData.getMarkersAtChunk(x, z);
                    if (markers == null) continue;
                    for (Marker marker : markers) {
                        renderMarker(mouseX, mouseY, marker, iconScale);
                    }
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Overlay the frame so that edges of the map are smooth:
        RenderSystem.color4f(1, 1, 1, 1);
        AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, getGuiX(), getGuiY(), WIDTH, HEIGHT);
        renderScaleOverlay(deltaMillis);
        iconScale = getIconScale();

        // Draw player icon:
        if (!state.is(HIDING_MARKERS)) {
            // How much the player has moved from the top left corner of the map, in pixels:
            int playerOffsetX = (int) (player.getPosX() * mapScale) + mapOffsetX;
            int playerOffsetZ = (int) (player.getPosZ() * mapScale) + mapOffsetY;
            if (playerOffsetX < -MAP_WIDTH / 2) playerOffsetX = -MAP_WIDTH / 2;
            if (playerOffsetX > MAP_WIDTH / 2) playerOffsetX = MAP_WIDTH / 2;
            if (playerOffsetZ < -MAP_HEIGHT / 2) playerOffsetZ = -MAP_HEIGHT / 2;
            if (playerOffsetZ > MAP_HEIGHT / 2 - 2) playerOffsetZ = MAP_HEIGHT / 2 - 2;
            // Draw the icon:
            RenderSystem.color4f(1, 1, 1, state.is(PLACING_MARKER) ? 0.5f : 1);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(getGuiX() + WIDTH / 2 + playerOffsetX, getGuiY() + HEIGHT / 2 + playerOffsetZ, 0);
            float playerRotation = (float) Math.round(player.rotationYaw / 360f * PLAYER_ROTATION_STEPS) / PLAYER_ROTATION_STEPS * 360f;
            RenderSystem.rotatef(180 + playerRotation, 0, 0, 1);
            RenderSystem.translatef((float) (-PLAYER_ICON_WIDTH / 2 * iconScale), (float) (-PLAYER_ICON_HEIGHT / 2 * iconScale), 0f);
            AtlasRenderHelper.drawFullTexture(Textures.PLAYER, 0, 0,
                    (int) Math.round(PLAYER_ICON_WIDTH * iconScale), (int) Math.round(PLAYER_ICON_HEIGHT * iconScale));
            RenderSystem.popMatrix();
            RenderSystem.color4f(1, 1, 1, 1);
        }

        // Draw buttons:
        super.render(mouseX, mouseY, par3);

        // Draw the semi-transparent marker attached to the cursor when placing a new marker:
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (state.is(PLACING_MARKER)) {
            RenderSystem.color4f(1, 1, 1, 0.5f);
            markerFinalizer.selectedType.calculateMip(iconScale, mapScale, screenScale);
            MarkerRenderInfo renderInfo = markerFinalizer.selectedType.getRenderInfo(iconScale, mapScale, screenScale);
            markerFinalizer.selectedType.resetMip();
            AtlasRenderHelper.drawFullTexture(
                    renderInfo.tex,
                    mouseX + renderInfo.x, mouseY + renderInfo.y,
                    renderInfo.width, renderInfo.height);
            RenderSystem.color4f(1, 1, 1, 1);
        }

        // Draw progress overlay:
        if (state.is(EXPORTING_IMAGE)) {
            renderBackground();
            progressBar.draw((width - 100) / 2, height / 2 - 34);
        }
    }

    private void renderScaleOverlay(long deltaMillis) {
        if (scaleAlpha > 0) {
            RenderSystem.translatef(getGuiX() + WIDTH - 13, getGuiY() + 12, 0);

            String text;
            int textWidth, xWidth;

            text = "x";
            xWidth = textWidth = font.getStringWidth(text);
            xWidth++;
            font.drawString(text, -textWidth, 0, scaleAlpha << 24);

            text = zoomNames[zoomLevel];
            if (text.contains("/")) {

                String[] parts = text.split("/");

                text = parts[0];
                int centerXtranslate = Math.max(font.getStringWidth(parts[0]), font.getStringWidth(parts[1])) / 2;
                RenderSystem.translatef(-xWidth - centerXtranslate, -font.FONT_HEIGHT / 2, 0);

                RenderSystem.disableTexture();
                Tessellator t = Tessellator.getInstance();
                BufferBuilder vb = t.getBuffer();
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                vb.pos(centerXtranslate, font.FONT_HEIGHT - 1, 0.0D).endVertex();
                vb.pos(-centerXtranslate - 1, font.FONT_HEIGHT - 1, 0.0D).endVertex();
                vb.pos(-centerXtranslate - 1, font.FONT_HEIGHT, 0.0D).endVertex();
                vb.pos(centerXtranslate, font.FONT_HEIGHT, 0.0D).endVertex();
                t.draw();
                RenderSystem.enableTexture();
                textWidth = font.getStringWidth(text);
                font.drawString(text, -textWidth / 2, 0, scaleAlpha << 24);

                text = parts[1];
                RenderSystem.translatef(0, font.FONT_HEIGHT + 1, 0);

                textWidth = font.getStringWidth(text);
                font.drawString(text, -textWidth / 2, 0, scaleAlpha << 24);

                RenderSystem.translatef(xWidth + centerXtranslate, (-font.FONT_HEIGHT / 2) - 2, 0);
            } else {
                textWidth = font.getStringWidth(text);
                font.drawString(text, -textWidth - xWidth + 1, 1, scaleAlpha << 24);
            }

            RenderSystem.translatef(-(getGuiX() + WIDTH - 13), -(getGuiY() + 12), 0);

            int deltaScaleAlpha = (int) (deltaMillis * 0.256);
            // because of some crazy high frame rate
            if (deltaScaleAlpha == 0) {
                deltaScaleAlpha = 1;
            }

            scaleAlpha -= 20 * deltaScaleAlpha;

            if (scaleAlpha < 0)
                scaleAlpha = 0;
        }
    }

    private void renderMarker(int mouseX, int mouseY, Marker marker, double scale) {
        MarkerType type = MarkerRegistry.find(marker.getType());
        if (type == null) {
            Log.warn("Could not find marker data for %s. Is it in the config file?\n", marker.getType());
            return;
        }
        if (type.shouldHide(state.is(HIDING_MARKERS), scaleClipIndex)) {
            return;
        }

        int markerX = worldXToScreenX(marker.getX());
        int markerY = worldZToScreenY(marker.getZ());
        if (!marker.isVisibleAhead() &&
                !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
            return;
        }
        type.calculateMip(scale, mapScale, screenScale);
        MarkerRenderInfo info = type.getRenderInfo(scale, mapScale, screenScale);

        boolean mouseIsOverMarker = type.shouldHover((getMouseX() - (markerX + info.x)) / info.width, (getMouseY() - (markerY + info.y)) / info.height);
        type.resetMip();

        if (mouseIsOverMarker) {
            RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1);
            hoveredMarker = marker;
            //MarkerHoveredCallback.EVENT.invoker().onHovered(player, marker);
        } else {
            RenderSystem.color4f(1, 1, 1, 1);
            if (hoveredMarker == marker) {
                hoveredMarker = null;
            }
        }

        if (state.is(PLACING_MARKER)) {
            RenderSystem.color4f(1, 1, 1, 0.5f);
        } else if (state.is(DELETING_MARKER) && marker.isGlobal()) {
            RenderSystem.color4f(1, 1, 1, 0.5f);
        } else {
            RenderSystem.color4f(1, 1, 1, 1);
        }

        if (SettingsConfig.debugRender) {
            System.out.println("Rendering Marker: " + info.tex);
        }

        AtlasRenderHelper.drawFullTexture(
                info.tex,
                markerX + info.x,
                markerY + info.y,
                info.width, info.height);
        if (isMouseOver && mouseIsOverMarker && marker.getLabel().length() > 0) {
            drawTooltip(Collections.singletonList(marker.getLocalizedLabel()), font);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        removeChild(markerFinalizer);
        removeChild(blinkingIcon);
        // Keyboard.enableRepeatEvents(false);
        biomeData.setBrowsingPosition(mapOffsetX, mapOffsetY, mapScale);

        PacketDispatcher.INSTANCE.sendToServer(new BrowsingPositionPacket(getAtlasID(), player.dimension, mapOffsetX, mapOffsetY, mapScale));
    }

    /**
     * Returns the Y coordinate that the cursor is pointing at.
     */
    private int screenXToWorldX(int mouseX) {
        return (int) Math.round((double) (mouseX - this.width / 2 - mapOffsetX) / mapScale);
    }

    /**
     * Returns the Y block coordinate that the cursor is pointing at.
     */
    private int screenYToWorldZ(int mouseY) {
        return (int) Math.round((double) (mouseY - this.height / 2 - mapOffsetY) / mapScale);
    }

    private int worldXToScreenX(int x) {
        return (int) Math.round((double) x * mapScale + this.width / 2f + mapOffsetX);
    }

    private int worldZToScreenY(int z) {
        return (int) Math.round((double) z * mapScale + this.height / 2f + mapOffsetY);
    }

    @Override
    protected void onChildClosed(GuiComponent child) {
        if (child.equals(markerFinalizer)) {
            setInterceptKeyboard(true);
            removeChild(blinkingIcon);
        }
    }

    /**
     * Update all text labels to current localization.
     */
    public void updateL18n() {
        btnExportPng.setTitle(I18n.format("gui.antiqueatlas.exportImage"));
        btnMarker.setTitle(I18n.format("gui.antiqueatlas.addMarker"));
    }

    /**
     * Returns the scale of markers and player icon at given mapScale.
     */
    private double getIconScale() {
        return SettingsConfig.doScaleMarkers ? (mapScale < 0.5 ? 0.5 : mapScale > 1 ? 2 : 1) : 1;
    }

    /**
     * Returns atlas id based on "itemNeeded" option
     */
    private int getAtlasID() {
        return SettingsConfig.itemNeeded ? ((ItemAtlas) stack.getItem()).getAtlasID(stack) : player.getUniqueID().hashCode();
    }
}
