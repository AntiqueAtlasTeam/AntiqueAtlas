package hunternif.mc.impl.atlas.core;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to store tiles, which are shared between ALL atlases
 * Also, this data overwrites the result of ITileDetector instances.
 * <p>
 * Use this class for world gen structures and other important but unique tiles.
 */
public class GlobalTileDataHandler {
    private static final String DATA_KEY = "aAtlasTiles";

    private final Map<RegistryKey<World>, TileDataStorage> worldData =
            new ConcurrentHashMap<>(2, 0.75f, 2);

    public void onWorldLoad(MinecraftServer server, ServerWorld world) {
        worldData.put(world.getDimensionKey(), world.getSavedData().getOrCreate(() -> {
            TileDataStorage data = new TileDataStorage(DATA_KEY);
            data.markDirty();
            return data;
        }, DATA_KEY));
    }

    public TileDataStorage getData(World world) {
        return getData(world.getDimensionKey());
    }

    public TileDataStorage getData(RegistryKey<World> world) {
        return worldData.computeIfAbsent(world,
                k -> new TileDataStorage(DATA_KEY));
    }

    public void onPlayerLogin(ServerPlayerEntity player) {
        worldData.forEach((world, tileData) -> tileData.syncToPlayer(player, world));
    }

}