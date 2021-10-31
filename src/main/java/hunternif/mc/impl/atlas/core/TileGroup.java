package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.Rect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a group of tiles that may be sent/stored as a single NBT
 */
public class TileGroup implements ITileStorage {

    public static final String TAG_POSITION = "p";
    public static final String TAG_TILES = "t";

    /**
     * The width/height of this TileGroup
     */
    public static final int CHUNK_STEP = 16;

    /**
     * The area of chunks this group covers
     */
    Rect scope = new Rect(0, 0, CHUNK_STEP, CHUNK_STEP);

    /**
     * The tiles in this scope
     */
    ResourceLocation[][] tiles = new ResourceLocation[CHUNK_STEP][CHUNK_STEP];

    public TileGroup(int x, int y) {
        scope.minX = x;
        scope.minY = y;
        scope.maxX = scope.minX + CHUNK_STEP - 1;
        scope.maxY = scope.minY + CHUNK_STEP - 1;
    }

    public TileGroup() {

    }

    public TileGroup readFromNBT(CompoundTag compound) {
        this.scope.minX = compound.getIntArray(TAG_POSITION)[0];
        this.scope.minY = compound.getIntArray(TAG_POSITION)[1];
        this.scope.maxX = this.scope.minX + CHUNK_STEP - 1;
        this.scope.maxY = this.scope.minY + CHUNK_STEP - 1;
        ListTag listTag = compound.getList(TAG_TILES, 8);
        for (int y = 0; y < CHUNK_STEP; y++) {
            for (int x = 0; x < CHUNK_STEP; x++) {
                // order:
                // 0 1 2
                // 3 4 5
                // 6 7 8
                String id = listTag.getString(x + y * CHUNK_STEP);
                if (!id.isEmpty()) {
                    tiles[x][y] = ResourceLocation.tryParse(id);
                }
            }
        }

        return this;
    }

    public CompoundTag writeToNBT(CompoundTag compound) {
        int[] pos = {scope.minX, scope.minY};
        ListTag listTag = new ListTag();
        for (int y = 0; y < CHUNK_STEP; y++) {
            for (int x = 0; x < CHUNK_STEP; x++) {
                // order:
                // 0 1 2
                // 3 4 5
                // 6 7 8
                listTag.add(x + y * CHUNK_STEP, StringTag.valueOf(this.tiles[x][y] == null ? "" : this.tiles[x][y].toString()));
            }
        }

        compound.putIntArray(TAG_POSITION, pos);
        compound.put(TAG_TILES, listTag);
        return compound;
    }

    @Override
    public void setTile(int x, int y, ResourceLocation tile) {
        if (x >= scope.minX && y >= scope.minY && x <= scope.maxX && y <= scope.maxY) {
            int rx = x - scope.minX;
            int ry = y - scope.minY;
            tiles[rx][ry] = tile;
        } else {
            Log.warn("TileGroup tried to set tile out of bounds:" +
                    "\n\tbounds:" + scope +
                    "\n\ttarget: x:" + x + ", y:" + y);
        }
    }

    @Override
    public ResourceLocation removeTile(int x, int y) {
        ResourceLocation tmp = getTile(x, y);
        setTile(x, y, null);
        return tmp;
    }

    @Override
    public ResourceLocation getTile(int x, int y) {
        if (x >= scope.minX && y >= scope.minY && x <= scope.maxX && y <= scope.maxY) {
            int rx = x - scope.minX;
            int ry = y - scope.minY;
            return tiles[rx][ry];
        }
        return null;
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
    public boolean equals(Object obj) {
        if (!(obj instanceof TileGroup)) {
            return false;
        }

        TileGroup other = (TileGroup) obj;
        if (!scope.equals(other.scope)) {
            return false;
        }

        ResourceLocation a;
        ResourceLocation b;

        for (int y = 0; y < CHUNK_STEP; y++) {
            for (int x = 0; x < CHUNK_STEP; x++) {

                a = this.tiles[x][y];
                b = other.tiles[x][y];

                if (a == null) {
                    if (b == null) {
                        continue;
                    } else {
                        return false;
                    }
                }

                if (!this.tiles[x][y].equals(other.tiles[x][y])) {
                    return false;
                }
            }
        }

        return true;
    }
}
