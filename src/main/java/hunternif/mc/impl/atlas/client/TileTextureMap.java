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
     * Assign texture set to biome.
     */
    public void setTexture(Biome biome, TextureSet textureSet) {
        this.setTexture(BuiltinRegistries.BIOME.getId(biome), textureSet);
    }

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
    private void autoRegister(Biome biome) {
        if (biome == null) {
            Log.error("Given biome is null. Cannot autodetect a suitable texture set for that.");
            return;
        }

        switch (biome.getCategory()) {
            case SWAMP:
                setTexture(biome, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("swamp_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("swamp")));
                break;
            case OCEAN:
            case RIVER:
                setTexture(biome, biome.getPrecipitation() == Biome.Precipitation.SNOW ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("ice")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("water")));
                break;
            case BEACH:
                setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("shore")));
                break;
            case JUNGLE:
                setTexture(biome, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("jungle_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("jungle")));
                break;
            case SAVANNA:
                setTexture(biome, biome.getDepth() >= 1.0f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plateau_savanna")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("savanna")));
                break;
            case MESA:
                setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plateau_mesa")));
                break;
            case FOREST:
                setTexture(biome, biome.getPrecipitation() == Biome.Precipitation.SNOW ?
                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_pines_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_pines"))) :
                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("forest_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("forest")))
                );
                break;
            case PLAINS:
                setTexture(biome, biome.getPrecipitation() == Biome.Precipitation.SNOW ?
                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow"))) :
                        (biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("plains")))
                );
                break;
            case ICY:
                setTexture(biome, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mountains_snow_caps")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("ice_spikes"))); // TODO also snowy mountains/tundra?
                break;
            case DESERT:
                setTexture(biome, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("desert_hills")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("desert")));
                break;
            case TAIGA:
                setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("snow"))); // TODO
                break;
            case EXTREME_HILLS:
                setTexture(biome, biome.getScale() >= 0.25f ? TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mountains")) : TextureSetMap.instance().getByName(AntiqueAtlasMod.id("hills")));
                break;
            case THEEND:
                List<List<Supplier<ConfiguredFeature<?, ?>>>> features = biome.getGenerationSettings().getFeatures();
                boolean has_chorus_plant = features.stream().anyMatch(supplier -> supplier.stream().anyMatch(step -> step.get() == ConfiguredFeatures.CHORUS_PLANT));
                if (has_chorus_plant) {
                    setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("end_island_plants")));
                } else {
                    setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("end_island")));
                }
                break;
            case MUSHROOM:
                setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("mushroom")));
                break;
            case NETHER:
                setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("nether_wastes")));
                break;
            case NONE:
                setTexture(biome, TextureSetMap.instance().getByName(AntiqueAtlasMod.id("end_void")));
                break;
        }

        if (textureMap.get(BuiltinRegistries.BIOME.getId(biome)) != null) {
            Log.info("Auto-registered standard texture set for biome %s: %s", Objects.requireNonNull(BuiltinRegistries.BIOME.getId(biome)).toString(), textureMap.get(BuiltinRegistries.BIOME.getId(biome)).name);
        } else {
            Log.error("Failed to auto-register a standard texture set for the biome '%s'. This is most likely caused by errors in the TextureSet configurations, check your resource packs first before reporting it as an issue!", Objects.requireNonNull(BuiltinRegistries.BIOME.getId(biome)).toString());
            setTexture(biome, getDefaultTexture());
        }
    }

    /**
     * Auto-registers the biome if it is not registered.
     */
    public void checkRegistration(Biome biome) {
        if (!isRegistered(biome)) {
            autoRegister(biome);
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

    public boolean isRegistered(Biome biome) {
        return isRegistered(BuiltinRegistries.BIOME.getId(biome));
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
            checkRegistration(biome);
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
