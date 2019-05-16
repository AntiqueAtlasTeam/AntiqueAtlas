package hunternif.mc.atlas.core;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public interface TileKind {
	int getId();
	@Nullable
	Biome getBiome();
	@Nullable
	Identifier getExtTile();
}
