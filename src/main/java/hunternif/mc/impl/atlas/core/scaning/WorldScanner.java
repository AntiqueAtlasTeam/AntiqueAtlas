package hunternif.mc.impl.atlas.core.scaning;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.ITileStorage;
import hunternif.mc.impl.atlas.core.TileInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

import java.util.*;

public class WorldScanner {

    /**
     * Maps dimension ID to biomeAnalyzer.
     */
    private final Map<ResourceKey<Level>, ITileDetector> biomeAnalyzers = new HashMap<>();
    private final TileDetectorBase tileDetectorOverworld = new TileDetectorBase();

    public WorldScanner() {
        setBiomeDetectorForWorld(Level.OVERWORLD, tileDetectorOverworld);
        setBiomeDetectorForWorld(Level.NETHER, new TileDetectorNether());
        setBiomeDetectorForWorld(Level.END, new TileDetectorEnd());
    }


    /**
     * If not found, returns the analyzer for overworld.
     */
    private ITileDetector getBiomeDetectorForWorld(ResourceKey<Level> world) {
        ITileDetector biomeAnalyzer = biomeAnalyzers.get(world);

        return biomeAnalyzer == null ? tileDetectorOverworld : biomeAnalyzer;
    }

    private void setBiomeDetectorForWorld(ResourceKey<Level> world, ITileDetector biomeAnalyzer) {
        biomeAnalyzers.put(world, biomeAnalyzer);
    }

    /**
     * Updates map data around player
     *
     * @return A set of the new tiles, mostly so the server can sync those with relevant clients.
     */
    public Collection<TileInfo> updateAtlasAroundPlayer(AtlasData data, Player player) {
        // Update the actual map only so often:
        int newScanInterval = Math.round(AntiqueAtlasMod.CONFIG.newScanInterval * 20);

        if (player.getCommandSenderWorld().getGameTime() % newScanInterval != 0) {
            return Collections.emptyList(); //no new tiles
        }

        ArrayList<TileInfo> updatedTiles = new ArrayList<>();

        int rescanInterval = newScanInterval * AntiqueAtlasMod.CONFIG.rescanRate;
        boolean rescanRequired = AntiqueAtlasMod.CONFIG.doRescan && player.getCommandSenderWorld().getGameTime() % rescanInterval == 0;

        ITileDetector biomeDetector = getBiomeDetectorForWorld(player.getCommandSenderWorld().dimension());

        int scanRadius = biomeDetector.getScanRadius();

        // Look at chunks around in a circular area:
        for (int dx = -scanRadius; dx <= scanRadius; dx++) {
            for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                if (dx * dx + dz * dz > scanRadius * scanRadius) {
                    continue; // Outside the circle
                }

                int chunkX = player.chunkPosition().x + dx;
                int chunkZ = player.chunkPosition().z + dz;

                TileInfo update = updateAtlasForChunk(data, player.getCommandSenderWorld(), chunkX, chunkZ, rescanRequired);
                if (update != null) {
                    updatedTiles.add(update);
                }
            }
        }
        return updatedTiles;
    }

    private TileInfo updateAtlasForChunk(AtlasData data, Level world, int x, int z, boolean rescanRequired) {
        ITileStorage storedData = data.getWorldData(world.dimension());
        ResourceLocation oldTile = storedData.getTile(x, z);

        // Check if there's a custom tile at the location:
        // Custom tiles overwrite even the chunks already seen.
        ResourceLocation tile = AtlasAPI.getTileAPI().getGlobalTile(world, x, z);

        // If there's no custom tile, check the actual chunk:
        if (tile == null) {
            // If the chunk has been scanned previously, only re-scan it so often:
            if (oldTile != null && !rescanRequired) {
                return null;
            }

            // TODO FABRIC: forceChunkLoading crashes here
            ChunkAccess chunk = world.getChunk(x, z, ChunkStatus.FULL, AntiqueAtlasMod.CONFIG.forceChunkLoading);

            // Skip chunk if it hasn't loaded yet:
            if (chunk == null) {
                return null;
            }

            ITileDetector biomeDetector = getBiomeDetectorForWorld(world.dimension());
            tile = biomeDetector.getBiomeID(world, chunk);

            if (oldTile != null) {
                if (tile == null) {
                    // If the new tile is empty, remove the old one:
                    data.removeTile(world.dimension(), x, z);
                } else if (!oldTile.equals(tile)) {
                    // Only update if the old tile's biome ID doesn't match the new one:
                    data.setTile(world.dimension(), x, z, tile);
                    return new TileInfo(x, z, tile);
                }
            } else {
                // Scanning new chunk:
                if (tile != null) {
                    data.setTile(world.dimension(), x, z, tile);
                    return new TileInfo(x, z, tile);
                }
            }
        } else {
            // Only update the custom tile if it doesn't rewrite itself:
            if (oldTile == null || !oldTile.equals(tile)) {
                data.setTile(world.dimension(), x, z, tile);
                data.setDirty();
                return new TileInfo(x, z, tile);
            }
        }

        return null;
    }
}
