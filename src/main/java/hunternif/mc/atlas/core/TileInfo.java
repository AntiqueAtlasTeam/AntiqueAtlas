package hunternif.mc.atlas.core;

import net.minecraft.world.biome.Biome;

public class TileInfo {
	public final int x, z;
	public final TileKind biome;

	public TileInfo(int x, int z, TileKind biome){
		this.x = x;
		this.z = z;
		this.biome = biome;
	}
}
