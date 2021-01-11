package kenkron.antiqueatlasoverlay;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.client.BiomeTextureMap;
import hunternif.mc.impl.atlas.client.SubTile;
import hunternif.mc.impl.atlas.client.SubTileQuartet;
import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.TileRenderIterator;
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
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
@OnlyIn(Dist.CLIENT)
public class OverlayRenderer extends AbstractGui {
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
        if (!AntiqueAtlasConfig.itemNeeded.get()) {
            return player.getUniqueID().hashCode();
        }

        ItemStack stack = player.getHeldItemOffhand();
        if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            return AtlasItem.getAtlasID(stack);
        }

        for (int i = 0; i < 9; i++) {
            stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return AtlasItem.getAtlasID(stack);
            }
        }

        return null;
    }

    private static final float INNER_ELEMENTS_SCALE_FACTOR = 1.9F;

    private Minecraft client;
    private PlayerEntity player;
    private World world;
    private Integer atlasID;

    public void drawOverlay(MatrixStack matrices) {
        // Overlay must close if Atlas GUI is opened
        if (Minecraft.getInstance().currentScreen instanceof GuiAtlas) {
            return;
        }

        if (Minecraft.getInstance().world == null || Minecraft.getInstance().player == null) {
            return;
        }

        this.client = Minecraft.getInstance();
        this.player = Minecraft.getInstance().player;
        this.world = Minecraft.getInstance().world;

        if (AntiqueAtlasConfig.requiresHold.get()) {
            ItemStack stack = player.getHeldItemMainhand();
            ItemStack stack2 = player.getHeldItemOffhand();

            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                atlasID = AtlasItem.getAtlasID(stack);
            } else if (!stack2.isEmpty() && stack2.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                atlasID = AtlasItem.getAtlasID(stack2);
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
        this.client.getTextureManager().bindTexture(Textures.BOOK);
        blit(matrices, 0, 0, (int) (GuiAtlas.WIDTH * 1.5), (int) (GuiAtlas.HEIGHT * 1.5),
            0,
            0,
            310,
            218,
            310,
            218
        );

        matrices.push();
        matrices.push();
        matrices.scale(INNER_ELEMENTS_SCALE_FACTOR, INNER_ELEMENTS_SCALE_FACTOR, 1F);
        
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTiles(matrices);
        if (AntiqueAtlasConfig.markerSize.get() > 0) {
            drawMarkers(matrices);
        }

        matrices.pop();

        drawPlayer(matrices);


        // Overlay the frame so that edges of the map are smooth:
        matrices.pop();
        Minecraft.getInstance().getTextureManager().bindTexture(Textures.BOOK_FRAME);
        blit(matrices, 0, 0, (int) (GuiAtlas.WIDTH * 1.5), (int) (GuiAtlas.HEIGHT * 1.5),
                0,
                0,
                310,
                218,
                310,
                218
        );
        RenderSystem.disableBlend();
    }

    private void drawTiles(MatrixStack matrices) {
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        WorldData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
                atlasID, this.world).getWorldData(this.world.getDimensionKey());

        TileRenderIterator iter = new TileRenderIterator(biomeData);
        Rect iteratorScope = getChunkCoverage(player.getPositionVec());
        iter.setScope(iteratorScope);

        iter.setStep(1);
        Vector3d chunkPosition = player.getPositionVec().mul(1D / CHUNK_SIZE, 1D / CHUNK_SIZE, 1D / CHUNK_SIZE);
        int shapeMiddleX = (int) ((GuiAtlas.WIDTH * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        int shapeMiddleY = (int) ((GuiAtlas.HEIGHT * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        SetTileRenderer renderer = new SetTileRenderer(matrices, AntiqueAtlasConfig.tileSize.get() / 2);

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
                                * AntiqueAtlasConfig.tileSize.get()),
                        shapeMiddleY
                                + (int) Math.floor(relativeChunkPositionY
                                * AntiqueAtlasConfig.tileSize.get()), subtile.getTextureU(),
                        subtile.getTextureV());
            }
        }
        renderer.draw();
    }

    private void drawMarkers(MatrixStack matrices) {
        // biomeData needed to prevent undiscovered markers from appearing
        WorldData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
                atlasID, this.world).getWorldData(
                this.world.getDimensionKey());
        DimensionMarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData
                .getData().getMarkersDataInWorld(this.world.getDimensionKey());

        // Draw global markers:
        drawMarkersData(matrices, globalMarkersData, biomeData);

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(
                atlasID, Minecraft.getInstance().world);
        DimensionMarkersData localMarkersData = null;
        if (markersData != null) {
            localMarkersData = markersData.getMarkersDataInWorld(world.getDimensionKey());
        }

        // Draw local markers:
        drawMarkersData(matrices, localMarkersData, biomeData);
    }

    private void drawPlayer(MatrixStack matrices) {
        // Draw player icon:

        Minecraft.getInstance().getTextureManager().bindTexture(Textures.PLAYER);
        matrices.push();

        matrices.translate((int)((GuiAtlas.WIDTH * 1.5F) / 2F), (int)((GuiAtlas.HEIGHT * 1.5F) / 2F), 0);
        matrices.rotate(new Quaternion(Vector3f.ZP, this.player.getRotationYawHead() + 180, true));
        matrices.translate(-AntiqueAtlasConfig.playerIconWidth.get() / 2.0, -AntiqueAtlasConfig.playerIconHeight.get() / 2.0, 0);

        blit(matrices, 0, 0, AntiqueAtlasConfig.playerIconWidth.get(), AntiqueAtlasConfig.playerIconHeight.get(), 0, 0, 8, 7, 8, 7);
        matrices.pop();
    }

    private void drawMarkersData(MatrixStack matrices, DimensionMarkersData markersData, WorldData biomeData) {
        //this will be large enough to include markers that are larger than tiles
        Rect mcchunks = getChunkCoverage(player.getPositionVec());
        Rect chunks = new Rect(mcchunks.minX / MarkersData.CHUNK_STEP,
                mcchunks.minY / MarkersData.CHUNK_STEP,
                (int) Math.ceil((float)mcchunks.maxX / MarkersData.CHUNK_STEP),
                (int) Math.ceil((float)mcchunks.maxY / MarkersData.CHUNK_STEP));

        int shapeMiddleX = (int) ((GuiAtlas.WIDTH * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        int shapeMiddleY = (int) ((GuiAtlas.HEIGHT * 1.5F) / (INNER_ELEMENTS_SCALE_FACTOR * 2));
        Vector3d chunkPosition = player.getPositionVec().mul(1D / CHUNK_SIZE, 1D / CHUNK_SIZE, 1D / CHUNK_SIZE);

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

    private void renderMarker(MatrixStack matrices, Marker marker, int x, int y, WorldData biomeData) {
        int tileHalfSize = GuiAtlas.MARKER_SIZE / 16;
        if (!((x + tileHalfSize) <= 240 && (x - tileHalfSize >= 3) && (y + tileHalfSize) < 166 && (y - tileHalfSize) >= 0))
            return;

        if (!marker.isVisibleAhead() && !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
            return;
        }

        MarkerType type = MarkerType.REGISTRY.getOrDefault(marker.getType());
        // TODO Fabric - Scale factor?
        MarkerRenderInfo info = type.getRenderInfo(1, AntiqueAtlasConfig.tileSize.get(), 1);
        Minecraft.getInstance().getTextureManager().bindTexture(info.tex);
        blit(matrices,
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
//        AtlasRenderHelper.drawFullTexture(matrices, info.tex, x, y, AntiqueAtlasMod.CONFIG.markerSize, AntiqueAtlasMod.CONFIG.markerSize);
    }

    private Rect getChunkCoverage(Vector3d position) {
        int minChunkX = (int) Math.floor(position.x / CHUNK_SIZE
                - (GuiAtlas.WIDTH) / (4f * AntiqueAtlasConfig.tileSize.get()));
        minChunkX -= 4;
        int minChunkY = (int) Math.floor(position.z / CHUNK_SIZE
                - (GuiAtlas.HEIGHT) / (4f * AntiqueAtlasConfig.tileSize.get()));
        minChunkY -= 3;
        int maxChunkX = (int) Math.ceil(position.x / CHUNK_SIZE
                + (GuiAtlas.WIDTH) / (4f * AntiqueAtlasConfig.tileSize.get()));
        maxChunkX += 4;
        int maxChunkY = (int) Math.ceil(position.z / CHUNK_SIZE
                + (GuiAtlas.HEIGHT) / (4f * AntiqueAtlasConfig.tileSize.get()));
        maxChunkY += 2;
        return new Rect(minChunkX, minChunkY, maxChunkX, maxChunkY);
    }
}
