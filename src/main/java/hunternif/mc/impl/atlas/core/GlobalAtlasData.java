package hunternif.mc.impl.atlas.core;

import hunternif.mc.impl.atlas.forge.NbtType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class GlobalAtlasData extends WorldSavedData {
	public static final String TAG_NEXT_ID = "aaNextID";
	private int nextId = 1;

	public GlobalAtlasData(String key) {
		super(key);
	}

	public int getNextAtlasId() {
		int id = nextId++;
		markDirty();
		return id;
	}

	@Override
	public void read(CompoundNBT compound) {
		if (compound.contains(TAG_NEXT_ID, NbtType.IntNBT)) {
			nextId = compound.getInt(TAG_NEXT_ID);
		} else {
			nextId = 1;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT var1) {
		var1.putInt(TAG_NEXT_ID, nextId);
		return var1;
	}
}
