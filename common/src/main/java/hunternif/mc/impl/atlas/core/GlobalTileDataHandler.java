package hunternif.mc.impl.atlas.core;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

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

    public void onWorldLoad(ServerWorld world) {
        worldData.put(world.getRegistryKey(), world.getPersistentStateManager().getOrCreate(() -> {
            TileDataStorage data = new TileDataStorage(DATA_KEY);
            data.markDirty();
            return data;
        }, DATA_KEY));
    }

    public TileDataStorage getData(World world) {
        return getData(world.getRegistryKey());
    }

    public TileDataStorage getData(RegistryKey<World> world) {
        return worldData.computeIfAbsent(world,
                k -> new TileDataStorage(DATA_KEY));
    }

    public void onPlayerLogin(ServerPlayerEntity player) {
        worldData.forEach((world, tileData) -> tileData.syncToPlayer(player, world));
    }

}
