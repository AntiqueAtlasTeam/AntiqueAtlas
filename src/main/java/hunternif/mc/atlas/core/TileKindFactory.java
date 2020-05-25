package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

public final class TileKindFactory {
	private static final Map<Biome, TileKind> biomeKinds = new IdentityHashMap<>();
	private static final Int2ObjectMap<TileKind> extKinds = new Int2ObjectOpenHashMap<>();

	private TileKindFactory() {

	}

	public static TileKind get(int id) {
		if (id < 0) {
			return extKinds.get(id);
		} else {
			Biome biome = Registry.BIOME.getByValue(id);
			if (biome != null) {
				return get(biome);
			} else {
				return null;
			}
		}
	}

	public static TileKind get(Biome biome) {
		return biomeKinds.computeIfAbsent(biome, BiomeKind::new);
	}

	public static TileKind get(ResourceLocation extTile) {
		int id = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(extTile);
		return extKinds.computeIfAbsent(id, ExtKind::new);
	}

	private static class ExtKind implements TileKind {
		private final int id;

		ExtKind(int id) {
			this.id = id;
		}

		@Override
		public int getId() {
			return id;
		}

		@Nullable
		@Override
		public Biome getBiome() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getExtTile() {
			return ExtTileIdMap.instance().getPseudoBiomeName(id);
		}

		@Override
		public String toString() {
			return "ExtTile{" + getExtTile() + "}";
		}
	}

	private static class BiomeKind implements TileKind {
		private final Biome biome;

		BiomeKind(Biome biome) {
			this.biome = biome;
		}

		@Override
		public int getId() {
			return Registry.BIOME.getId(biome);
		}

		@Nullable
		@Override
		public net.minecraft.world.biome.Biome getBiome() {
			return biome;
		}

		@Nullable
		@Override
		public ResourceLocation getExtTile() {
			return null;
		}

		@Override
		public String toString() {
			return "BiomeTile{" + Registry.BIOME.getId(biome) + "}";
		}
	}
}
