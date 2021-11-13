package hunternif.mc.impl.atlas.core;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

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

    private final Map<ResourceKey<Level>, TileDataStorage> worldData =
            new ConcurrentHashMap<>(2, 0.75f, 2);

    public void onWorldLoad(MinecraftServer server, ServerLevel world) {
        worldData.put(world.dimension(), world.getDataStorage().computeIfAbsent(TileDataStorage::readNbt, () -> {
            TileDataStorage data = new TileDataStorage();
            data.setDirty();
            return data;
        }, DATA_KEY));
    }

    public TileDataStorage getData(Level world) {
        return getData(world.dimension());
    }

    public TileDataStorage getData(ResourceKey<Level> world) {
        return worldData.computeIfAbsent(world,
                k -> new TileDataStorage());
    }

    public void onPlayerLogin(ServerPlayer player) {
        worldData.forEach((world, tileData) -> tileData.syncToPlayer(player, world));
    }

}
