package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.texture.ITexture;
import hunternif.mc.impl.atlas.core.scaning.TileHeightType;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.*;
import java.util.Map.Entry;

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

    public static final Identifier DEFAULT_TEXTURE = AntiqueAtlasMod.id("test");

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
        if (tileId == null) return;

        if (textureSet == null) {
            if (textureMap.remove(tileId) != null) {
                Log.warn("Removing old texture for %d", tileId);
            }
            return;
        }

        textureMap.put(tileId, textureSet);
    }

    /**
     * Assign the same texture set to all height variations of the tileId
     */
    public void setAllTextures(Identifier tileId, TextureSet textureSet) {
        setTexture(tileId, textureSet);

        for (TileHeightType layer : TileHeightType.values()) {
            setTexture(Identifier.tryParse(tileId + "_" + layer), textureSet);
        }
    }

    public TextureSet getDefaultTexture() {
        return TextureSetMap.instance().getByName(DEFAULT_TEXTURE);
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

//        Identifier texture_set = switch (biome.getCategory()) {
//            case SWAMP ->
////                   biome.getScale()  >= 0.25f ? AntiqueAtlasMod.id("swamp_hills") : AntiqueAtlasMod.id("swamp");
//                    AntiqueAtlasMod.id("swamp");
//            case OCEAN, RIVER ->
//                    biome.getPrecipitation() == Biome.Precipitation.SNOW ? AntiqueAtlasMod.id("ice") : AntiqueAtlasMod.id("water");
//            case BEACH -> AntiqueAtlasMod.id("shore");
//            case JUNGLE ->
////                setTexture(id, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("jungle_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("jungle")));
//                    AntiqueAtlasMod.id("jungle");
//            case SAVANNA ->
////                setTexture(id, biome.getDepth() >= 1.0f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plateau_savanna")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("savanna")));
//                    AntiqueAtlasMod.id("savanna");
//            case MESA -> AntiqueAtlasMod.id("plateau_mesa");
//            case FOREST ->
////                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_pines_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_pines"))) :
////                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("forest_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("forest")))
//                    biome.getPrecipitation() == Biome.Precipitation.SNOW ? AntiqueAtlasMod.id("snow_pines") : AntiqueAtlasMod.id("forest");
//            case PLAINS ->
////                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow"))) :
////                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plains")))
//                    biome.getPrecipitation() == Biome.Precipitation.SNOW ? AntiqueAtlasMod.id("snow") : AntiqueAtlasMod.id("plains");
//            case ICY ->
////                setTexture(id, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mountains_snow_caps")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("ice_spikes"))); // TODO also snowy mountains/tundra?
//                    AntiqueAtlasMod.id("ice_spikes");
//            case DESERT ->
////                setTexture(id, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("desert_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("desert")));
//                    AntiqueAtlasMod.id("desert");
//            case TAIGA -> AntiqueAtlasMod.id("snow");
//            case EXTREME_HILLS -> AntiqueAtlasMod.id("hills");
//            case MOUNTAIN -> AntiqueAtlasMod.id("mountains");
//            case THEEND -> {
//                List<RegistryEntryList<PlacedFeature>> features = biome.getGenerationSettings().getFeatures();
//                PlacedFeature chorus_plant_feature = BuiltinRegistries.PLACED_FEATURE.get(new Identifier("chorus_plant"));
//                assert chorus_plant_feature != null;
//                boolean has_chorus_plant = features.stream().anyMatch(entries -> entries.stream().anyMatch(feature -> feature.value() == chorus_plant_feature));
//                if (has_chorus_plant) {
//                    yield AntiqueAtlasMod.id("end_island_plants");
//                } else {
//                    yield AntiqueAtlasMod.id("end_island");
//                }
//            }
//            case MUSHROOM -> AntiqueAtlasMod.id("mushroom");
//            case NETHER -> AntiqueAtlasMod.id("soul_sand_valley");
//            case NONE -> AntiqueAtlasMod.id("end_void");
//            case UNDERGROUND -> {
//                Log.warn("Underground biomes aren't supported yet.");
//                yield null;
//            }
//        };

//        if (texture_set != null) {
//            setAllTextures(id, TextureSetMap.instance().getByName(texture_set));
//            Log.info("Auto-registered standard texture set for biome %s: %s", id, texture_set);
//        } else {
            Log.error("Failed to auto-register a standard texture set for the biome '%s'. This is most likely caused by errors in the TextureSet configurations, check your resource packs first before reporting it as an issue!", id.toString());
            setAllTextures(id, getDefaultTexture());
//        }
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
