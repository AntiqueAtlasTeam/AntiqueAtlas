package hunternif.mc.atlas.core;

import net.minecraft.world.biome.Biome;

public class TileInfo {
	public int x, z;
	public TileKind biome;
	
	public TileInfo(){}
	
	public TileInfo(int x, int z, TileKind biome){
		this.x = x;
		this.z = z;
		this.biome = biome;
	}
}
