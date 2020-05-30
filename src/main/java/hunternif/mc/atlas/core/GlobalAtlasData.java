package hunternif.mc.atlas.core;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

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
		if (compound.contains(TAG_NEXT_ID, Constants.NBT.TAG_ANY_NUMERIC)) {
			nextId = compound.getInt(TAG_NEXT_ID);
		} else {
			nextId = 1;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt(TAG_NEXT_ID, nextId);
		return compound;
	}
}
