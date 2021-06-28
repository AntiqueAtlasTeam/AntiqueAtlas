package hunternif.mc.impl.atlas.core;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

public class GlobalAtlasData extends PersistentState {
    public static final String TAG_NEXT_ID = "aaNextID";
    private int nextId = 1;

    public GlobalAtlasData() {
    }

    public int getNextAtlasId() {
        int id = nextId++;
        markDirty();
        return id;
    }

    public static GlobalAtlasData readNbt(NbtCompound compound) {
        GlobalAtlasData data = new GlobalAtlasData();
        if (compound.contains(TAG_NEXT_ID, NbtType.NUMBER)) {
            data.nextId = compound.getInt(TAG_NEXT_ID);
        } else {
            data.nextId = 1;
        }
        return data;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound var1) {
        var1.putInt(TAG_NEXT_ID, nextId);
        return var1;
    }
}
