package hunternif.mc.atlas.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public interface TileKind {
	int getId();
	@Nullable
	Biome getBiome();
	@Nullable
	ResourceLocation getExtTile();
}
