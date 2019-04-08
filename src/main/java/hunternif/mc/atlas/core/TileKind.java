package hunternif.mc.atlas.core;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public interface TileKind {
	int getId();
	@Nullable
	Biome getBiome();
	@Nullable
	Identifier getExtTile();

	default int getVariationNumber(int x, int y) {
		return 2;
	}
}
