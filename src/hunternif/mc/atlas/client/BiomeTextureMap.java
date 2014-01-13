package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.MapTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public enum BiomeTextureMap {
	INSTANCE;
	public static BiomeTextureMap instance() {
		return INSTANCE;
	}
	
	protected static class BiomeTextureEntry {
		public final int biomeID;
		public StandardTextureSet textureSet;
		public final List<ResourceLocation> textures;
		public BiomeTextureEntry(int biomeID, ResourceLocation ... textures) {
			this(biomeID, null, textures);
		}
		public BiomeTextureEntry(int biomeID, StandardTextureSet textureSet) {
			this(biomeID, textureSet, textureSet.textures);
		}
		public BiomeTextureEntry(int biomeID, StandardTextureSet textureSet, ResourceLocation ... textures) {
			this.biomeID = biomeID;
			this.textureSet = textureSet;
			this.textures = new ArrayList<ResourceLocation>();
			for (ResourceLocation texture : textures) {
				this.textures.add(texture);
			}
		}
		public boolean isStandardSet() {
			return textureSet != null;
		}
	}
	
	/** This map allows keys other than the 256 biome IDs to use for special tiles. */
	protected final Map<Integer, BiomeTextureEntry> textureMap =
			new ConcurrentHashMap<Integer, BiomeTextureMap.BiomeTextureEntry>();
	
	public static final StandardTextureSet defaultTexture = PLAINS;

	/** Assign default textures to vanilla biomes. Returns true if any texture
	 * was changed. */
	public boolean assignVanillaTextures() {
		boolean changed = false;
		changed |= addTextureIfNone(ocean,			WATER);
		changed |= addTextureIfNone(frozenOcean,	WATER);
		changed |= addTextureIfNone(river,			WATER);
		changed |= addTextureIfNone(frozenRiver,	WATER);
		changed |= addTextureIfNone(beach,			BEACH);
		changed |= addTextureIfNone(desert,		SAND);
		changed |= addTextureIfNone(plains,		PLAINS);
		//changed |= addTextureIfNone(icePlains,	PLAINS);
		changed |= addTextureIfNone(jungleHills,	JUNGLE_HILLS);
		changed |= addTextureIfNone(forestHills,	FOREST_HILLS);
		changed |= addTextureIfNone(desertHills,	HILLS);
		changed |= addTextureIfNone(extremeHills,	MOUNTAINS);
		changed |= addTextureIfNone(extremeHillsEdge, MOUNTAINS);
		changed |= addTextureIfNone(iceMountains,	MOUNTAINS);
		changed |= addTextureIfNone(forest,		FOREST);
		changed |= addTextureIfNone(jungle,		JUNGLE);
		changed |= addTextureIfNone(taiga,			PINES);
		changed |= addTextureIfNone(taigaHills,	PINES_HILLS);
		changed |= addTextureIfNone(swampland,		SWAMP);
		changed |= addTextureIfNone(sky,			BEACH);
		//changed |= addTextureIfNone(hell,		NETHER);
		changed |= addTextureIfNone(mushroomIsland, MUSHROOM);
		changed |= addTextureIfNone(mushroomIslandShore, BEACH);
		return changed;
	}

	/** Assigns texture to biome, if this biome has no texture assigned.
	 * Returns true if a new texture was assigned. */
	public boolean addTextureIfNone(BiomeGenBase biome, StandardTextureSet textureSet) {
		if (!isRegistered(biome.biomeID)) {
			addTexture(biome.biomeID, textureSet);
			return true;
		}
		return false;
	}
	public void addTextureIfNone(BiomeGenBase biome, ResourceLocation ... textures) {
		if (!isRegistered(biome.biomeID)) {
			addTexture(biome.biomeID, textures);
		}
	}
	public void addTexture(int biomeID, StandardTextureSet textureSet) {
		BiomeTextureEntry entry = textureMap.get(biomeID);
		if (entry == null) {
			entry = new BiomeTextureEntry(biomeID, textureSet);
			textureMap.put(biomeID, entry);
		} else {
			if (!entry.textureSet.equals(textureSet)) {
				// Adding textures from multiple sets breaks the "standard-ness"
				entry.textureSet = null;
			}
			for (ResourceLocation texture : textureSet.textures) {
				entry.textures.add(texture);
			}
		}
	}
	public void addTexture(int biomeID, ResourceLocation ... textures) {
		addTexture(biomeID, false, textures);
	}
	public void addTexture(int biomeID, boolean isStandard, ResourceLocation ... textures) {
		BiomeTextureEntry entry = textureMap.get(biomeID);
		if (entry == null) {
			entry = new BiomeTextureEntry(biomeID, textures);
			textureMap.put(biomeID, entry);
		} else {
			for (ResourceLocation texture : textures) {
				entry.textures.add(texture);
			}
		}
	}
	
	public void autoRegister(int biomeID) {
		if (biomeID < 0 || biomeID >= 256) {
			addTexture(biomeID, defaultTexture);
			return;
		}
		BiomeGenBase biome = biomeList[biomeID];
		List<Type> types = Arrays.asList(BiomeDictionary.getTypesForBiome(biome));
		if (types.contains(Type.WATER)) {
			addTexture(biomeID, WATER);
		} else if (types.contains(Type.HILLS)) {
			if (types.contains(Type.FOREST)) {
				addTexture(biomeID, FOREST_HILLS);
			} else if (types.contains(Type.JUNGLE)) {
				addTexture(biomeID, JUNGLE_HILLS);
			} else {
				addTexture(biomeID, HILLS);
			}
		} else if (types.contains(Type.JUNGLE)) {
			addTexture(biomeID, JUNGLE);
		} else if (types.contains(Type.FOREST)) {
			addTexture(biomeID, FOREST);
		} else if (types.contains(Type.MOUNTAIN)) {
			addTexture(biomeID, MOUNTAINS);
		} else if (types.contains(Type.DESERT) || types.contains(Type.WASTELAND)) {
			addTexture(biomeID, SAND);
		} else {
			addTexture(biomeID, defaultTexture);
		}
		AntiqueAtlasMod.logger.info("Auto-registered standard texture set for biome " + biomeID);
	}
	
	public void checkRegistration(int biomeID) {
		if (!isRegistered(biomeID)) {
			autoRegister(biomeID);
			AntiqueAtlasMod.proxy.updateConfig();
		}
	}
	
	public boolean isRegistered(int biomeID) {
		return textureMap.containsKey(biomeID);
	}

	public int getVariations(int biomeID) {
		checkRegistration(biomeID);
		BiomeTextureEntry entry = textureMap.get(biomeID);
		return entry.textures.size();
	}

	public ResourceLocation getTexture(MapTile tile) {
		checkRegistration(tile.biomeID);
		BiomeTextureEntry entry = textureMap.get(tile.biomeID);
		return entry.textures.get(tile.variationNumber);
	}
	
	public boolean haveSameTexture(int ... biomeIDs) {
		List<ResourceLocation> textures = null;
		for (int biomeID : biomeIDs) {
			checkRegistration(biomeID);
			if (textures == null) {
				textureMap.get(biomeID);
				textures = new ArrayList<ResourceLocation>(textureMap.get(biomeID).textures);
			} else {
				textures.retainAll(textureMap.get(biomeID).textures);
			}
		}
		return !textures.isEmpty();
	}
	
	public static boolean areBiomesEqual(BiomeGenBase ... biomes) {
		for (int i = 1; i < biomes.length; i++) {
			if (biomes[0] != biomes[i]) {
				return false;
			}
		}
		return true;
	}
	
	public List<ResourceLocation> getAllTextures() {
		List<ResourceLocation> list = new ArrayList<ResourceLocation>();
		for (Entry<Integer, BiomeTextureEntry> entry : textureMap.entrySet()) {
			list.addAll(entry.getValue().textures);
		}
		return list;
	}
}