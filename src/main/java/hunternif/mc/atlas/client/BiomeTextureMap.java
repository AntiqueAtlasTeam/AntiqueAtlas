package hunternif.mc.atlas.client;

import hunternif.mc.atlas.core.TileKind;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.SaveData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static hunternif.mc.atlas.client.TextureSet.*;

/**
 * Maps biome IDs (or pseudo IDs) to textures. <i>Not thread-safe!</i>
 * <p>If several textures are set for one ID, one will be chosen at random when
 * putting tile into Atlas.</p>
 * @author Hunternif
 */
@OnlyIn(Dist.CLIENT)
public class BiomeTextureMap extends SaveData {
	private static final BiomeTextureMap INSTANCE = new BiomeTextureMap();
	public static BiomeTextureMap instance() {
		return INSTANCE;
	}

	/** This map stores biome texture mappings. */
	final Map<Biome, TextureSet> biomeTextureMap = new HashMap<>();
	/** This map stores the pseudo biome texture mappings, any biome with ID <0 is assumed to be a pseudo biome */
	final Map<Integer, TextureSet> pseudoBiomeTextureMap = new HashMap<>();

	public static final TextureSet defaultTexture = PLAINS;

	/** Assign texture set to biome. */
	public void setTexture(Biome biome, TextureSet textureSet) {
		if (textureSet == null) {
			if (biomeTextureMap.remove(biome) != null) {
				Log.warn("Removing old texture for biome %s", Registry.BIOME.getId(biome));
			}
			return;
		}
		Log.info("Register texture set %s for biome %s", textureSet.name, biome.getTranslationKey());
		TextureSet previous = biomeTextureMap.put(biome, textureSet);
		// If the old texture set is equal to the new one (i.e. has equal name
		// and equal texture files), then there's no need to update the config.
		if (previous == null) {
			markDirty();
		} else if (!previous.equals(textureSet)) {
			Log.warn("Overwriting texture set for biome %s", Registry.BIOME.getId(biome));
			markDirty();
		}
	}

	/** Assign texture set to pseudo biome */
	public void setTexture(int pseudoID, TextureSet textureSet) {
		if (textureSet == null) {
			if (pseudoBiomeTextureMap.remove(pseudoID) != null) {
				Log.warn("Removing old texture for pseudo-biome %d", pseudoID);
			}
			return;
		}
		TextureSet previous = pseudoBiomeTextureMap.put(pseudoID, textureSet);
		// If the old texture set is equal to the new one (i.e. has equal name
		// and equal texture files), then there's no need to update the config.
		if (previous == null) {
			markDirty();
		} else if (!previous.equals(textureSet)) {
			Log.warn("Overwriting texture set for pseudo-biome %d", pseudoID);
			markDirty();
		}
	}

	/** Find the most appropriate standard texture set depending on
	 * BiomeDictionary types. */
	private void autoRegister(Biome biome) {
		if (biome == null) {
			Log.warn("Biome is null");
			return;
		}

		switch (biome.getCategory()) {
			case SWAMP:
				setTexture(biome, biome.getScale() >= 0.25f ? SWAMP_HILLS : SWAMP);
				break;
			case OCEAN:
			case RIVER:
				setTexture(biome, biome.getPrecipitation() == Biome.RainType.SNOW ? ICE : WATER);
				break;
			case BEACH:
				setTexture(biome, SHORE); // TODO ROCK_SHORE
				break;
			case JUNGLE:
				setTexture(biome, biome.getScale() >= 0.25f ? JUNGLE_HILLS : JUNGLE); // TODO JUNGLE_CLIFFS
				break;
			case SAVANNA:
				setTexture(biome, biome.getDepth() >= 1.0f ? PLATEAU_SAVANNA : SAVANNA);
				break;
			case MESA:
				setTexture(biome, PLATEAU_MESA); // TODO PLATEAU_MESA_TREES
				break;
			case FOREST:
				setTexture(biome, biome.getPrecipitation() == Biome.RainType.SNOW ?
						(biome.getScale() >= 0.25f ? SNOW_PINES_HILLS : SNOW_PINES) :
						(biome.getScale() >= 0.25f ? FOREST_HILLS : FOREST)
				);
				break;
			case PLAINS:
				setTexture(biome, biome.getPrecipitation() == Biome.RainType.SNOW ?
						(biome.getScale() >= 0.25f ? SNOW_HILLS : SNOW) :
						(biome.getScale() >= 0.25f ? HILLS : PLAINS)
				);
				break;
			case ICY:
				setTexture(biome,  biome.getScale() >= 0.25f ? MOUNTAINS_SNOW_CAPS : ICE_SPIKES); // TODO also snowy mountains/tundra?
				break;
			case DESERT:
				setTexture(biome, biome.getScale() >= 0.25f ? DESERT_HILLS : DESERT);
				break;
			case TAIGA:
				setTexture(biome, SNOW); // TODO
				break;
			case EXTREME_HILLS:
				setTexture(biome, biome.getScale() >= 0.25f ? MOUNTAINS : HILLS);
				break;
			case THEEND:
				final boolean[] hasPlants = {false};
				Stream.of(biome.getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION)).forEach(
						feature -> {
							if(!feature.isEmpty()) hasPlants[0] = true;
						});
				if(hasPlants[0]) {
					setTexture(biome, END_ISLAND_PLANTS);
				} else {
					setTexture(biome, END_ISLAND);
				}
				break;
			case NONE:
				setTexture(biome, END_VOID);
				break;
			default:
				setTexture(biome, defaultTexture);
				break;
		}

		Set<Type> types = BiomeDictionary.getTypes(biome);
		// 1. Swamp
		if (types.contains(Type.SWAMP)) {
			if (types.contains(Type.HILLS)) {
				setTexture(biome, SWAMP_HILLS);
			} else {
				setTexture(biome, SWAMP);
			}
		}
		// 2. Water
		else if (types.contains(Type.WATER) || types.contains(Type.RIVER)) {
			// Water + trees = swamp
			if (types.contains(Type.FOREST) || types.contains(Type.JUNGLE)) {
				if (types.contains(Type.HILLS)) {
					setTexture(biome, SWAMP_HILLS);
				} else {
					setTexture(biome, SWAMP);
				}
			} else if (types.contains(Type.SNOWY)){
				setTexture(biome, ICE);
			} else {
				setTexture(biome, WATER);
			}
		}
		// 3. Shore
		else if (types.contains(Type.BEACH)){
			if (types.contains(Type.MOUNTAIN)) {
				setTexture(biome, ROCK_SHORE);
			} else {
				setTexture(biome, SHORE);
			}
		}
		// 4. Jungle
		else if (types.contains(Type.JUNGLE)) {
			if (types.contains(Type.MOUNTAIN)) {
				setTexture(biome, JUNGLE_CLIFFS);
			} else if (types.contains(Type.HILLS)) {
				setTexture(biome, JUNGLE_HILLS);
			} else {
				setTexture(biome, TextureSet.JUNGLE);
			}
		}
		// 5. Savanna
		else if (types.contains(Type.SAVANNA)) {
			if (types.contains(Type.MOUNTAIN) || types.contains(Type.HILLS)) {
				setTexture(biome, SAVANNA_CLIFFS);
			} else {
				setTexture(biome, SAVANNA);
			}
		}
		// 6. Pines
		else if (types.contains(Type.CONIFEROUS)) {
			if (types.contains(Type.MOUNTAIN) || types.contains(Type.HILLS)) {
				setTexture(biome, PINES_HILLS);
			} else {
				setTexture(biome, PINES);
			}
		}
		// 7. Mesa - I suspect that by using this type people usually mean "Plateau"
		else if (types.contains(Type.MESA)) {
			if (types.contains(Type.FOREST)) {
				setTexture(biome, PLATEAU_MESA_TREES);
			} else {
				setTexture(biome, PLATEAU_MESA);
			}
		}
		// 8. General forest
		else if (types.contains(Type.FOREST)) {
			// Frozen forest automatically counts as pines:
			if (types.contains(Type.SNOWY)) {
				if (types.contains(Type.HILLS)) {
					setTexture(biome, SNOW_PINES_HILLS);
				} else {
					setTexture(biome, SNOW_PINES);
				}
			} else {
				// Segregate by density:
				if (types.contains(Type.SPARSE)) {
					if (types.contains(Type.HILLS)) {
						setTexture(biome, SPARSE_FOREST_HILLS);
					} else {
						setTexture(biome, SPARSE_FOREST);
					}
				} else if (types.contains(Type.DENSE)) {
					if (types.contains(Type.HILLS)) {
						setTexture(biome, DENSE_FOREST_HILLS);
					} else {
						setTexture(biome, DENSE_FOREST);
					}
				} else {
					if (types.contains(Type.HILLS)) {
						setTexture(biome, FOREST_HILLS);
					} else {
						setTexture(biome, FOREST);
					}
				}
			}
		}
		// 9. Various plains
		else if (types.contains(Type.PLAINS) || types.contains(Type.WASTELAND)) {
			if (types.contains(Type.SNOWY) || types.contains(Type.COLD)) {
				if (types.contains(Type.MOUNTAIN)) {
					setTexture(biome, MOUNTAINS_SNOW_CAPS);
				} else if (types.contains(Type.HILLS)) {
					setTexture(biome, SNOW_HILLS);
				} else {
					setTexture(biome, SNOW);
				}
			} else if (types.contains(Type.HOT)) {
				if (types.contains(Type.HILLS) || types.contains(Type.MOUNTAIN)) {
					setTexture(biome, DESERT_HILLS);
				} else {
					setTexture(biome, DESERT);
				}
			}
			else {
				if (types.contains(Type.HILLS) || types.contains(Type.MOUNTAIN)) {
					setTexture(biome, HILLS);
				} else {
					setTexture(biome, PLAINS);
				}
			}
		}
		// 10. General mountains
		else if (types.contains(Type.MOUNTAIN)) {
			setTexture(biome, MOUNTAINS_NAKED);
		}
		// 11. General hills
		else if (types.contains(Type.HILLS)) {
			if (types.contains(Type.SNOWY) || types.contains(Type.COLD)) {
				setTexture(biome, SNOW_HILLS);
			} else if (types.contains(Type.SANDY)) {
				setTexture(biome, DESERT_HILLS);
			} else {
				setTexture(biome, HILLS);
			}
		} else {
			setTexture(biome, defaultTexture);
		}

		Log.info("Auto-registered standard texture set for biome %s", Registry.BIOME.getKey(biome).toString());
	}

	/** Auto-registers the biome if it is not registered. */
	public void checkRegistration(Biome biome) {
		if (!isRegistered(biome)) {
			autoRegister(biome);
			markDirty();
		}
	}

	/** Checks for pseudo biome ID - if not registered, use default */
	private void checkRegistration(int pseudoID) {
		if (!isRegistered(pseudoID)) {
			setTexture(pseudoID, defaultTexture);
		}
	}

	public boolean isRegistered(Biome biome) {
		return biomeTextureMap.containsKey(biome);
	}

	public boolean isRegistered(int pseudoID) {
		return pseudoBiomeTextureMap.containsKey(pseudoID);
	}

	/** If unknown biome, auto-registers a texture set. If null, returns default set. */
	public TextureSet getTextureSet(TileKind tile) {
		if (tile == null) return defaultTexture;
		Biome biome = tile.getBiome();
		if (biome != null) {
			checkRegistration(biome);
			return biomeTextureMap.getOrDefault(biome, defaultTexture);
		}
		else {
			checkRegistration(tile.getId());
			return pseudoBiomeTextureMap.get(tile.getId());
		}
	}

	public ResourceLocation getTexture(int variationNumber, TileKind tile) {
		TextureSet set = getTextureSet(tile);
		return set.textures[variationNumber % set.textures.length];
	}

	public List<ResourceLocation> getAllTextures() {
		List<ResourceLocation> list = new ArrayList<>(biomeTextureMap.size());
		for (Entry<Biome, TextureSet> entry : biomeTextureMap.entrySet()) {
			list.addAll(Arrays.asList(entry.getValue().textures));
		}
		for (Entry<Integer, TextureSet> entry : pseudoBiomeTextureMap.entrySet()) {
			list.addAll(Arrays.asList(entry.getValue().textures));
		}
		return list;
	}
}
