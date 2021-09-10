package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.network.packet.s2c.play.CustomTileInfoS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.Streams;
import me.shedaniel.architectury.utils.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This world-saved data contains all the non-biome tiles in a world.
 * Atlases check with it when updating themselves.
 *
 * @author Hunternif
 */
public class TileDataStorage extends PersistentState {
    private static final int VERSION = 3;
    private static final String TAG_VERSION = "aaVersion";
    private static final String TAG_TILE_LIST = "tiles";
    private static final int CHUNK_SIZE = 10000;
    private final Map<ChunkPos, Identifier> tiles = new ConcurrentHashMap<>(2, 0.75f, 2);

    public TileDataStorage(String key) {
        super(key);
    }

    @Override
    public void fromTag(CompoundTag compound) {
        int version = compound.getInt(TAG_VERSION);

        if (version < VERSION) {
            Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
            this.markDirty();
        }

        ListTag tileList = compound.getList(TAG_TILE_LIST, NbtType.COMPOUND);

        tileList.forEach(tag1 -> {
            CompoundTag tile = (CompoundTag) tag1;
            ChunkPos coords = new ChunkPos(tile.getInt("x"), tile.getInt("y"));
            tiles.put(coords, Identifier.tryParse(tile.getString("id")));
        });
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        compound.putInt(TAG_VERSION, VERSION);

        ListTag tileList = new ListTag();

        for (Entry<ChunkPos, Identifier> entry : tiles.entrySet()) {
            CompoundTag tile = new CompoundTag();
            tile.putInt("x", entry.getKey().x);
            tile.putInt("y", entry.getKey().z);
            tile.putString("id", entry.getValue().toString());

            tileList.add(tile);
        }

        compound.put(TAG_TILE_LIST, tileList);

        return compound;
    }

    /**
     * If no custom tile is set at the specified coordinates, returns null.
     */
    public Identifier getTile(int x, int z) {
        return tiles.get(new ChunkPos(x, z));
    }

    /**
     * If setting tile on the server, a packet should be sent to all players.
     */
    public void setTile(int x, int z, Identifier tile) {
        tiles.put(new ChunkPos(x, z), tile);
        markDirty();
    }

    public void removeTile(int x, int z) {
        tiles.remove(new ChunkPos(x, z));
        markDirty();
    }

    /**
     * Send all data to player in several zipped packets.
     */
    public void syncToPlayer(PlayerEntity player, RegistryKey<World> world) {
        Streams.chunked(tiles.entrySet().stream(), CHUNK_SIZE)
                .forEach(chunk -> new CustomTileInfoS2CPacket(world, chunk).send((ServerPlayerEntity) player));

        Log.info("Sent custom biome data to player %s", player.getCommandSource().getName());
    }
}
