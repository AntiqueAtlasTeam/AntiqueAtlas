package hunternif.mc.impl.atlas.core;

import net.minecraft.util.ResourceLocation;

public class TileInfo {
	public final int x, z;
	public final ResourceLocation id;

	public TileInfo(int x, int z, ResourceLocation id){
		this.x = x;
		this.z = z;
		this.id = id;
	}
}
