package hunternif.mc.atlas.core;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.StandardTextureSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Maps biome IDs (or pseudo IDs) to textures. <i>Not thread-safe!</i>
 * <p>If several textures are set for one ID, one will be chosen at random when
 * putting tile into Atlas.</p> 
 * @author Hunternif
 */
@SideOnly(Side.CLIENT)
public enum BiomeTextureMap {
	INSTANCE;
	public static BiomeTextureMap instance() {
		return INSTANCE;
	}
	
	static class BiomeTextureEntry {
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
		@Override
		public String toString() {
			if (isStandardSet()) {
				return textureSet.name();
			} else {
				return textures.toString();
			}
		}
	}
	
	/** This map allows keys other than the 256 biome IDs to use for special tiles. */
	final Map<Integer, BiomeTextureEntry> textureMap =
			new HashMap<Integer, BiomeTextureMap.BiomeTextureEntry>();
	
	public static final StandardTextureSet defaultTexture = PLAINS;
	
	/** Assigns texture to biome, if this biome has no texture assigned.
	 * Returns true if a new texture was assigned. */
	public boolean setTextureIfNone(int biomeID, StandardTextureSet textureSet) {
		if (!isRegistered(biomeID)) {
			setTexture(biomeID, textureSet);
			return true;
		}
		return false;
	}
	/** Assigns texture to biome, if this biome has no texture assigned.
	 * Returns true if a new texture was assigned. */
	public boolean setTextureIfNone(int biomeID, ResourceLocation ... textures) {
		if (!isRegistered(biomeID)) {
			setTexture(biomeID, textures);
			return true;
		}
		return false;
	}
	/** Assigns texture to biome. */
	public void setTexture(int biomeID, StandardTextureSet textureSet) {
		BiomeTextureEntry entry = textureMap.get(biomeID);
		if (entry == null) {
			entry = new BiomeTextureEntry(biomeID, textureSet);
			textureMap.put(biomeID, entry);
		} else {
			if (!entry.textureSet.equals(textureSet)) {
				// Adding textures from multiple sets breaks the "standard-ness" of a set
				entry.textureSet = null;
			}
			if (!entry.textures.isEmpty()) {
				AntiqueAtlasMod.logger.warn("Overwriting textures for biome " + biomeID);
			}
			entry.textures.clear();
			for (ResourceLocation texture : textureSet.textures) {
				entry.textures.add(texture);
			}
		}
	}
	/** Assigns texture to biome. */
	public void setTexture(int biomeID, ResourceLocation ... textures) {
		BiomeTextureEntry entry = textureMap.get(biomeID);
		if (entry == null) {
			entry = new BiomeTextureEntry(biomeID, textures);
			textureMap.put(biomeID, entry);
		} else {
			if (!entry.textures.isEmpty()) {
				AntiqueAtlasMod.logger.warn("Overwriting textures for biome " + biomeID);
			}
			entry.textures.clear();
			for (ResourceLocation texture : textures) {
				entry.textures.add(texture);
			}
		}
	}
	
	/** Find the most appropriate standard texture set depending on
	 * BiomeDictionary types. */
	private void autoRegister(int biomeID) {
		if (biomeID < 0 || biomeID >= 256) {
			AntiqueAtlasMod.logger.warn("Biome ID " + biomeID + " is out of range. "
					+ "Auto-registering default texture set");
			setTexture(biomeID, defaultTexture);
			return;
		}
		BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);
		if (biome == null) {
			AntiqueAtlasMod.logger.warn("Biome ID " + biomeID + " is null. "
					+ "Auto-registering default texture set");
			setTexture(biomeID, defaultTexture);
			return;
		}
		List<Type> types = Arrays.asList(BiomeDictionary.getTypesForBiome(biome));
		if (types.contains(Type.SWAMP)) {
			setTexture(biomeID, SWAMP);
		} else if (types.contains(Type.WATER)) {
			// Water + trees = swamp
			if (types.contains(Type.FOREST) || types.contains(Type.JUNGLE) || types.contains(Type.SWAMP)) {
				setTexture(biomeID, SWAMP);
			} else if (types.contains(Type.SNOWY)){
				setTexture(biomeID, FROZEN_WATER);
			} else {
				setTexture(biomeID, WATER);
			}
		} else if (types.contains(Type.MOUNTAIN)) {
			setTexture(biomeID, MOUNTAINS);
		} else if (types.contains(Type.HILLS)) {
			if (types.contains(Type.FOREST)) {
				// Frozen forest automatically counts as pines:
				if (types.contains(Type.SNOWY)) {
					setTexture(biomeID, PINES_HILLS);
				} else {
					setTexture(biomeID, FOREST_HILLS);
				}
			} else if (types.contains(Type.JUNGLE)) {
				setTexture(biomeID, JUNGLE_HILLS);
			} else {
				setTexture(biomeID, HILLS);
			}
		} else if (types.contains(Type.JUNGLE)) {
			setTexture(biomeID, JUNGLE);
		} else if (types.contains(Type.FOREST)) {
			// Frozen forest automatically counts as pines:
			if (types.contains(Type.SNOWY)) {
				setTexture(biomeID, PINES);
			} else {
				setTexture(biomeID, FOREST);
			}
		} else if (types.contains(Type.SANDY) || types.contains(Type.WASTELAND)) {
			if (types.contains(Type.SNOWY)) {
				setTexture(biomeID, SNOW);
			} else {
				setTexture(biomeID, SAND);
			}
		} else if (types.contains(Type.BEACH)){
			setTexture(biomeID, BEACH);
		} else {
			setTexture(biomeID, defaultTexture);
		}
		AntiqueAtlasMod.logger.info("Auto-registered standard texture set for biome " + biomeID);
	}
	
	/** Auto-registers the biome ID if it is not registered. */
	public void checkRegistration(int biomeID) {
		if (!isRegistered(biomeID)) {
			autoRegister(biomeID);
			AntiqueAtlasMod.proxy.updateBiomeTextureConfig();
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
		return entry.textures.get(tile.getVariationNumber());
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