package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.forge.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class GlobalAtlasData extends SavedData {
    public static final String TAG_NEXT_ID = "aaNextID";
    private int nextId = 1;

    public GlobalAtlasData() {
    }

    public int getNextAtlasId() {
        int id = nextId++;
        setDirty();
        return id;
    }

    public static GlobalAtlasData readNbt(CompoundTag compound) {
        GlobalAtlasData data = new GlobalAtlasData();
        if (compound.contains(TAG_NEXT_ID, NbtType.NUMBER)) {
            data.nextId = compound.getInt(TAG_NEXT_ID);
        } else {
            data.nextId = 1;
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag var1) {
        var1.putInt(TAG_NEXT_ID, nextId);
        return var1;
    }
}
