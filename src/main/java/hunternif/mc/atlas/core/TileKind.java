package hunternif.mc.atlas.core;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public interface TileKind {
	int getId();
	@Nullable
	Biome getBiome();
	@Nullable
	String getExtTile();

	default short getVariationNumber(int x, int y) {
		return (short) (MathHelper.hashCode(x, y, 0) & 32767);
	}
}
