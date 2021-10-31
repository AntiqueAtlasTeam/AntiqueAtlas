package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.forge.NbtType;
import hunternif.mc.impl.atlas.network.packet.s2c.play.CustomTileInfoS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.Streams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This world-saved data contains all the non-biome tiles in a world.
 * Atlases check with it when updating themselves.
 *
 * @author Hunternif
 */
public class TileDataStorage extends SavedData {
    private static final int VERSION = 3;
    private static final String TAG_VERSION = "aaVersion";
    private static final String TAG_TILE_LIST = "tiles";
    private static final int CHUNK_SIZE = 10000;
    private final Map<ChunkPos, ResourceLocation> tiles = new ConcurrentHashMap<>(2, 0.75f, 2);

    public TileDataStorage() {
    }
    
    public static TileDataStorage readNbt(CompoundTag compound) {
        TileDataStorage data = new TileDataStorage();

        int version = compound.getInt(TAG_VERSION);

        if (version < VERSION) {
            Log.warn("Outdated atlas data format! Was %d but current is %d", version, VERSION);
            return data;
        }

        ListTag tileList = compound.getList(TAG_TILE_LIST, NbtType.COMPOUND);

        tileList.forEach(tag1 -> {
            CompoundTag tile = (CompoundTag) tag1;
            ChunkPos coords = new ChunkPos(tile.getInt("x"), tile.getInt("y"));
            data.tiles.put(coords, ResourceLocation.tryParse(tile.getString("id")));
        });

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putInt(TAG_VERSION, VERSION);

        ListTag tileList = new ListTag();

        for (Entry<ChunkPos, ResourceLocation> entry : tiles.entrySet()) {
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
    public ResourceLocation getTile(int x, int z) {
        return tiles.get(new ChunkPos(x, z));
    }

    /**
     * If setting tile on the server, a packet should be sent to all players.
     */
    public void setTile(int x, int z, ResourceLocation tile) {
        tiles.put(new ChunkPos(x, z), tile);
        setDirty();
    }

    public void removeTile(int x, int z) {
        tiles.remove(new ChunkPos(x, z));
        setDirty();
    }

    /**
     * Send all data to player in several zipped packets.
     */
    public void syncToPlayer(Player player, ResourceKey<Level> world) {
        Streams.chunked(tiles.entrySet().stream(), CHUNK_SIZE)
                .forEach(chunk -> new CustomTileInfoS2CPacket(world, chunk).send((ServerPlayer) player));

        Log.info("Sent custom biome data to player %s", player.createCommandSourceStack().getTextName());
    }
}
