package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.StandardTextureSet.*;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.util.SaveData;

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
public class BiomeTextureMap extends SaveData {
	private static final BiomeTextureMap INSTANCE = new BiomeTextureMap();
	public static BiomeTextureMap instance() {
		return INSTANCE;
	}
	
	static class BiomeTextureEntry {
		public final int biomeID;
		public StandardTextureSet textureSet;
		public final List<ResourceLocation> textures;
		public BiomeTextureEntry(int biomeID, ResourceLocation[] textures) {
			this(biomeID, null, textures);
		}
		public BiomeTextureEntry(int biomeID, StandardTextureSet textureSet) {
			this(biomeID, textureSet, textureSet.textures);
		}
		public BiomeTextureEntry(int biomeID, StandardTextureSet textureSet, ResourceLocation[] textures) {
			this.biomeID = biomeID;
			this.textureSet = textureSet;
			this.textures = new ArrayList<ResourceLocation>(textures.length);
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
	
	/** Assigns texture to biome. */
	public void setTexture(int biomeID, StandardTextureSet textureSet) {
		BiomeTextureEntry entry = textureMap.get(biomeID);
		if (entry == null) {
			entry = new BiomeTextureEntry(biomeID, textureSet);
			textureMap.put(biomeID, entry);
			markDirty();
		} else if (entry.textureSet != textureSet) {
			AntiqueAtlasMod.logger.warn("Overwriting texture set for biome " + biomeID);
			entry.textureSet = textureSet;
			entry.textures.clear();
			for (ResourceLocation texture : textureSet.textures) {
				entry.textures.add(texture);
			}
			markDirty();
		}
	}
	
	/** Assigns texture to biome. */
	public void setTexture(int biomeID, ResourceLocation ... textures) {
		BiomeTextureEntry entry = textureMap.get(biomeID);
		if (entry == null) {
			entry = new BiomeTextureEntry(biomeID, textures);
			textureMap.put(biomeID, entry);
			markDirty();
		} else if (!Arrays.equals(textures, entry.textures.toArray())) {
			//TODO: get rid of single textures in favor of texture packs
			AntiqueAtlasMod.logger.warn("Overwriting textures for biome " + biomeID);
			entry.textures.clear();
			for (ResourceLocation texture : textures) {
				entry.textures.add(texture);
			}
			markDirty();
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
			markDirty();
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

	public ResourceLocation getTexture(Tile tile) {
		checkRegistration(tile.biomeID);
		BiomeTextureEntry entry = textureMap.get(tile.biomeID);
		return entry.textures.get(tile.getVariationNumber());
	}
	
	/** Whether the first tile should be stitched to the 2nd
	 * (but the opposite is not always true!) */
	public boolean shouldStitchTo(int biomeID, int toBiomeID) {
		checkRegistration(biomeID);
		checkRegistration(toBiomeID);
		BiomeTextureEntry entry = textureMap.get(biomeID);
		BiomeTextureEntry toEntry = textureMap.get(toBiomeID);
		if (entry.textureSet != null) {
			for (StandardTextureSet texSet : entry.textureSet.stitchTo) {
				if (texSet.equals(toEntry.textureSet)) {
					return true;
				}
			}
			return false;
		} else {
			List<ResourceLocation> list = new ArrayList<ResourceLocation>(textureMap.get(biomeID).textures);
			list.retainAll(textureMap.get(toBiomeID).textures);
			return !list.isEmpty();
		}
	}
	
	public List<ResourceLocation> getAllTextures() {
		List<ResourceLocation> list = new ArrayList<ResourceLocation>(textureMap.size());
		for (Entry<Integer, BiomeTextureEntry> entry : textureMap.entrySet()) {
			list.addAll(entry.getValue().textures);
		}
		return list;
	}
}