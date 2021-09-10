package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.network.packet.s2c.play.MapDataS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import me.shedaniel.architectury.utils.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class AtlasData extends PersistentState {
    public static final int VERSION = 4;
    public static final String TAG_VERSION = "aaVersion";
    public static final String TAG_WORLD_MAP_LIST = "qWorldMap";
    public static final String TAG_WORLD_ID = "qWorldID";
    public static final String TAG_VISITED_CHUNKS = "qVisitedChunks";

    // Navigation
    public static final String TAG_BROWSING_X = "qBrowseX";
    public static final String TAG_BROWSING_Y = "qBrowseY";
    public static final String TAG_BROWSING_ZOOM = "qBrowseZoom";

    /**
     * This map contains, for each dimension, a map of chunks the player
     * has seen. This map is thread-safe.
     * CAREFUL! Don't modify chunk coordinates that are already put in the map!
     */
    private final Map<RegistryKey<World>, WorldData> worldMap =
            new ConcurrentHashMap<>(2, 0.75f, 2);

    /**
     * Set of players this Atlas data has been sent to.
     */
    private final Set<PlayerEntity> playersSentTo = new HashSet<>();

    public AtlasData(String key) {
        super(key);
    }

    @Override
    public void fromTag(CompoundTag compound) {
        int version = compound.getInt(TAG_VERSION);
        if (version < VERSION) {
            Log.warn("Outdated atlas data format! Was %d but current is %d.", version, VERSION);
            return;
        }

        ListTag worldMapList = compound.getList(TAG_WORLD_MAP_LIST, NbtType.COMPOUND);
        for (int d = 0; d < worldMapList.size(); d++) {
            CompoundTag worldTag = worldMapList.getCompound(d);
            RegistryKey<World> worldID;
            worldID = RegistryKey.of(Registry.DIMENSION, new Identifier(worldTag.getString(TAG_WORLD_ID)));
            ListTag dimensionTag = (ListTag) worldTag.get(TAG_VISITED_CHUNKS);
            WorldData dimData = getWorldData(worldID);
            dimData.readFromNBT(dimensionTag);
            double zoom = worldTag.getDouble(TAG_BROWSING_ZOOM);
            if (zoom == 0) zoom = 0.5;
            dimData.setBrowsingPosition(worldTag.getInt(TAG_BROWSING_X),
                    worldTag.getInt(TAG_BROWSING_Y), zoom);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compound) {
        return writeToNBT(compound, true);
    }

    public CompoundTag writeToNBT(CompoundTag compound, boolean includeTileData) {
        ListTag dimensionMapList = new ListTag();
        compound.putInt(TAG_VERSION, VERSION);
        for (Entry<RegistryKey<World>, WorldData> dimensionEntry : worldMap.entrySet()) {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString(TAG_WORLD_ID, dimensionEntry.getKey().getValue().toString());
            WorldData dimData = dimensionEntry.getValue();
            if (includeTileData) {
                dimTag.put(TAG_VISITED_CHUNKS, dimData.writeToNBT());
            }
            dimTag.putInt(TAG_BROWSING_X, dimData.getBrowsingX());
            dimTag.putInt(TAG_BROWSING_Y, dimData.getBrowsingY());
            dimTag.putDouble(TAG_BROWSING_ZOOM, dimData.getBrowsingZoom());
            dimensionMapList.add(dimTag);
        }
        compound.put(TAG_WORLD_MAP_LIST, dimensionMapList);

        return compound;
    }

    /**
     * Puts a given tile into given map at specified coordinates and,
     * if tileStitcher is present, sets appropriate sectors on adjacent tiles.
     */
    public void setTile(RegistryKey<World> world, int x, int y, Identifier tile) {
        WorldData worldData = getWorldData(world);
        worldData.setTile(x, y, tile);
    }

    /**
     * Returns the Tile previously set at given coordinates.
     */
    public Identifier removeTile(RegistryKey<World> world, int x, int y) {
        WorldData dimData = getWorldData(world);
        return dimData.removeTile(x, y);
    }

    public Set<RegistryKey<World>> getVisitedWorlds() {
        return worldMap.keySet();
    }

    /* TODO: Packet Rework
     *   Dimension data should check the server for updates*/

    /**
     * If this dimension is not yet visited, empty DimensionData will be created.
     */
    public WorldData getWorldData(RegistryKey<World> world) {
        return worldMap.computeIfAbsent(world, k -> new WorldData(this, world));
    }

    public Map<ChunkPos, Identifier> getSeenChunksInDimension(RegistryKey<World> world) {
        return getWorldData(world).getSeenChunks();
    }

    /**
     * The set of players this AtlasData has already been sent to.
     */
    public Collection<PlayerEntity> getSyncedPlayers() {
        return Collections.unmodifiableCollection(playersSentTo);
    }

    /**
     * Whether this AtlasData has already been sent to the specified player.
     */
    public boolean isSyncedOnPlayer(PlayerEntity player) {
        return playersSentTo.contains(player);
    }

    /**
     * Send all data to the player in several zipped packets. Called once
     * during the first run of ItemAtlas.onUpdate().
     */
    public void syncOnPlayer(int atlasID, PlayerEntity player) {
        CompoundTag data = new CompoundTag();

        // Before syncing make sure the changes are written to the nbt.
        // Do not include dimension tile data.  This will happen later.
        writeToNBT(data, false);
        new MapDataS2CPacket(atlasID, data).send((ServerPlayerEntity) player);

        for (RegistryKey<World> world : worldMap.keySet()) {
            worldMap.get(world).syncOnPlayer(atlasID, player);
        }

        Log.info("Sent Atlas #%d data to player %s", atlasID, player.getCommandSource().getName());
        playersSentTo.add(player);
    }

    public boolean isEmpty() {
        return worldMap.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AtlasData)) return false;
        AtlasData other = (AtlasData) obj;
        // TODO: This doesn't handle disjoint DimensionType keysets of equal size
        if (other.worldMap.size() != worldMap.size()) return false;
        for (RegistryKey<World> key : worldMap.keySet()) {
            if (!worldMap.get(key).equals(other.worldMap.get(key))) return false;
        }
        return true;
    }
}
