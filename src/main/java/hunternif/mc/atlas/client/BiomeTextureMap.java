package hunternif.mc.atlas.client;

import static hunternif.mc.atlas.client.TextureSet.DENSE_FOREST;
import static hunternif.mc.atlas.client.TextureSet.DENSE_FOREST_HILLS;
import static hunternif.mc.atlas.client.TextureSet.DESERT;
import static hunternif.mc.atlas.client.TextureSet.DESERT_HILLS;
import static hunternif.mc.atlas.client.TextureSet.FOREST;
import static hunternif.mc.atlas.client.TextureSet.FOREST_HILLS;
import static hunternif.mc.atlas.client.TextureSet.HILLS;
import static hunternif.mc.atlas.client.TextureSet.ICE;
import static hunternif.mc.atlas.client.TextureSet.JUNGLE;
import static hunternif.mc.atlas.client.TextureSet.JUNGLE_CLIFFS;
import static hunternif.mc.atlas.client.TextureSet.JUNGLE_HILLS;
import static hunternif.mc.atlas.client.TextureSet.MOUNTAINS_NAKED;
import static hunternif.mc.atlas.client.TextureSet.MOUNTAINS_SNOW_CAPS;
import static hunternif.mc.atlas.client.TextureSet.PINES;
import static hunternif.mc.atlas.client.TextureSet.PINES_HILLS;
import static hunternif.mc.atlas.client.TextureSet.PLAINS;
import static hunternif.mc.atlas.client.TextureSet.PLATEAU_MESA;
import static hunternif.mc.atlas.client.TextureSet.PLATEAU_MESA_TREES;
import static hunternif.mc.atlas.client.TextureSet.ROCK_SHORE;
import static hunternif.mc.atlas.client.TextureSet.SAVANNA;
import static hunternif.mc.atlas.client.TextureSet.SAVANNA_CLIFFS;
import static hunternif.mc.atlas.client.TextureSet.SHORE;
import static hunternif.mc.atlas.client.TextureSet.SNOW;
import static hunternif.mc.atlas.client.TextureSet.SNOW_HILLS;
import static hunternif.mc.atlas.client.TextureSet.SNOW_PINES;
import static hunternif.mc.atlas.client.TextureSet.SNOW_PINES_HILLS;
import static hunternif.mc.atlas.client.TextureSet.SPARSE_FOREST;
import static hunternif.mc.atlas.client.TextureSet.SPARSE_FOREST_HILLS;
import static hunternif.mc.atlas.client.TextureSet.SWAMP;
import static hunternif.mc.atlas.client.TextureSet.SWAMP_HILLS;
import static hunternif.mc.atlas.client.TextureSet.WATER;

import java.util.*;
import java.util.Map.Entry;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.SaveData;

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
	
	/** This map allows keys other than the 256 biome IDs to use for special tiles. */
	final Map<Integer, TextureSet> textureMap = new HashMap<>();
	
	public static final TextureSet defaultTexture = PLAINS;
	
	/** Assign texture set to biome. */
	public void setTexture(int biomeID, TextureSet textureSet) {
		if (textureSet == null) {
			if (textureMap.remove(biomeID) != null) {
				Log.warn("Removing old texture for biome %s", biomeID);
				if (biomeID >= 0 && biomeID < 256) {
					markDirty();
				}
			}
			return;
		}
		TextureSet previous = textureMap.put(biomeID, textureSet);
		if (biomeID >= 0 && biomeID < 256) {
			// The config only concerns itself with biomes 0-256.
			// If the old texture set is equal to the new one (i.e. has equal name
			// and equal texture files), then there's no need to update the config.
			if (previous == null) {
				markDirty();
			} else if (!previous.equals(textureSet)) {
				Log.warn("Overwriting texture set for biome %d", biomeID);
				markDirty();
			}
		}
	}
	
	/** Find the most appropriate standard texture set depending on
	 * BiomeDictionary types. */
	private void autoRegister(int biomeID) {
		if (biomeID < 0 || biomeID >= 256) {
			Log.warn("Biome ID %d is out of range. Auto-registering default texture set", biomeID);
			setTexture(biomeID, defaultTexture);
			return;
		}
		Biome biome = Biome.getBiomeForId(biomeID);
		if (biome == null) {
			Log.warn("Biome ID %d is null. Auto-registering default texture set", biomeID);
			setTexture(biomeID, defaultTexture);
			return;
		}
		Set<Type> types = BiomeDictionary.getTypes(biome);
		// 1. Swamp
		if (types.contains(Type.SWAMP)) {
			if (types.contains(Type.HILLS)) {
				setTexture(biomeID, SWAMP_HILLS);
			} else {
				setTexture(biomeID, SWAMP);
			}
		}
		// 2. Water
		else if (types.contains(Type.WATER) || types.contains(Type.RIVER)) {
			// Water + trees = swamp
			if (types.contains(Type.FOREST) || types.contains(Type.JUNGLE)) {
				if (types.contains(Type.HILLS)) {
					setTexture(biomeID, SWAMP_HILLS);
				} else {
					setTexture(biomeID, SWAMP);
				}
			} else if (types.contains(Type.SNOWY)){
				setTexture(biomeID, ICE);
			} else {
				setTexture(biomeID, WATER);
			}
		}
		// 3. Shore
		else if (types.contains(Type.BEACH)){
			if (types.contains(Type.MOUNTAIN)) {
				setTexture(biomeID, ROCK_SHORE);
			} else {
				setTexture(biomeID, SHORE);
			}
		} 
		// 4. Jungle
		else if (types.contains(Type.JUNGLE)) {
			if (types.contains(Type.MOUNTAIN)) {
				setTexture(biomeID, JUNGLE_CLIFFS);
			} else if (types.contains(Type.HILLS)) {
				setTexture(biomeID, JUNGLE_HILLS);
			} else {
				setTexture(biomeID, JUNGLE);
			}
		}
		// 5. Savanna
		else if (types.contains(Type.SAVANNA)) {
			if (types.contains(Type.MOUNTAIN) || types.contains(Type.HILLS)) {
				setTexture(biomeID, SAVANNA_CLIFFS);
			} else {
				setTexture(biomeID, SAVANNA);
			}
		}
		// 6. Pines
		else if (types.contains(Type.CONIFEROUS)) {
			if (types.contains(Type.MOUNTAIN) || types.contains(Type.HILLS)) {
				setTexture(biomeID, PINES_HILLS);
			} else {
				setTexture(biomeID, PINES);
			}
		}
		// 7. Mesa - I suspect that by using this type people usually mean "Plateau"
		else if (types.contains(Type.MESA)) {
			if (types.contains(Type.FOREST)) {
				setTexture(biomeID, PLATEAU_MESA_TREES);
			} else {
				setTexture(biomeID, PLATEAU_MESA);
			}
		}
		// 8. General forest
		else if (types.contains(Type.FOREST)) {
			// Frozen forest automatically counts as pines:
			if (types.contains(Type.SNOWY)) {
				if (types.contains(Type.HILLS)) {
					setTexture(biomeID, SNOW_PINES_HILLS);
				} else {
					setTexture(biomeID, SNOW_PINES);
				}
			} else {
				// Segregate by density:
				if (types.contains(Type.SPARSE)) {
					if (types.contains(Type.HILLS)) {
						setTexture(biomeID, SPARSE_FOREST_HILLS);
					} else {
						setTexture(biomeID, SPARSE_FOREST);
					}
				} else if (types.contains(Type.DENSE)) {
					if (types.contains(Type.HILLS)) {
						setTexture(biomeID, DENSE_FOREST_HILLS);
					} else {
						setTexture(biomeID, DENSE_FOREST);
					}
				} else {
					if (types.contains(Type.HILLS)) {
						setTexture(biomeID, FOREST_HILLS);
					} else {
						setTexture(biomeID, FOREST);
					}
				}
			}
		}
		// 9. Various plains
		else if (types.contains(Type.PLAINS) || types.contains(Type.WASTELAND)) {
			if (types.contains(Type.SNOWY) || types.contains(Type.COLD)) {
				if (types.contains(Type.MOUNTAIN)) {
					setTexture(biomeID, MOUNTAINS_SNOW_CAPS);
				} else if (types.contains(Type.HILLS)) {
					setTexture(biomeID, SNOW_HILLS);
				} else {
					setTexture(biomeID, SNOW);
				}
			} else {
				if (types.contains(Type.HILLS) || types.contains(Type.MOUNTAIN)) {
					setTexture(biomeID, DESERT_HILLS);
				} else {
					setTexture(biomeID, DESERT);
				}
			}
		}
		// 10. General mountains
		else if (types.contains(Type.MOUNTAIN)) {
			setTexture(biomeID, MOUNTAINS_NAKED);
		}
		// 11. General hills
		else if (types.contains(Type.HILLS)) {
			if (types.contains(Type.SNOWY) || types.contains(Type.COLD)) {
				setTexture(biomeID, SNOW_HILLS);
			} else if (types.contains(Type.SANDY)) {
				setTexture(biomeID, DESERT_HILLS);
			} else {
				setTexture(biomeID, HILLS);
			}
		} else {
			setTexture(biomeID, defaultTexture);
		}
		Log.info("Auto-registered standard texture set for biome %d", biomeID);
	}
	
	/** Auto-registers the biome ID if it is not registered. */
	private void checkRegistration(int biomeID) {
		if (!isRegistered(biomeID)) {
			autoRegister(biomeID);
			markDirty();
		}
	}
	
	public boolean isRegistered(int biomeID) {
		return textureMap.containsKey(biomeID);
	}
	
	/** If unknown biome, auto-registers a texture set. If null, returns default set. */
	public TextureSet getTextureSet(Tile tile) {
		if (tile == null) return defaultTexture;
		checkRegistration(tile.biomeID);
		return textureMap.get(tile.biomeID);
	}

	public ResourceLocation getTexture(Tile tile) {
		TextureSet set = getTextureSet(tile);
		int i = MathHelper.floor((float)(tile.getVariationNumber())
				/ (float)(Short.MAX_VALUE) * (float)(set.textures.length));
		return set.textures[i];
	}
	
	public List<ResourceLocation> getAllTextures() {
		List<ResourceLocation> list = new ArrayList<>(textureMap.size());
		for (Entry<Integer, TextureSet> entry : textureMap.entrySet()) {
			list.addAll(Arrays.asList(entry.getValue().textures));
		}
		return list;
	}
}