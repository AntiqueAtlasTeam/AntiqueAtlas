package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import static net.minecraft.world.biome.BiomeGenBase.*;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.MapTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		public final List<String> textures;
		public BiomeTextureEntry(int biomeID, String ... textures) {
			this(biomeID, null, textures);
		}
		public BiomeTextureEntry(int biomeID, StandardTextureSet textureSet) {
			this(biomeID, textureSet, textureSet.textures);
		}
		public BiomeTextureEntry(int biomeID, StandardTextureSet textureSet, String ... textures) {
			this.biomeID = biomeID;
			this.textureSet = textureSet;
			this.textures = new ArrayList<String>();
			for (String texture : textures) {
				this.textures.add(texture);
			}
		}
		public boolean isStandardSet() {
			return textureSet != null;
		}
	}
	
	/** This map allows keys other than the 256 biome IDs to use for special tiles. */
	protected final Map<Integer, BiomeTextureEntry> textureMap =
			new HashMap<Integer, BiomeTextureMap.BiomeTextureEntry>();
	
	public static final StandardTextureSet defaultTexture = PLAINS;

	public void assignVanillaTextures() {
		addTextureIfNone(ocean,			WATER);
		addTextureIfNone(frozenOcean,	WATER);
		addTextureIfNone(river,			WATER);
		addTextureIfNone(frozenRiver,	WATER);
		addTextureIfNone(beach,			BEACH);
		addTextureIfNone(desert,		SAND);
		addTextureIfNone(plains,		PLAINS);
		//addTextureIfNone(icePlains,	PLAINS);
		addTextureIfNone(jungleHills,	JUNGLE_HILLS);
		addTextureIfNone(forestHills,	FOREST_HILLS);
		addTextureIfNone(desertHills,	HILLS);
		addTextureIfNone(extremeHills,	MOUNTAINS);
		addTextureIfNone(extremeHillsEdge, MOUNTAINS);
		addTextureIfNone(iceMountains,	MOUNTAINS);
		addTextureIfNone(forest,		FOREST);
		addTextureIfNone(jungle,		JUNGLE);
		addTextureIfNone(taiga,			PINES);
		addTextureIfNone(taigaHills,	PINES_HILLS);
		addTextureIfNone(swampland,		SWAMP);
		addTextureIfNone(sky,			BEACH);
		//addTextureIfNone(hell,		NETHER);
		addTextureIfNone(mushroomIsland, MUSHROOM);
		addTextureIfNone(mushroomIslandShore, BEACH);
	}

	public void addTextureIfNone(BiomeGenBase biome, StandardTextureSet textureSet) {
		if (!isRegistered(biome.biomeID)) {
			addTexture(biome.biomeID, textureSet);
		}
	}
	public void addTextureIfNone(BiomeGenBase biome, String ... textures) {
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
			for (String texture : textureSet.textures) {
				entry.textures.add(texture);
			}
		}
	}
	public void addTexture(int biomeID, String ... textures) {
		addTexture(biomeID, false, textures);
	}
	public void addTexture(int biomeID, boolean isStandard, String ... textures) {
		BiomeTextureEntry entry = textureMap.get(biomeID);
		if (entry == null) {
			entry = new BiomeTextureEntry(biomeID, textures);
			textureMap.put(biomeID, entry);
		} else {
			for (String texture : textures) {
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

	public String getTexture(MapTile tile) {
		checkRegistration(tile.biomeID);
		BiomeTextureEntry entry = textureMap.get(tile.biomeID);
		return entry.textures.get(tile.variationNumber);
	}
	
	public boolean haveSameTexture(int ... biomeIDs) {
		List<String> textures = null;
		for (int biomeID : biomeIDs) {
			checkRegistration(biomeID);
			if (textures == null) {
				textureMap.get(biomeID);
				textures = new ArrayList<String>(textureMap.get(biomeID).textures);
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
}