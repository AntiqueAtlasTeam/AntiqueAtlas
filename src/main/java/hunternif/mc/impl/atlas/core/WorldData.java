package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.network.packet.s2c.play.TileGroupsS2CPacket;
import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.Rect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * All tiles seen in dimension. Thread-safe (probably)
 */
public class WorldData implements ITileStorage {
    public final AtlasData parent;
    public final RegistryKey<World> world;

    private int browsingX, browsingY;
    private double browsingZoom = 0.5;

    /**
     * a map of chunks the player has seen. This map is thread-safe. CAREFUL!
     * Don't modify chunk coordinates that are already put in the map!
     * <p>
     * Key is a ChunkPos representing the gilegroup's position in units of TileGroup.CHUNK_STEP
     */
    private final Map<ChunkPos, TileGroup> tileGroups = new ConcurrentHashMap<>(2, 0.75f, 2);

    /**
     * Limits of explored area, in chunks.
     */
    private final Rect scope = new Rect();

    public WorldData(AtlasData parent, RegistryKey<World> world) {
        this.parent = parent;
        this.world = world;
    }

    /**
     * This function has to create a new map on each call since the packet rework
     */
    public Map<ChunkPos, Identifier> getSeenChunks() {
        Map<ChunkPos, Identifier> chunks = new ConcurrentHashMap<>(2, 0.75f, 2);
        Identifier t;
        for (Map.Entry<ChunkPos, TileGroup> entry : tileGroups.entrySet()) {
            int basex = entry.getValue().getScope().minX;
            int basey = entry.getValue().getScope().minY;
            for (int x = basex; x < basex + TileGroup.CHUNK_STEP; x++) {
                for (int y = basey; y < basey + TileGroup.CHUNK_STEP; y++) {
                    t = entry.getValue().getTile(x, y);
                    if (t != null) {
                        chunks.put(new ChunkPos(x, y), t);
                    }
                }
            }
        }
        return chunks;
    }

    /**
     * Set world coordinates that are in the center of the GUI.
     */
    public void setBrowsingPosition(int x, int y, double zoom) {
        this.browsingX = x;
        this.browsingY = y;
        this.browsingZoom = zoom;
        if (browsingZoom <= 0) {
            Log.warn("Setting map zoom to invalid value of %f", zoom);
            browsingZoom = AntiqueAtlasMod.CONFIG.minScale;
        }
        parent.markDirty();
    }

    public void setBrowsingPositionTo(Entity e) {
        setBrowsingPosition((int) Math.round(-e.getX() * AntiqueAtlasMod.CONFIG.defaultScale),
                (int) Math.round(-e.getZ() * AntiqueAtlasMod.CONFIG.defaultScale),
                AntiqueAtlasMod.CONFIG.defaultScale);
    }

    public int getBrowsingX() {
        return browsingX;
    }

    public int getBrowsingY() {
        return browsingY;
    }

    public double getBrowsingZoom() {
        return browsingZoom;
    }

    @Override
    public void setTile(int x, int y, Identifier tile) {
        ChunkPos groupPos = new ChunkPos((int) Math.floor(x / (float) TileGroup.CHUNK_STEP),
                (int) Math.floor(y / (float) TileGroup.CHUNK_STEP));
        TileGroup tg = tileGroups.get(groupPos);
        if (tg == null) {
            tg = new TileGroup(groupPos.x * TileGroup.CHUNK_STEP, groupPos.z * TileGroup.CHUNK_STEP);
            tileGroups.put(groupPos, tg);
        }
        tg.setTile(x, y, tile);
        scope.extendTo(x, y);
        parent.markDirty();
    }

    /**
     * Puts a tileGroup into this dimensionData, overwriting any previous stuff.
     */
    public void putTileGroup(TileGroup t) {
        ChunkPos key = new ChunkPos(Math.floorDiv(t.scope.minX, TileGroup.CHUNK_STEP), Math.floorDiv(t.scope.minY, TileGroup.CHUNK_STEP));
        tileGroups.put(key, t);
        extendToTileGroup(t);
    }

    @Override
    public Identifier removeTile(int x, int y) {
        //TODO
        // since scope is not modified, I assume this was never really used
        // Tile oldTile = tileGroups.remove(getKey().set(x, y));
        // if (oldTile != null) parent.markDirty();
        // return oldTile;
        return getTile(x, y);
    }

    @Override
    public Identifier getTile(int x, int y) {
        ChunkPos groupPos = new ChunkPos((int) Math.floor(x / (float) TileGroup.CHUNK_STEP),
                (int) Math.floor(y / (float) TileGroup.CHUNK_STEP));
        TileGroup tg = tileGroups.get(groupPos);
        if (tg == null) {
            return null;
        }
        return tg.getTile(x, y);
    }

    @Override
    public boolean hasTileAt(int x, int y) {
        return getTile(x, y) != null;
    }

    @Override
    public Rect getScope() {
        return scope;
    }

    @Override
    public WorldData clone() {
        //TODO
        WorldData data = new WorldData(this.parent, this.world);
        data.tileGroups.putAll(tileGroups);
        data.scope.set(scope);
        return data;
    }

    public void addData(WorldData other) {
        for (Entry<ChunkPos, TileGroup> e : other.tileGroups.entrySet()) {
            TileGroup group = e.getValue();
            Rect s = group.getScope();
            for (int x = s.minX; x <= s.maxX; x++) {
                for (int y = s.minY; y <= s.maxY; y++) {
                    Identifier tile = group.getTile(x, y);
                    if (tile != null) setTile(x, y, tile);
                }
            }
        }
    }

    public NbtList writeToNBT() {
        NbtList tileGroupList = new NbtList();
        for (Entry<ChunkPos, TileGroup> entry : tileGroups.entrySet()) {
            NbtCompound newbie = new NbtCompound();
            entry.getValue().writeToNBT(newbie);
            tileGroupList.add(newbie);
        }
        return tileGroupList;
    }

    private void extendToTileGroup(TileGroup tg) {
        for (int x = tg.scope.minX; x <= tg.scope.maxX; x++) {
            for (int y = tg.scope.minY; y <= tg.scope.maxY; y++) {
                if (tg.hasTileAt(x, y)) {
                    scope.extendTo(x, y);
                }
            }
        }
    }

    public void readFromNBT(NbtList me) {
        if (me == null) {
            return;
        }
        for (int d = 0; d < me.size(); d++) {
            NbtCompound tgTag = me.getCompound(d);
            TileGroup tg = new TileGroup(0, 0);
            tg.readFromNBT(tgTag);
            putTileGroup(tg);
        }
    }

    public void syncOnPlayer(int atlasID, PlayerEntity player) {
        Log.info("Sending dimension #%s", this.world.toString());
        ArrayList<TileGroup> tileGroups;
        tileGroups = new ArrayList<>(TileGroupsS2CPacket.TILE_GROUPS_PER_PACKET);
        int count = 0;
        int total = 0;
        for (Entry<ChunkPos, TileGroup> t : this.tileGroups.entrySet()) {
            tileGroups.add(t.getValue());
            count++;
            total++;
            if (count >= TileGroupsS2CPacket.TILE_GROUPS_PER_PACKET) {
                new TileGroupsS2CPacket(atlasID, this.world, tileGroups).send((ServerPlayerEntity) player);
                tileGroups.clear();
                count = 0;
            }
        }
        if (count > 0) {
            new TileGroupsS2CPacket(atlasID, this.world, tileGroups).send((ServerPlayerEntity) player);
        }

        Log.info("Sent dimension #%s (%d tiles)", this.world.toString(), total);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WorldData)) return false;
        WorldData other = (WorldData) obj;
        if (other.tileGroups.size() != tileGroups.size()) return false;
        for (ChunkPos entry : tileGroups.keySet()) {
            if (!this.tileGroups.get(entry).equals(other.tileGroups.get(entry))) return false;
        }
        return true;
    }
}
