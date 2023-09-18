package hunternif.mc.impl.atlas.client.fabric;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.TileTextureMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TileTextureMapImpl {

    static public Optional<Identifier> guessFittingTextureSet(RegistryKey<Biome> biome) {
        if (MinecraftClient.getInstance().world == null)
            return Optional.empty();

        RegistryEntry.Reference<Biome> biomeTag = MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.BIOME).entryOf(biome);
        if (biomeTag.isIn(ConventionalBiomeTags.SWAMP)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("swamp_hills"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("swamp"));
            }
        }

        if (biomeTag.isIn(BiomeTags.IS_OCEAN)
                || biomeTag.isIn(BiomeTags.IS_DEEP_OCEAN)
                || biomeTag.isIn(BiomeTags.IS_RIVER)
                || biomeTag.isIn(ConventionalBiomeTags.AQUATIC)) {
            if (biomeTag.isIn(ConventionalBiomeTags.ICY))
                return Optional.of(AntiqueAtlasMod.id("ice"));

            return Optional.of(AntiqueAtlasMod.id("water"));
        }

        if (biomeTag.isIn(BiomeTags.IS_BEACH) || biomeTag.isIn(ConventionalBiomeTags.BEACH)) {
            return Optional.of(AntiqueAtlasMod.id("shore"));
        }

        if (biomeTag.isIn(BiomeTags.IS_JUNGLE)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("jungle_hills"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("jungle"));
            }
        }

        if (biomeTag.isIn(ConventionalBiomeTags.SAVANNA) || biomeTag.isIn(ConventionalBiomeTags.TREE_SAVANNA)) {
            return Optional.of(AntiqueAtlasMod.id("savana"));
        }

        if (biomeTag.isIn((ConventionalBiomeTags.MESA))) {
            return Optional.of(AntiqueAtlasMod.id("plateau_mesa"));
        }

        if (biomeTag.isIn(BiomeTags.IS_FOREST) || biomeTag.isIn(ConventionalBiomeTags.TREE_DECIDUOUS)) {
            if (biomeTag.isIn(ConventionalBiomeTags.ICY) || biomeTag.isIn(ConventionalBiomeTags.SNOWY)) {
                if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                    return Optional.of(AntiqueAtlasMod.id("snow_pines_hills"));
                } else {
                    return Optional.of(AntiqueAtlasMod.id("snow_pines"));
                }
            } else {
                if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                    return Optional.of(AntiqueAtlasMod.id("forest_hills"));
                } else {
                    return Optional.of(AntiqueAtlasMod.id("forest"));
                }
            }
        }

        if (biomeTag.isIn(ConventionalBiomeTags.PLAINS) || biomeTag.isIn(ConventionalBiomeTags.SNOWY_PLAINS)) {
            if (biomeTag.isIn(ConventionalBiomeTags.ICY)
                    || biomeTag.isIn(ConventionalBiomeTags.SNOWY)
            ) {
                if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                    return Optional.of(AntiqueAtlasMod.id("snow_hills"));
                } else {
                    return Optional.of(AntiqueAtlasMod.id("snow"));
                }
            } else {
                if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                    return Optional.of(AntiqueAtlasMod.id("hills"));
                } else {
                    return Optional.of(AntiqueAtlasMod.id("plains"));
                }
            }
        }

        if (biomeTag.isIn(ConventionalBiomeTags.ICY)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("mountains_snow_caps"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("ice_spikes"));
            }
        }

        if (biomeTag.isIn(ConventionalBiomeTags.DESERT)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("desert_hills"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("desert"));
            }
        }

        if (biomeTag.isIn(ConventionalBiomeTags.TAIGA)) {
            return Optional.of(AntiqueAtlasMod.id("snow"));
        }

        if (biomeTag.isIn(ConventionalBiomeTags.EXTREME_HILLS)) {
            return Optional.of(AntiqueAtlasMod.id("hills"));
        }

        if (biomeTag.isIn(ConventionalBiomeTags.MOUNTAIN) || biomeTag.isIn(ConventionalBiomeTags.MOUNTAIN_SLOPE)) {
            return Optional.of(AntiqueAtlasMod.id("mountains"));
        }

        if (biomeTag.isIn(ConventionalBiomeTags.MOUNTAIN_PEAK)) {
            return Optional.of(AntiqueAtlasMod.id("mountains_snow_caps"));
        }

        if (biomeTag.isIn(ConventionalBiomeTags.IN_THE_END) || biomeTag.isIn(ConventionalBiomeTags.END_ISLANDS)) {
            if (biomeTag.isIn(ConventionalBiomeTags.VEGETATION_DENSE) || biomeTag.isIn(ConventionalBiomeTags.VEGETATION_SPARSE)) {
                return Optional.of(AntiqueAtlasMod.id("end_island_plants"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("end_island"));
            }
        }

        if (biomeTag.isIn(ConventionalBiomeTags.MUSHROOM)) {
            return Optional.of(AntiqueAtlasMod.id("mushroom"));
        }

        if (biomeTag.isIn(ConventionalBiomeTags.IN_NETHER) || biomeTag.isIn(BiomeTags.IS_NETHER)) {
            return Optional.of(AntiqueAtlasMod.id("soul_sand_valley"));
        }

        if (biomeTag.isIn(ConventionalBiomeTags.VOID)) {
            return Optional.of(AntiqueAtlasMod.id("end_void"));
        }

        if (biomeTag.isIn(ConventionalBiomeTags.UNDERGROUND)) {
            AntiqueAtlasMod.LOG.warn("Underground biomes aren't supported yet.");
        }

        if (biomeTag.isIn(BiomeTags.IS_BADLANDS)) {
            return Optional.of(AntiqueAtlasMod.id("mesa"));
        }

        return TileTextureMap.guessFittingTextureSetFallback(biomeTag);
    }
}
