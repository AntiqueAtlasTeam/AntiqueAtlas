package hunternif.mc.impl.atlas.util;

import hunternif.mc.impl.atlas.client.*;
import hunternif.mc.impl.atlas.client.gui.ExportUpdateListener;
import hunternif.mc.impl.atlas.core.WorldData;
import hunternif.mc.impl.atlas.marker.DimensionMarkersData;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.registry.MarkerRenderInfo;
import hunternif.mc.impl.atlas.registry.MarkerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

@Environment(EnvType.CLIENT)
public class ExportImageUtil {
    public static final int TILE_SIZE = 16;
    public static final int MARKER_SIZE = 32;
    public static boolean isExporting = false;

    private static Frame frame;
    private static final JFileChooser chooser = new JFileChooser();

    private static ExportUpdateListener getListener() {
        return ExportUpdateListener.INSTANCE;
    }

    static {
        chooser.setDialogTitle(I18n.translate("gui.antiqueatlas.exportImage"));
        chooser.setSelectedFile(new File("Atlas.png"));
        chooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "PNG Image";
            }

            @Override
            public boolean accept(File file) {
                // Accept all files so they are visible
                return true;
            }
        });
    }

    /**
     * Beware that the background texture doesn't follow the Autotile format.
     */
    private static final int BG_TILE_SIZE = 22;

    /**
     * Opens a dialog and returns the file that was chosen, null if none or error.
     */
    public static File selectPngFileToSave(String atlasName) {
        getListener().setHeaderString("");
        getListener().setStatusString("gui.antiqueatlas.export.opening");
        getListener().setProgressMax(-1);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            Log.error(e, "Setting system Look&Feel for JFileChooser");
        }

        getListener().setStatusString("gui.antiqueatlas.export.selectFile");
        frame = new Frame();
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            frame.dispose();
            // Check file extension:
            if (file.getName().length() < 4 || // No extension
                    !file.getName().substring(file.getName().length() - 4).equalsIgnoreCase(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            return file;
        }
        frame.dispose();
        return null;
    }

    /**
     * Renders the map into file as PNG image.
     */
    public static void exportPngImage(WorldData biomeData, DimensionMarkersData globalMarkers,
                                      DimensionMarkersData localMarkers, File file, boolean showMarkers) {
        getListener().setHeaderString("gui.antiqueatlas.export.setup");
        // Prepare output image
        // Leave padding of one row of map tiles on each side
        int minX = (biomeData.getScope().minX - 1) * TILE_SIZE;
        int minY = (biomeData.getScope().minY - 1) * TILE_SIZE;
        int outWidth = (biomeData.getScope().maxX + 2) * TILE_SIZE - minX;
        int outHeight = (biomeData.getScope().maxY + 2) * TILE_SIZE - minY;
        Log.info("Image size: %dx%d", outWidth, outHeight);
        getListener().setStatusString("gui.antiqueatlas.export.makingbuffer", outWidth, outHeight);
        BufferedImage outImage = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = outImage.createGraphics();

        // Draw background, double scale:
        int scale = 2;
        int bgTilesX = Math.round((float) outWidth / (float) BG_TILE_SIZE / (float) scale);
        int bgTilesY = Math.round((float) outHeight / (float) BG_TILE_SIZE / (float) scale);
        // Count background tiles too:

        // Preload all textures (they should be small enough)
        // Count loaded textures as update units too.
        getListener().setStatusString("gui.antiqueatlas.export.loadingtextures");
        getListener().setProgressMax(-1);
        BufferedImage bg = null;
        Map<Identifier, BufferedImage> textureImageMap = new HashMap<>();
        try {
            InputStream is = MinecraftClient.getInstance().getResourceManager().getResource(Textures.EXPORTED_BG).getInputStream();
            bg = ImageIO.read(is);
            is.close();

            // Biome & Marker textures:
            List<Identifier> allTextures = new ArrayList<>(64);
            allTextures.addAll(TileTextureMap.instance().getAllTextures());
            if (showMarkers) {
                for (MarkerType type : MarkerType.REGISTRY) {
                    allTextures.addAll(Arrays.asList(type.getAllIcons()));
//					allTextures.add(type.getIcon());
                }
            }
            for (Identifier texture : allTextures) {
                try {
                    is = MinecraftClient.getInstance().getResourceManager().getResource(texture).getInputStream();
                    BufferedImage tileImage = ImageIO.read(is);
                    is.close();
                    textureImageMap.put(texture, tileImage);
                } catch (FileNotFoundException e) {
                    // This can happen, for example, when you remove a mod that has added custom textures
                    Log.warn("Texture %s not found!", texture.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getListener().setHeaderString("gui.antiqueatlas.export.rendering");
        drawMapToGraphics(
                graphics,
                bgTilesX, bgTilesY, outWidth, outHeight,
                biomeData, textureImageMap,
                globalMarkers, localMarkers,
                showMarkers, minX, minY,
                scale, bg);

        try {
            getListener().setHeaderString("");
            getListener().setStatusString("gui.antiqueatlas.export.writing");
            ImageIO.write(outImage, "PNG", file);
            Log.info("Done writing image");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renders the map into file as PNG image stripe by stripe in order to not have a OutOfMemoryError.
     */
    public static void exportPngImageTooLarge(final WorldData biomeData, final DimensionMarkersData globalMarkers,
                                              final DimensionMarkersData localMarkers, File file, final boolean showMarkers) {
        getListener().setHeaderString("");
        // Prepare output image
        // Leave padding of one row of map tiles on each side
        final int minX = (biomeData.getScope().minX - 1) * TILE_SIZE;
        final int minY = (biomeData.getScope().minY - 1) * TILE_SIZE;
        final int outWidth = (biomeData.getScope().maxX + 2) * TILE_SIZE - minX;
        final int outHeight = (biomeData.getScope().maxY + 2) * TILE_SIZE - minY;
        Log.info("Image size: %dx%d", outWidth, outHeight);

        // Draw background, double scale:
        final int scale = 2;
        final int bgTilesX = Math.round((float) outWidth / (float) BG_TILE_SIZE / (float) scale);
        final int bgTilesY = Math.round((float) outHeight / (float) BG_TILE_SIZE / (float) scale);

        // Preload all textures (they should be small enough)
        // Count loaded textures as update units too.
        getListener().setStatusString("gui.antiqueatlas.export.loadingtextures");
        getListener().setProgressMax(-1);
        BufferedImage bg = null;
        final Map<Identifier, BufferedImage> textureImageMap = new HashMap<>();
        try {
            InputStream is = MinecraftClient.getInstance().getResourceManager().getResource(Textures.EXPORTED_BG).getInputStream();
            bg = ImageIO.read(is);
            is.close();

            // Biome & Marker textures:
            List<Identifier> allTextures = new ArrayList<>(64);
            allTextures.addAll(TileTextureMap.instance().getAllTextures());
            if (showMarkers) {
                for (MarkerType type : MarkerType.REGISTRY) {
                    allTextures.addAll(Arrays.asList(type.getAllIcons()));
//					allTextures.add(type.getIcon());
                }
            }
            for (Identifier texture : allTextures) {
                try {
                    is = MinecraftClient.getInstance().getResourceManager().getResource(texture).getInputStream();
                    BufferedImage tileImage = ImageIO.read(is);
                    is.close();
                    textureImageMap.put(texture, tileImage);
                } catch (FileNotFoundException e) {
                    // This can happen, for example, when you remove a mod that has added custom textures
                    Log.warn("Texture %s not found!", texture.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.gc();

        long availableMem = getAvailableMemory();
        long usableMem = (long) (availableMem * 0.8); // leave some breathing room
        int pixelSize = Integer.SIZE / 8;

        int sliceHeight = TILE_SIZE;

        for (int i = bgTilesY; i > 0; i--) {
            long usedMem = ((long) (i * TILE_SIZE) * outWidth * pixelSize);
            if (usedMem <= usableMem) {
                sliceHeight = i * TILE_SIZE;
                break;
            } else {
                Log.info("%d tiles tall is too big, %d > %d", i, usedMem, usableMem);
            }
        }
        final int sliceHeight_ = sliceHeight;
        final int slices = (int) Math.ceil((float) outHeight / (float) sliceHeight);
        final BufferedImage bg_ = bg;

        final BufferedImage scanBuffer = new BufferedImage(outWidth, sliceHeight, BufferedImage.TYPE_INT_ARGB);

        getListener().setProgressMax(slices);
        RenderedImage outImage = new RenderedImageScanned(outWidth, outHeight, scanBuffer, graphics -> {
            int slice = (int) Math.floor(-graphics.getTransform().getTranslateY() / sliceHeight_);
            getListener().setProgress(slice);
            getListener().setHeaderString("gui.antiqueatlas.export.renderstripe", slice + 1, slices);
            drawMapToGraphics(
                    graphics,
                    bgTilesX, bgTilesY, outWidth, outHeight,
                    biomeData, textureImageMap,
                    globalMarkers, localMarkers,
                    showMarkers, minX, minY,
                    scale, bg_);
            getListener().setStatusString("gui.antiqueatlas.export.writestripe");
            getListener().setProgressMax(sliceHeight_ * (slice + 1) > outHeight ? outHeight - (sliceHeight_ * slice) : sliceHeight_);
        }, value -> getListener().setProgress(value));

        try {
            getListener().setHeaderString("gui.antiqueatlas.export.renderstripe", 1, slices);
            ImageIO.write(outImage, "PNG", file);
            Log.info("Done writing image");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long getAvailableMemory() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory(); // current heap allocated to the VM process
        long freeMemory = runtime.freeMemory(); // out of the current heap, how much is free
        long maxMemory = runtime.maxMemory(); // Max heap VM can use e.g. Xmx setting
        long usedMemory = totalMemory - freeMemory; // how much of the current heap the VM is using

        return maxMemory - usedMemory; // available memory i.e. Maximum heap size minus the current amount used
    }

    private static void drawMapToGraphics(Graphics2D graphics,
                                          int bgTilesX, int bgTilesY, int outWidth, int outHeight,
                                          WorldData biomeData, Map<Identifier, BufferedImage> textureImageMap,
                                          DimensionMarkersData globalMarkers, DimensionMarkersData localMarkers,
                                          boolean showMarkers, int minX, int minY,
                                          int scale, BufferedImage bg) {
        getListener().setStatusString("gui.antiqueatlas.export.rendering.background");
        getListener().setProgressMax(bgTilesX * bgTilesY);
        //================ Draw map background ================
        // Top left corner:
        graphics.drawImage(bg, 0, 0, BG_TILE_SIZE * scale, BG_TILE_SIZE * scale,
                0, 0, BG_TILE_SIZE, BG_TILE_SIZE, null);
        getListener().addProgress(1);
        // Topmost row:
        for (int x = 1; x < bgTilesX; x++) {
            graphics.drawImage(bg, x * BG_TILE_SIZE * scale, 0, (x + 1) * BG_TILE_SIZE * scale, BG_TILE_SIZE * scale,
                    BG_TILE_SIZE, 0, BG_TILE_SIZE * 2, BG_TILE_SIZE, null);
            getListener().addProgress(1);
        }
        // Leftmost column:
        for (int y = 1; y < bgTilesY; y++) {
            graphics.drawImage(bg, 0, y * BG_TILE_SIZE * scale, BG_TILE_SIZE * scale, (y + 1) * BG_TILE_SIZE * scale,
                    0, BG_TILE_SIZE, BG_TILE_SIZE, BG_TILE_SIZE * 2, null);
            getListener().addProgress(1);
        }
        // Middle:
        for (int x = 1; x < bgTilesX; x++) {
            for (int y = 1; y < bgTilesY; y++) {
                graphics.drawImage(bg,
                        x * BG_TILE_SIZE * scale, y * BG_TILE_SIZE * scale,
                        (x + 1) * BG_TILE_SIZE * scale, (y + 1) * BG_TILE_SIZE * scale,
                        BG_TILE_SIZE, BG_TILE_SIZE, BG_TILE_SIZE * 2, BG_TILE_SIZE * 2, null);
                getListener().addProgress(1);
            }
        }
        // Top right corner:
        graphics.drawImage(bg, outWidth - BG_TILE_SIZE * scale, 0,
                outWidth, BG_TILE_SIZE * scale,
                BG_TILE_SIZE * 2, 0, BG_TILE_SIZE * 3, BG_TILE_SIZE, null);
        getListener().addProgress(1);
        // Rightmost column:
        for (int y = 1; y < bgTilesY; y++) {
            graphics.drawImage(bg,
                    outWidth - BG_TILE_SIZE * scale, y * BG_TILE_SIZE * scale,
                    outWidth, (y + 1) * BG_TILE_SIZE * scale,
                    BG_TILE_SIZE * 2, BG_TILE_SIZE, BG_TILE_SIZE * 3, BG_TILE_SIZE * 2, null);
            getListener().addProgress(1);
        }
        // Bottom left corner:
        graphics.drawImage(bg, 0, outHeight - BG_TILE_SIZE * scale,
                BG_TILE_SIZE * scale, outHeight,
                0, BG_TILE_SIZE * 2, BG_TILE_SIZE, BG_TILE_SIZE * 3, null);
        getListener().addProgress(1);
        // Bottommost row:
        for (int x = 1; x < bgTilesX; x++) {
            graphics.drawImage(bg, x * BG_TILE_SIZE * scale, outHeight - BG_TILE_SIZE * scale,
                    (x + 1) * BG_TILE_SIZE * scale, outHeight,
                    BG_TILE_SIZE, BG_TILE_SIZE * 2, BG_TILE_SIZE * 2, BG_TILE_SIZE * 3, null);
            getListener().addProgress(1);
        }
        // Bottom right corner:
        graphics.drawImage(bg, outWidth - BG_TILE_SIZE * scale, outHeight - BG_TILE_SIZE * scale,
                outWidth, outHeight, BG_TILE_SIZE * 2, BG_TILE_SIZE * 2, BG_TILE_SIZE * 3, BG_TILE_SIZE * 3, null);
        getListener().addProgress(1);

        //============= Draw actual map tiles ==============
        Rect scope = biomeData.getScope();
        getListener().setStatusString("gui.antiqueatlas.export.rendering.map");
        getListener().setProgressMax(scope.getHeight() * scope.getWidth());

        TileRenderIterator iter = new TileRenderIterator(biomeData);
        while (iter.hasNext()) {
            SubTileQuartet subtiles = iter.next();
            for (SubTile subtile : subtiles) {
                if (subtile == null || subtile.tile == null) continue;

                // Load tile texture
                Identifier texture = TileTextureMap.instance().getTexture(subtile).getTexture();
                BufferedImage tileImage = textureImageMap.get(texture);
                if (tileImage == null) continue;

                graphics.drawImage(tileImage,

                        TILE_SIZE + subtile.x * TILE_SIZE / 2,
                        TILE_SIZE + subtile.y * TILE_SIZE / 2,

                        TILE_SIZE + (subtile.x + 1) * TILE_SIZE / 2,
                        TILE_SIZE + (subtile.y + 1) * TILE_SIZE / 2,

                        subtile.getTextureU() * TILE_SIZE / 2,
                        subtile.getTextureV() * TILE_SIZE / 2,

                        (subtile.getTextureU() + 1) * TILE_SIZE / 2,
                        (subtile.getTextureV() + 1) * TILE_SIZE / 2,

                        null);
            }
            getListener().addProgress(1);
        }

        //============== Draw markers ================
        // Draw local markers on top of global markers
        getListener().setStatusString("gui.antiqueatlas.export.rendering.markers");
        getListener().setProgressMax(-1);

        List<Marker> markers = new ArrayList<>();
        for (int x = biomeData.getScope().minX / MarkersData.CHUNK_STEP;
             x <= biomeData.getScope().maxX / MarkersData.CHUNK_STEP; x++) {
            for (int z = biomeData.getScope().minY / MarkersData.CHUNK_STEP;
                 z <= biomeData.getScope().maxY / MarkersData.CHUNK_STEP; z++) {

                markers.clear();
                List<Marker> globalMarkersAt = globalMarkers.getMarkersAtChunk(x, z);
                if (globalMarkersAt != null) {
                    markers.addAll(globalMarkers.getMarkersAtChunk(x, z));
                }
                if (localMarkers != null) {
                    List<Marker> localMarkersAt = localMarkers.getMarkersAtChunk(x, z);
                    if (localMarkersAt != null) {
                        markers.addAll(localMarkersAt);
                    }
                }

                for (Marker marker : markers) {
                    MarkerType type = MarkerType.REGISTRY.get(marker.getType());
                    if (type == null) {
                        Log.warn("Could not find marker data for type: %s\n", marker.getType());
                        continue;
                    }

                    if (!marker.isVisibleAhead() &&
                            !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
                        continue;
                    }

                    if (type.shouldHide(!showMarkers, 0)) {
                        continue;
                    }

                    type.calculateMip(1, 1, 1);
                    MarkerRenderInfo info = type.getRenderInfo(1, 1, 1);
                    type.resetMip();

                    // Load marker texture
                    Identifier texture = info.tex.getTexture();
                    BufferedImage markerImage = textureImageMap.get(texture);
                    if (markerImage == null)
                        continue;

                    int markerX = marker.getX() - minX;
                    int markerY = marker.getZ() - minY;

                    graphics.drawImage(
                            markerImage,
                            (int) (markerX + info.x), (int) (markerY + info.y),
                            info.tex.width(), info.tex.height(), null);
                }
            }
        }
    }
}
