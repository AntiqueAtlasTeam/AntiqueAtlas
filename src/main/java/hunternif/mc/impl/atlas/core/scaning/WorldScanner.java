package hunternif.mc.impl.atlas.core.scaning;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.core.AtlasData;
import hunternif.mc.impl.atlas.core.ITileStorage;
import hunternif.mc.impl.atlas.core.TileInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;

import java.util.*;

public class WorldScanner {

    /**
     * Maps dimension ID to biomeAnalyzer.
     */
    private final Map<RegistryKey<World>, ITileDetector> biomeAnalyzers = new HashMap<>();
    private final TileDetectorBase tileDetectorOverworld = new TileDetectorBase();

    public WorldScanner() {
        setBiomeDetectorForWorld(World.OVERWORLD, tileDetectorOverworld);
        setBiomeDetectorForWorld(World.THE_NETHER, new TileDetectorNether());
        setBiomeDetectorForWorld(World.THE_END, new TileDetectorEnd());
    }


    /**
     * If not found, returns the analyzer for overworld.
     */
    private ITileDetector getBiomeDetectorForWorld(RegistryKey<World> world) {
        ITileDetector biomeAnalyzer = biomeAnalyzers.get(world);

        return biomeAnalyzer == null ? tileDetectorOverworld : biomeAnalyzer;
    }

    private void setBiomeDetectorForWorld(RegistryKey<World> world, ITileDetector biomeAnalyzer) {
        biomeAnalyzers.put(world, biomeAnalyzer);
    }

    /**
     * Updates map data around player
     *
     * @return A set of the new tiles, mostly so the server can sync those with relevant clients.
     */
    public Collection<TileInfo> updateAtlasAroundPlayer(AtlasData data, PlayerEntity player) {
        // Update the actual map only so often:
        int newScanInterval = Math.round(AntiqueAtlasConfig.newScanInterval.get().floatValue() * 20);

        if (player.getEntityWorld().getGameTime() % newScanInterval != 0) {
            return Collections.emptyList(); //no new tiles
        }

        ArrayList<TileInfo> updatedTiles = new ArrayList<>();

        int rescanInterval = newScanInterval * AntiqueAtlasConfig.rescanRate.get();
        boolean rescanRequired = AntiqueAtlasConfig.doRescan.get() && player.getEntityWorld().getGameTime() % rescanInterval == 0;

        ITileDetector biomeDetector = getBiomeDetectorForWorld(player.getEntityWorld().getDimensionKey());

        int scanRadius = biomeDetector.getScanRadius();

        // Look at chunks around in a circular area:
        for (int dx = -scanRadius; dx <= scanRadius; dx++) {
            for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                if (dx * dx + dz * dz > scanRadius * scanRadius) {
                    continue; // Outside the circle
                }

                int chunkX = player.chunkCoordX + dx;
                int chunkZ = player.chunkCoordZ + dz;

                TileInfo update = updateAtlasForChunk(data, player.getEntityWorld(), chunkX, chunkZ, rescanRequired);
                if (update != null) {
                    updatedTiles.add(update);
                }
            }
        }
        return updatedTiles;
    }

    private TileInfo updateAtlasForChunk(AtlasData data, World world, int x, int z, boolean rescanRequired) {
        ITileStorage storedData = data.getWorldData(world.getDimensionKey());
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
            IChunk chunk = world.getChunk(x, z, ChunkStatus.FULL, AntiqueAtlasConfig.forceChunkLoading.get());

            // Skip chunk if it hasn't loaded yet:
            if (chunk == null) {
                return null;
            }

            ITileDetector biomeDetector = getBiomeDetectorForWorld(world.getDimensionKey());
            tile = biomeDetector.getBiomeID(world, chunk);

            if (oldTile != null) {
                if (tile == null) {
                    // If the new tile is empty, remove the old one:
                    data.removeTile(world.getDimensionKey(), x, z);
                } else if (!oldTile.equals(tile)) {
                    // Only update if the old tile's biome ID doesn't match the new one:
                    data.setTile(world.getDimensionKey(), x, z, tile);
                    return new TileInfo(x, z, tile);
                }
            } else {
                // Scanning new chunk:
                if (tile != null) {
                    data.setTile(world.getDimensionKey(), x, z, tile);
                    return new TileInfo(x, z, tile);
                }
            }
        } else {
            // Only update the custom tile if it doesn't rewrite itself:
            if (oldTile == null || !oldTile.equals(tile)) {
                data.setTile(world.getDimensionKey(), x, z, tile);
                data.markDirty();
                return new TileInfo(x, z, tile);
            }
        }

        return null;
    }
}