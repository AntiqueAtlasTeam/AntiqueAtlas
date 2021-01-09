package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.util.Log;
import hunternif.mc.impl.atlas.util.SaveData;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.Map.Entry;

import static hunternif.mc.impl.atlas.client.TextureSet.*;

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

	/** This map stores the pseudo biome texture mappings, any biome with ID <0 is assumed to be a pseudo biome */
	final Map<ResourceLocation, TextureSet> textureMap = new HashMap<>();

	public static final TextureSet defaultTexture = PLAINS;

	/** Assign texture set to biome. */
	public void setTexture(Biome biome, TextureSet textureSet) {
		this.setTexture(WorldGenRegistries.BIOME.getKey(biome), textureSet);
	}

	/** Assign texture set to pseudo biome */
	public void setTexture(ResourceLocation tileId, TextureSet textureSet) {
		if (textureSet == null) {
			if (textureMap.remove(tileId) != null) {
				Log.warn("Removing old texture for %d", tileId);
			}
			return;
		}
		TextureSet previous = textureMap.put(tileId, textureSet);
		// If the old texture set is equal to the new one (i.e. has equal name
		// and equal texture files), then there's no need to update the config.
		if (previous == null) {
			markDirty();
		} else if (!previous.equals(textureSet)) {
			Log.warn("Overwriting texture set for %d", tileId);
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
				if(biome.getGenerationSettings().getFeatures().size() > 1) {
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

		Log.info("Auto-registered standard texture set for biome %s", WorldGenRegistries.BIOME.getKey(biome).toString());
	}

	/** Auto-registers the biome if it is not registered. */
	public void checkRegistration(Biome biome) {
		if (!isRegistered(biome)) {
			autoRegister(biome);
			markDirty();
		}
	}

	/** Checks for pseudo biome ID - if not registered, use default */
	private void checkRegistration(ResourceLocation id) {
		if (!isRegistered(id)) {
			setTexture(id, defaultTexture);
		}
	}

	public boolean isRegistered(Biome biome) {
		return isRegistered(WorldGenRegistries.BIOME.getKey(biome));
	}

	public boolean isRegistered(ResourceLocation id) {
		return textureMap.containsKey(id);
	}

	/** If unknown biome, auto-registers a texture set. If null, returns default set. */
	public TextureSet getTextureSet(ResourceLocation tile) {
		if (tile == null) {
			return defaultTexture;
		}

		Biome biome = WorldGenRegistries.BIOME.getOrDefault(tile);
		if (biome != null) {
			checkRegistration(biome);
		} else {
			checkRegistration(tile);
		}

		return textureMap.get(tile);
	}

	public ResourceLocation getTexture(int variationNumber, ResourceLocation tile) {
		TextureSet set = getTextureSet(tile);
		return set.textures[variationNumber % set.textures.length];
	}

	public List<ResourceLocation> getAllTextures() {
		List<ResourceLocation> list = new ArrayList<>();

		for (Entry<ResourceLocation, TextureSet> entry : textureMap.entrySet()) {
			list.addAll(Arrays.asList(entry.getValue().textures));
		}

		return list;
	}
}
