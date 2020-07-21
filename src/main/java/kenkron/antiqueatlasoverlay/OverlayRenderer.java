package kenkron.antiqueatlasoverlay;

import com.mojang.blaze3d.platform.GlStateManager;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.client.*;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Rect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.opengl.GL11;

import java.util.List;

@Environment(EnvType.CLIENT)
public class OverlayRenderer extends DrawableHelper {
    /**
     * Number of blocks per chunk in minecraft. This is certianly stored
     * somewhere else, but I couldn't be bothered to find it.
     */
    private static final int CHUNK_SIZE = 16;

    /**
     * Convenience method that returns the first atlas ID for all atlas items
     * the player is currently carrying in the hotbar/offhand. Returns null if
     * there are none. Offhand gets priority.
     **/
    private Integer getPlayerAtlas(PlayerEntity player) {
        if (!AntiqueAtlasMod.CONFIG.gameplay.itemNeeded) {
            return player.getUuid().hashCode();
        }

        ItemStack stack = player.getOffHandStack();
        if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            return stack.getDamage();
        }

        for (int i = 0; i < 9; i++) {
            stack = player.inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack.getDamage();
            }
        }

        return null;
    }

    private MinecraftClient client;
    private PlayerEntity player;
    private ChunkPos chunkPos;
    private RegistryKey<DimensionType> dimension;
    private Integer atlasID;

    public void drawOverlay(MatrixStack matrices) {
        // Overlay must close if Atlas GUI is opened
        if (MinecraftClient.getInstance().currentScreen instanceof GuiAtlas) {
            return;
        }

        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().player == null) {
            return;
        }

        this.client = MinecraftClient.getInstance();
        this.player = MinecraftClient.getInstance().player;
        this.chunkPos = MinecraftClient.getInstance().world.getChunk(player.getBlockPos()).getPos();
        this.dimension = this.player.getEntityWorld().getDimensionRegistryKey();

        if (AntiqueAtlasMod.CONFIG.appearance.requiresHold) {
            ItemStack stack = player.getMainHandStack();
            ItemStack stack2 = player.getOffHandStack();

            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                atlasID = ((ItemAtlas) stack.getItem()).getAtlasID(stack);
            } else if (!stack2.isEmpty() && stack2.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                atlasID = ((ItemAtlas) stack2.getItem()).getAtlasID(stack2);
            }
        } else {
            atlasID = getPlayerAtlas(player);
        }

        if (atlasID != null) {
            drawMinimap(matrices);
        }

        atlasID = null;
    }

    private void drawMinimap(MatrixStack matrices) {
//        GlStateManager.color4f(1, 1, 1, 1);
//        GlStateManager.enableBlend();
//        GlStateManager.alphaFunc(GL11.GL_GREATER, 0); // So light detail on tiles is
        // visible
        this.client.getTextureManager().bindTexture(Textures.BOOK);
        drawTexture(matrices, 0, 0, (int) (GuiAtlas.WIDTH * 1.5), (int) (GuiAtlas.HEIGHT * 1.5),
            0,
            0,
            310,
            218,
            310,
            218
        );

        matrices.push();
        matrices.push();
        matrices.scale(2, 2, 1);
        drawTiles(matrices);
        if (AntiqueAtlasMod.CONFIG.appearance.markerSize > 0) {
            drawMarkers(matrices);
        }

        matrices.pop();


        drawPlayer(matrices);


        // Overlay the frame so that edges of the map are smooth:
        GlStateManager.color4f(1, 1, 1, 1);
        matrices.pop();
        MinecraftClient.getInstance().getTextureManager().bindTexture(Textures.BOOK_FRAME);
        drawTexture(matrices, 0, 0, (int) (GuiAtlas.WIDTH * 1.5), (int) (GuiAtlas.HEIGHT * 1.5),
                0,
                0,
                310,
                218,
                310,
                218
        );
        GlStateManager.disableBlend();
    }

    private void drawTiles(MatrixStack matrices) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(20, 0, client.getWindow().getWidth(),
                client.getWindow().getHeight() - 100);

        DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
                atlasID, MinecraftClient.getInstance().world).getDimensionData(dimension);

        TileRenderIterator iter = new TileRenderIterator(biomeData);
        Rect iteratorScope = getChunkCoverage(player.getPos());
        iter.setScope(iteratorScope);

        iter.setStep(1);
        Vec3d chunkPosition = player.getPos().multiply(1D / CHUNK_SIZE, 1D / CHUNK_SIZE, 1D / CHUNK_SIZE);
        int shapeMiddleX = (int) ((GuiAtlas.WIDTH * 1.5F) / 4F);
        int shapeMiddleY = (int) ((GuiAtlas.HEIGHT * 1.5F) / 4F);
        SetTileRenderer renderer = new SetTileRenderer(matrices, AntiqueAtlasMod.CONFIG.appearance.tileSize / 2);

        while (iter.hasNext()) {
            SubTileQuartet subtiles = iter.next();
            for (SubTile subtile : subtiles) {
                if (subtile == null || subtile.tile == null)
                    continue;
                // Position of this subtile (measured in chunks) relative to the
                // player
                float relativeChunkPositionX = (float) (subtile.x / 2.0
                        + iteratorScope.minX - chunkPosition.x);
                float relativeChunkPositionY = (float) (subtile.y / 2.0
                        + iteratorScope.minY - chunkPosition.z);
                renderer.addTileCorner(
                        BiomeTextureMap.instance().getTexture(subtile.variationNumber, subtile.tile),
                        shapeMiddleX
                                + (int) Math.floor(relativeChunkPositionX
                                * AntiqueAtlasMod.CONFIG.appearance.tileSize),
                        shapeMiddleY
                                + (int) Math.floor(relativeChunkPositionY
                                * AntiqueAtlasMod.CONFIG.appearance.tileSize), subtile.getTextureU(),
                        subtile.getTextureV());
            }
        }
        renderer.draw();
        // get GL back to normal
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.color4f(1, 1, 1, 1);
    }

    private void drawMarkers(MatrixStack matrices) {
        // biomeData needed to prevent undiscovered markers from appearing
        DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
                atlasID, MinecraftClient.getInstance().world).getDimensionData(
                dimension);
        DimensionMarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData
                .getData().getMarkersDataInDimension(dimension);

        // Draw global markers:
        drawMarkersData(matrices, globalMarkersData, biomeData);

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(
                atlasID, MinecraftClient.getInstance().world);
        DimensionMarkersData localMarkersData = null;
        if (markersData != null) {
            localMarkersData = markersData.getMarkersDataInDimension(dimension);
        }

        // Draw local markers:
        drawMarkersData(matrices, localMarkersData, biomeData);
    }

    private void drawPlayer(MatrixStack matrices) {
        // Draw player icon:

        MinecraftClient.getInstance().getTextureManager().bindTexture(Textures.PLAYER);
        matrices.push();

        matrices.translate((int)((GuiAtlas.WIDTH * 1.5F) / 2F), (int)((GuiAtlas.HEIGHT * 1.5F) / 2F), 0);
        matrices.multiply(new Quaternion(Vector3f.POSITIVE_Z, this.player.getHeadYaw() + 180, true));
        matrices.translate(-AntiqueAtlasMod.CONFIG.appearance.playerIconWidth / 2.0, -AntiqueAtlasMod.CONFIG.appearance.playerIconHeight / 2.0, 0);

        drawTexture(matrices, 0, 0, AntiqueAtlasMod.CONFIG.appearance.playerIconWidth, AntiqueAtlasMod.CONFIG.appearance.playerIconHeight, 0, 0, 8, 7, 8, 7);
        matrices.pop();
    }

    private void drawMarkersData(MatrixStack matrices, DimensionMarkersData markersData, DimensionData biomeData) {
        //this will be large enough to include markers that are larger than tiles
        Rect mcchunks = getChunkCoverage(player.getPos());
        Rect chunks = new Rect((int) Math.floor(mcchunks.minX / MarkersData.CHUNK_STEP),
                (int) Math.floor(mcchunks.minY / MarkersData.CHUNK_STEP),
                (int) Math.ceil(mcchunks.maxX / MarkersData.CHUNK_STEP),
                (int) Math.ceil(mcchunks.maxY / MarkersData.CHUNK_STEP));

        int shapeMiddleX = (int) ((GuiAtlas.WIDTH * 1.5F) / 4F);
        int shapeMiddleY = (int) ((GuiAtlas.HEIGHT * 1.5F) / 4F);
        Vec3d chunkPosition = player.getPos().multiply(1D / CHUNK_SIZE, 1D / CHUNK_SIZE, 1D / CHUNK_SIZE);

        for (int x = chunks.minX; x <= chunks.maxX; x++) {
            for (int z = chunks.minY; z <= chunks.maxY; z++) {
                //A marker chunk is greater than a Minecraft chunk
                List<Marker> markers = markersData.getMarkersAtChunk(
                        Math.round(x),
                        Math.round(z));
                if (markers == null)
                    continue;
                for (Marker marker : markers) {
                    float relativeChunkPositionX = (float) (marker.getChunkX()
                            - chunkPosition.x);
                    float relativeChunkPositionY = (float) (marker.getChunkZ()
                             - chunkPosition.z);

                    renderMarker(matrices, marker,
                            shapeMiddleX
                                    + (int) Math.floor(relativeChunkPositionX * 8),
                            shapeMiddleY
                                    + (int) Math.floor(relativeChunkPositionY * 8), biomeData);
                }
            }
        }
    }

    private void renderMarker(MatrixStack matrices, Marker marker, int x, int y, DimensionData biomeData) {
        if (!marker.isVisibleAhead() && !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
            return;
        }

        MarkerType type = MarkerType.REGISTRY.get(AntiqueAtlasMod.id(marker.getType()));
        // TODO Fabric - Scale factor?
        MarkerRenderInfo info = type.getRenderInfo(1, AntiqueAtlasMod.CONFIG.appearance.tileSize, 1);
        MinecraftClient.getInstance().getTextureManager().bindTexture(info.tex);
        drawTexture(matrices,
                x - GuiAtlas.MARKER_SIZE / 4 + 1,
                y - GuiAtlas.MARKER_SIZE / 4 + 4,
                GuiAtlas.MARKER_SIZE / 2,
                GuiAtlas.MARKER_SIZE / 2,
                0,
                0,
                GuiAtlas.MARKER_SIZE,
                GuiAtlas.MARKER_SIZE,
                GuiAtlas.MARKER_SIZE,
                GuiAtlas.MARKER_SIZE);
//        AtlasRenderHelper.drawFullTexture(matrices, info.tex, x, y, AntiqueAtlasMod.CONFIG.appearance.markerSize, AntiqueAtlasMod.CONFIG.appearance.markerSize);
    }

    private Rect getChunkCoverage(Vec3d position) {
        int minChunkX = (int) Math.floor(position.x / CHUNK_SIZE
                - (GuiAtlas.WIDTH) / (4f * AntiqueAtlasMod.CONFIG.appearance.tileSize));
        minChunkX -= 4;
        int minChunkY = (int) Math.floor(position.z / CHUNK_SIZE
                - (GuiAtlas.HEIGHT) / (4f * AntiqueAtlasMod.CONFIG.appearance.tileSize));
        minChunkY -= 2;
        int maxChunkX = (int) Math.ceil(position.x / CHUNK_SIZE
                + (GuiAtlas.WIDTH) / (4f * AntiqueAtlasMod.CONFIG.appearance.tileSize));
        maxChunkX += 2;
        int maxChunkY = (int) Math.ceil(position.z / CHUNK_SIZE
                + (GuiAtlas.HEIGHT) / (4f * AntiqueAtlasMod.CONFIG.appearance.tileSize));
        maxChunkY += 1;
        return new Rect(minChunkX, minChunkY, maxChunkX, maxChunkY);
    }
}
