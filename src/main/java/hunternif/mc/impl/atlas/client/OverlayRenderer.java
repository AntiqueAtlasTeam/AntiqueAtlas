package hunternif.mc.impl.atlas.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import hunternif.mc.api.client.AtlasClientAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.core.WorldData;
import hunternif.mc.impl.atlas.item.AtlasItem;
import hunternif.mc.impl.atlas.marker.DimensionMarkersData;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.registry.MarkerRenderInfo;
import hunternif.mc.impl.atlas.registry.MarkerType;
import hunternif.mc.impl.atlas.util.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.lwjgl.opengl.GL11;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class OverlayRenderer extends GuiComponent {
    /**
     * Number of blocks per chunk in minecraft. This is certianly stored
     * somewhere else, but I couldn't be bothered to find it.
     */
    private static final int CHUNK_SIZE = 16;
    private static final float INNER_ELEMENTS_SCALE_FACTOR = 1.9F;
    private Player player;
    private Level world;

    public void drawOverlay(PoseStack matrices, MultiBufferSource vertexConsumer, int light, ItemStack atlas) {
        // Overlay must close if Atlas GUI is opened
        if (Minecraft.getInstance().screen instanceof GuiAtlas) {
            return;
        }

        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) {
            return;
        }

        this.player = Minecraft.getInstance().player;
        this.world = Minecraft.getInstance().level;

        if (!atlas.isEmpty() && atlas.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            int atlasID = AtlasItem.getAtlasID(atlas);
            drawMinimap(matrices, atlasID, vertexConsumer, light);
        }
    }

    private void drawMinimap(PoseStack matrices, int atlasID, MultiBufferSource buffer, int light) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        matrices.pushPose();
        matrices.translate(0, 0, 0.01);
        Textures.BOOK.drawWithLight(buffer, matrices, 0, 0, (int) (GuiAtlas.WIDTH * 1.5), (int) (GuiAtlas.HEIGHT * 1.5), light);
        matrices.popPose();

        matrices.pushPose();
        matrices.scale(INNER_ELEMENTS_SCALE_FACTOR, INNER_ELEMENTS_SCALE_FACTOR, 1F);


        drawTiles(buffer, matrices, atlasID, light);
        matrices.translate(0, 0, -0.01);
        if (AntiqueAtlasMod.CONFIG.markerSize > 0) {
            drawMarkers(buffer, matrices, atlasID, light);
        }
        matrices.popPose();

        matrices.translate(0, 0, -0.02);
        drawPlayer(buffer, matrices, light);

        // Overlay the frame so that edges of the map are smooth:
        matrices.translate(0, 0, -0.01);
        Textures.BOOK_FRAME.drawWithLight(buffer, matrices, 0, 0, (int) (GuiAtlas.WIDTH * 1.5), (int) (GuiAtlas.HEIGHT * 1.5), light);

        RenderSystem.disableBlend();
    }

    private void drawTiles(MultiBufferSource buffer, PoseStack matrices, int atlasID, int light) {
        Rect iteratorScope = getChunkCoverage(player.position());
        TileRenderIterator iter = AtlasClientAPI.getTileAPI().getTiles(world, atlasID, iteratorScope, 1);

        Vec3 chunkPosition = player.position().multiply(1D / CHUNK_SIZE, 1D / CHUNK_SIZE, 1D / CHUNK_SIZE);
        int shapeMiddleX = (int) ((GuiAtlas.WIDTH * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        int shapeMiddleY = (int) ((GuiAtlas.HEIGHT * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        SetTileRenderer renderer = new SetTileRenderer(buffer, matrices, AntiqueAtlasMod.CONFIG.tileSize / 2, light);

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
                        TileTextureMap.instance().getTexture(subtile).getTexture(),
                        shapeMiddleX
                                + (int) Math.floor(relativeChunkPositionX
                                * AntiqueAtlasMod.CONFIG.tileSize),
                        shapeMiddleY
                                + (int) Math.floor(relativeChunkPositionY
                                * AntiqueAtlasMod.CONFIG.tileSize), subtile.getTextureU(),
                        subtile.getTextureV());
            }
        }
        renderer.draw();
    }

    private void drawMarkers(MultiBufferSource buffer, PoseStack matrices, int atlasID, int light) {
        // biomeData needed to prevent undiscovered markers from appearing
        WorldData biomeData = AntiqueAtlasMod.tileData.getData(
                atlasID, this.world).getWorldData(
                this.world.dimension());
        DimensionMarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData
                .getData().getMarkersDataInWorld(this.world.dimension());

        // Draw global markers:
        drawMarkersData(buffer, matrices, globalMarkersData, biomeData, light);

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(
                atlasID, Minecraft.getInstance().level);
        if (markersData != null) {
            DimensionMarkersData localMarkersData = markersData.getMarkersDataInWorld(world.dimension());

            // Draw local markers:
            drawMarkersData(buffer, matrices, localMarkersData, biomeData, light);
        }
    }

    private void drawPlayer(MultiBufferSource buffer, PoseStack matrices, int light) {
        matrices.pushPose();

        matrices.translate((int) ((GuiAtlas.WIDTH * 1.5F) / 2F), (int) ((GuiAtlas.HEIGHT * 1.5F) / 2F), 0);
        matrices.mulPose(new Quaternion(Vector3f.ZP, this.player.getYHeadRot() + 180, true));
        matrices.translate(-AntiqueAtlasMod.CONFIG.playerIconWidth / 2.0, -AntiqueAtlasMod.CONFIG.playerIconHeight / 2.0, 0);

        Textures.PLAYER.drawWithLight(buffer, matrices, 0, 0, AntiqueAtlasMod.CONFIG.playerIconWidth, AntiqueAtlasMod.CONFIG.playerIconHeight, light);
        matrices.popPose();
    }

    private void drawMarkersData(MultiBufferSource buffer, PoseStack matrices, DimensionMarkersData markersData, WorldData biomeData, int light) {
        //this will be large enough to include markers that are larger than tiles
        Rect mcchunks = getChunkCoverage(player.position());
        Rect chunks = new Rect(mcchunks.minX / MarkersData.CHUNK_STEP,
                mcchunks.minY / MarkersData.CHUNK_STEP,
                (int) Math.ceil((float) mcchunks.maxX / MarkersData.CHUNK_STEP),
                (int) Math.ceil((float) mcchunks.maxY / MarkersData.CHUNK_STEP));

        int shapeMiddleX = (int) ((GuiAtlas.WIDTH * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        int shapeMiddleY = (int) ((GuiAtlas.HEIGHT * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        Vec3 chunkPosition = player.position().multiply(1D / CHUNK_SIZE, 1D / CHUNK_SIZE, 1D / CHUNK_SIZE);

        for (int x = chunks.minX; x <= chunks.maxX; x++) {
            for (int z = chunks.minY; z <= chunks.maxY; z++) {
                //A marker chunk is greater than a Minecraft chunk
                List<Marker> markers = markersData.getMarkersAtChunk(x, z);
                if (markers == null)
                    continue;
                for (Marker marker : markers) {
                    float relativeChunkPositionX = (float) (marker.getChunkX()
                            - chunkPosition.x);
                    float relativeChunkPositionY = (float) (marker.getChunkZ()
                            - chunkPosition.z);

                    renderMarker(buffer, matrices, marker,
                            shapeMiddleX
                                    + (int) Math.floor(relativeChunkPositionX * 8),
                            shapeMiddleY
                                    + (int) Math.floor(relativeChunkPositionY * 8), biomeData, light);
                }
            }
        }
    }

    private void renderMarker(MultiBufferSource buffer, PoseStack matrices, Marker marker, int x, int y, WorldData biomeData, int light) {
        int tileHalfSize = GuiAtlas.MARKER_SIZE / 16;
        if (!((x + tileHalfSize) <= 240 && (x - tileHalfSize >= 3) && (y + tileHalfSize) < 166 && (y - tileHalfSize) >= 0))
            return;

        if (!marker.isVisibleAhead() && !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
            return;
        }

        MarkerType type = MarkerType.REGISTRY.get(marker.getType());
        // TODO Fabric - Scale factor?
        MarkerRenderInfo info = type.getRenderInfo(1, AntiqueAtlasMod.CONFIG.tileSize, 1);
        info.tex.drawWithLight(buffer, matrices, x - GuiAtlas.MARKER_SIZE / 4 + 4, y - GuiAtlas.MARKER_SIZE / 4 + 4, GuiAtlas.MARKER_SIZE / 2, GuiAtlas.MARKER_SIZE / 2, light);
    }

    private Rect getChunkCoverage(Vec3 position) {
        int minChunkX = (int) Math.floor(position.x / CHUNK_SIZE
                - (GuiAtlas.WIDTH) / (4f * AntiqueAtlasMod.CONFIG.tileSize));
        minChunkX -= 4;
        int minChunkY = (int) Math.floor(position.z / CHUNK_SIZE
                - (GuiAtlas.HEIGHT) / (4f * AntiqueAtlasMod.CONFIG.tileSize));
        minChunkY -= 3;
        int maxChunkX = (int) Math.ceil(position.x / CHUNK_SIZE
                + (GuiAtlas.WIDTH) / (4f * AntiqueAtlasMod.CONFIG.tileSize));
        maxChunkX += 4;
        int maxChunkY = (int) Math.ceil(position.z / CHUNK_SIZE
                + (GuiAtlas.HEIGHT) / (4f * AntiqueAtlasMod.CONFIG.tileSize));
        maxChunkY += 2;
        return new Rect(minChunkX, minChunkY, maxChunkX, maxChunkY);
    }
}
