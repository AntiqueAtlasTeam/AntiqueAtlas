package kenkron.antiqueatlasoverlay;

import com.mojang.blaze3d.platform.GlStateManager;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.client.*;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.marker.DimensionMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerRenderInfo;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import hunternif.mc.atlas.util.Rect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class OverlayRenderer
{
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
    private static Integer getPlayerAtlas(PlayerEntity player) {
        if (!SettingsConfig.gameplay.itemNeeded) {
            return player.getUuid().hashCode();
        }

        ItemStack stack = player.getOffHandStack();
        if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            return stack.getDamage();
        }

        for (int i = 0; i < 9; i++) {
            stack = player.inventory.getInvStack(i);
            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                return stack.getDamage();
            }
        }

        return null;
    }

    public static void drawOverlay(int gameWidth, int gameHeight) {

        if (!AAOConfig.appearance.enabled) {
            return;
        }

        // Overlay must close if Atlas GUI is opened
        if (MinecraftClient.getInstance().currentScreen instanceof GuiAtlas) {
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        Integer atlas = null;

        if (AAOConfig.appearance.requiresHold) {
            ItemStack stack = player.getMainHandStack();
            ItemStack stack2 = player.getOffHandStack();

            if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                atlas = ((ItemAtlas) stack.getItem()).getAtlasID(stack);
            } else if (!stack2.isEmpty() && stack2.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                atlas = ((ItemAtlas) stack2.getItem()).getAtlasID(stack2);
            }
        } else {
            atlas = getPlayerAtlas(player);
        }

        if (atlas != null) {
            // remember, y=0 is at the top
            Rect bounds = new Rect().setOrigin(AAOConfig.position.xPosition, AAOConfig.position.yPosition);
            if (AAOConfig.position.alignRight) {
                bounds.minX = gameWidth - (AAOConfig.position.width + AAOConfig.position.xPosition);
            }
            if (AAOConfig.position.alignBottom) {
                bounds.minY = gameHeight - (AAOConfig.position.height + AAOConfig.position.yPosition);
            }

            bounds.setSize(AAOConfig.position.width, AAOConfig.position.height);
            drawMinimap(bounds, atlas, player.getPos(), player.getHeadYaw(), player.dimension);
        }
    }

    private static void drawMinimap(Rect shape, int atlasID, Vec3d position, float rotation,
                             DimensionType dimension) {
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0); // So light detail on tiles is
        // visible
        AtlasRenderHelper.drawFullTexture(Textures.BOOK, shape.minX,
                shape.minY, shape.getWidth(), shape.getHeight());
        Rect innerShape = new Rect(
                // stop it eclipse
                shape.minX + Math.round(AAOConfig.appearance.borderX * shape.getWidth()),
                shape.minY + Math.round(AAOConfig.appearance.borderY * shape.getHeight()),
                shape.maxX - Math.round(AAOConfig.appearance.borderX * shape.getWidth()),
                shape.maxY - Math.round(AAOConfig.appearance.borderY * shape.getHeight()));
        drawTiles(innerShape, atlasID, position, dimension);

        if (AAOConfig.appearance.markerSize > 0) {
            drawMarkers(innerShape, atlasID, position, dimension);
            int shapeMiddleX = (shape.minX + shape.maxX) / 2;
            int shapeMiddleY = (shape.minY + shape.maxY) / 2;
            drawPlayer(shapeMiddleX, shapeMiddleY, rotation);
        }

        // Overlay the frame so that edges of the map are smooth:
        GlStateManager.color4f(1, 1, 1, 1);
        AtlasRenderHelper.drawFullTexture(Textures.BOOK_FRAME, shape.minX,
                shape.minY, shape.getWidth(), shape.getHeight());
        GlStateManager.disableBlend();
    }

    private static void drawTiles(Rect shape, int atlasID, Vec3d position,
                           DimensionType dimension) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        // glScissor uses the default window coordinates,
        // the display window does not. We need to fix this
        glScissorGUI(shape);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
                atlasID, MinecraftClient.getInstance().world).getDimensionData(dimension);

        TileRenderIterator iter = new TileRenderIterator(biomeData);
        Rect iteratorScope = getChunkCoverage(position, shape);
        iter.setScope(iteratorScope);

        iter.setStep(1);
        Vec3d chunkPosition = new Vec3d(
                position.x / CHUNK_SIZE,
                position.y / CHUNK_SIZE,
                position.z / CHUNK_SIZE);
        int shapeMiddleX = (shape.minX + shape.maxX) / 2;
        int shapeMiddleY = (shape.minY + shape.maxY) / 2;
        SetTileRenderer renderer = new SetTileRenderer(AAOConfig.appearance.tileSize / 2);

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
                                * AAOConfig.appearance.tileSize),
                        shapeMiddleY
                                + (int) Math.floor(relativeChunkPositionY
                                * AAOConfig.appearance.tileSize), subtile.getTextureU(),
                        subtile.getTextureV());
            }
        }
        renderer.draw();
        // get GL back to normal
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.color4f(1, 1, 1, 1);
    }

    private static void drawMarkers(Rect shape, int atlasID, Vec3d position,
                                    DimensionType dimension) {

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        // glScissor uses the default window coordinates,
        // the display window does not. We need to fix this
        glScissorGUI(shape);

        // biomeData needed to prevent undiscovered markers from appearing
        DimensionData biomeData = AntiqueAtlasMod.atlasData.getAtlasData(
                atlasID, MinecraftClient.getInstance().world).getDimensionData(
                dimension);
        DimensionMarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData
                .getData().getMarkersDataInDimension(dimension);

        // Draw global markers:
        drawMarkersData(globalMarkersData, shape, biomeData, position);

        MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(
                atlasID, MinecraftClient.getInstance().world);
        DimensionMarkersData localMarkersData = null;
        if (markersData != null) {
            localMarkersData = markersData.getMarkersDataInDimension(dimension);
        }

        // Draw local markers:
        drawMarkersData(localMarkersData, shape, biomeData, position);

        // get GL back to normal
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.color4f(1, 1, 1, 1);
    }

    private static void drawPlayer(float x, float y, float rotation) {
        // Draw player icon:

        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 0);
        GlStateManager.rotatef(180 + rotation, 0, 0, 1);
        GlStateManager.translatef(-AAOConfig.appearance.playerIconWidth / 2, -AAOConfig.appearance.playerIconHeight / 2, 0);
        AtlasRenderHelper.drawFullTexture(Textures.PLAYER, 0, 0, AAOConfig.appearance.playerIconWidth, AAOConfig.appearance.playerIconHeight);
        GlStateManager.popMatrix();
        GlStateManager.color4f(1, 1, 1, 1);
    }

    private static void drawMarkersData(DimensionMarkersData markersData,
                                 Rect shape, DimensionData biomeData, Vec3d position) {

        //this will be large enough to include markers that are larger than tiles
        Rect markerShape = new Rect(shape.minX - AAOConfig.appearance.markerSize / 2, shape.minY - AAOConfig.appearance.markerSize / 2,
                shape.maxX + AAOConfig.appearance.markerSize / 2, shape.maxY + AAOConfig.appearance.markerSize / 2);

        Rect mcchunks = getChunkCoverage(position, markerShape);
        Rect chunks = new Rect((int) Math.floor(mcchunks.minX / MarkersData.CHUNK_STEP),
                (int) Math.floor(mcchunks.minY / MarkersData.CHUNK_STEP),
                (int) Math.ceil(mcchunks.maxX / MarkersData.CHUNK_STEP),
                (int) Math.ceil(mcchunks.maxY / MarkersData.CHUNK_STEP));

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
                    int relativeChunkPositionX = AAOConfig.appearance.tileSize * (2 * (marker.getX() / 2) - 2 * (int) Math.floor(position.x / 2))
                            / CHUNK_SIZE;
                    int relativeChunkPositionY = AAOConfig.appearance.tileSize * (2 * (marker.getZ() / 2) - 2 * (int) Math.floor(position.z / 2))
                            / CHUNK_SIZE;
                    int guiX = (int) Math.floor(shapeMiddleX - AAOConfig.appearance.markerSize / 2 + relativeChunkPositionX);
                    int guiY = (int) Math.floor(shapeMiddleY - AAOConfig.appearance.markerSize / 2 + relativeChunkPositionY);
                    renderMarker(marker, guiX, guiY, biomeData);
                }
            }
        }
    }

    private static void renderMarker(Marker marker, int x, int y,
                              DimensionData biomeData) {
        if (!marker.isVisibleAhead()
                && !biomeData.hasTileAt(marker.getChunkX(), marker.getChunkZ())) {
            return;
        }
        GlStateManager.color4f(1, 1, 1, 1);
        MarkerType m = MarkerRegistry.find(marker.getType());
        if (m == null){
        	AntiqueAtlasOverlayMod.LOGGER.warn("Could not find marker type for {}", marker.getId());
        	return;
        }
        // TODO Fabric - Scale factor?
        MarkerRenderInfo info = m.getRenderInfo(1, AAOConfig.appearance.tileSize, 1);
        AtlasRenderHelper.drawFullTexture(info.tex, x, y, AAOConfig.appearance.markerSize, AAOConfig.appearance.markerSize);
    }

    private static Rect getChunkCoverage(Vec3d position, Rect windowShape) {
        int minChunkX = (int) Math.floor(position.x / CHUNK_SIZE
                - windowShape.getWidth() / (2f * AAOConfig.appearance.tileSize));
        minChunkX -= 1;// IDK
        int minChunkY = (int) Math.floor(position.z / CHUNK_SIZE
                - windowShape.getHeight() / (2f * AAOConfig.appearance.tileSize));
        minChunkY -= 1;// IDK
        int maxChunkX = (int) Math.ceil(position.x / CHUNK_SIZE
                + windowShape.getWidth() / (2f * AAOConfig.appearance.tileSize));
        maxChunkX += 1;
        int maxChunkY = (int) Math.ceil(position.z / CHUNK_SIZE
                + windowShape.getHeight() / (2f * AAOConfig.appearance.tileSize));
        maxChunkY += 1;
        return new Rect(minChunkX, minChunkY, maxChunkX, maxChunkY);
    }

    /**
     * Calls GL11.glScissor, but uses GUI coordinates
     */
    private static void glScissorGUI(Rect shape) {
        // glScissor uses the default window coordinates,
        // the display window does not. We need to fix this
        float scissorScaleX = MinecraftClient.getInstance().window.getWidth() * 1.0f
                / MinecraftClient.getInstance().window.getScaledWidth();

        int mcHeight = MinecraftClient.getInstance().window.getHeight();
        float scissorScaleY = mcHeight * 1.0f / MinecraftClient.getInstance().window.getScaledHeight();

        GL11.glScissor((int) (shape.minX * scissorScaleX),
                (int) (mcHeight - shape.maxY * scissorScaleY),
                (int) (shape.getWidth() * scissorScaleX),
                (int) (shape.getHeight() * scissorScaleY));
    }
}
