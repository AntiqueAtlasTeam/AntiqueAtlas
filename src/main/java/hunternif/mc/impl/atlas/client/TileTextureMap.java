package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * Maps biome IDs (or pseudo IDs) to textures. <i>Not thread-safe!</i>
 * <p>If several textures are set for one ID, one will be chosen at random when
 * putting tile into Atlas.</p>
 *
 * @author Hunternif
 */
@Environment(EnvType.CLIENT)
public class TileTextureMap {
    private static final TileTextureMap INSTANCE = new TileTextureMap();

    public static TileTextureMap instance() {
        return INSTANCE;
    }

    /**
     * This map stores the pseudo biome texture mappings, any biome with ID <0 is assumed to be a pseudo biome
     */
    private final Map<Identifier, TextureSet> textureMap = new HashMap<>();

    /**
     * Assign texture set to pseudo biome
     */
    public void setTexture(Identifier tileId, TextureSet textureSet) {
        if (textureSet == null) {
            if (textureMap.remove(tileId) != null) {
                Log.warn("Removing old texture for %d", tileId);
            }
            return;
        }

        textureMap.put(tileId, textureSet);
    }

    public TextureSet getDefaultTexture() {
        return TextureSetMap.instance().getByName(AntiqueAtlasMod.id("test"));
    }

    /**
     * Find the most appropriate standard texture set depending on
     * BiomeDictionary types.
     */
    public void autoRegister(Identifier id, Biome biome) {
        if (biome == null) {
            Log.error("Given biome is null. Cannot autodetect a suitable texture set for that.");
            return;
        }

        switch (biome.getCategory()) {
            case SWAMP:
//                setTexture(id, biome.getScale()  >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("swamp_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("swamp")));
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("swamp")));
                break;
            case OCEAN:
            case RIVER:
                setTexture(id, biome.getPrecipitation() == Biome.Precipitation.SNOW ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("ice")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("water")));
                break;
            case BEACH:
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("shore")));
                break;
            case JUNGLE:
//                setTexture(id, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("jungle_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("jungle")));
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("jungle")));
                break;
            case SAVANNA:
//                setTexture(id, biome.getDepth() >= 1.0f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plateau_savanna")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("savanna")));
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("savanna")));
                break;
            case MESA:
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plateau_mesa")));
                break;
            case FOREST:
                setTexture(id, biome.getPrecipitation() == Biome.Precipitation.SNOW ?
//                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_pines_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_pines"))) :
//                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("forest_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("forest")))
                        (TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_pines"))) :
                        (TextureSetMap.instance().getByName(AntiqueAtlasMod.id("forest")))
                );
                break;
            case PLAINS:
                setTexture(id, biome.getPrecipitation() == Biome.Precipitation.SNOW ?
//                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow"))) :
//                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plains")))
                        TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow")) :
                        TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plains"))
                );
                break;
            case ICY:
//                setTexture(id, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mountains_snow_caps")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("ice_spikes"))); // TODO also snowy mountains/tundra?
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("ice_spikes")));
                break;
            case DESERT:
//                setTexture(id, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("desert_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("desert")));
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("desert")));
                break;
            case TAIGA:
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow"))); // TODO
                break;
            case EXTREME_HILLS:
//                setTexture(id, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mountains")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("hills")));
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mountains")));
                break;
            case THEEND:
                //List<List<Supplier<PlacedFeature>>> features = biome.getGenerationSettings().getFeatures();
                boolean has_chorus_plant = true;
                //features.stream().anyMatch(supplier -> supplier.stream().anyMatch(step -> step.get() == ConfiguredFeatures.CHORUS_PLANT));
                if (has_chorus_plant) {
                    setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("end_island_plants")));
                } else {
                    setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("end_island")));
                }
                break;
            case MUSHROOM:
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mushroom")));
            case NETHER:
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("soul_sand_valley")));
            case NONE:
                setTexture(id, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("end_void")));
                break;
            case UNDERGROUND:
                Log.warn("Underground biomes aren't supported yet.");
                break;
            default:
                Log.warn("Couldn't auto-registered standard texture set for biome %s", id.toString());
                setTexture(id, getDefaultTexture());
        }

        if (textureMap.get(id) != null) {
            Log.info("Auto-registered standard texture set for biome %s: %s", id.toString(), textureMap.get(id).name);
        } else {
            Log.error("Failed to auto-register a standard texture set for the biome '%s'. This is most likely caused by errors in the TextureSet configurations, check your resource packs first before reporting it as an issue!", id.toString());
        }
    }

    /**
     * Auto-registers the biome if it is not registered.
     */
    public void checkRegistration(Identifier id, Biome biome) {
        if (!isRegistered(id)) {
            autoRegister(id, biome);
        }
    }

    /**
     * Checks for pseudo biome ID - if not registered, use default
     */
    private void checkRegistration(Identifier id) {
        if (!isRegistered(id)) {
            setTexture(id, getDefaultTexture());
        }
    }

    public boolean isRegistered(Identifier id) {
        return textureMap.containsKey(id);
    }

    /**
     * If unknown biome, auto-registers a texture set. If null, returns default set.
     */
    public TextureSet getTextureSet(Identifier tile) {
        if (tile == null) {
            return getDefaultTexture();
        }

        Biome biome = BuiltinRegistries.BIOME.get(tile);
        if (biome != null) {
            checkRegistration(tile, biome);
        } else {
            checkRegistration(tile);
        }

        return textureMap.getOrDefault(tile, getDefaultTexture());
    }

    public ITexture getTexture(SubTile subTile) {
        return getTextureSet(subTile.tile).getTexture(subTile.variationNumber);
    }

    public List<Identifier> getAllTextures() {
        List<Identifier> list = new ArrayList<>();

        for (Entry<Identifier, TextureSet> entry : textureMap.entrySet()) {
            Arrays.stream(entry.getValue().textures).forEach(iTexture -> list.add(iTexture.getTexture()));
        }

        return list;
    }
}
