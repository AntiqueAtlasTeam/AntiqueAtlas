package hunternif.mc.atlas.core;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

public class GlobalAtlasData extends PersistentState {
	public static final String TAG_NEXT_ID = "aaNextID";
	private int nextId = 1;

	public GlobalAtlasData(String key) {
		super(key);
	}

	public int getNextAtlasId() {
		return nextId++;
	}

	@Override
	public void fromTag(CompoundTag compound) {
		if (compound.containsKey(TAG_NEXT_ID, NbtType.NUMBER)) {
			nextId = compound.getInt(TAG_NEXT_ID);
		} else {
			nextId = 1;
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag var1) {
		var1.putInt(TAG_NEXT_ID, nextId);
		return var1;
	}
}
