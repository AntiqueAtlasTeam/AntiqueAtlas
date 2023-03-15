package hunternif.mc.impl.atlas.client.forge;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class TileTextureMapImpl {
    static public Optional<Identifier> guessFittingTextureSet(RegistryKey<Biome> biome) {
        if (MinecraftClient.getInstance().world == null)
            return Optional.empty();

        RegistryEntry<Biome> biomeTag = MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).entryOf(biome);

        if (biomeTag.isIn(Tags.Biomes.IS_END)) {
//            if (biomeTag.isIn(Tags.Biomes.END) || biomeTag.isIn(ConventionalBiomeTags.VEGETATION_SPARSE)) {
//                return Optional.of(AntiqueAtlasMod.id("end_island_plants"));
//            } else {
            return Optional.of(AntiqueAtlasMod.id("end_island"));
//            }
        }

        if (biomeTag.isIn(BiomeTags.IS_NETHER)) {
            return Optional.of(AntiqueAtlasMod.id("soul_sand_valley"));
        }

        if (biomeTag.isIn(Tags.Biomes.IS_VOID)) {
            return Optional.of(AntiqueAtlasMod.id("end_void"));
        }

        if (biomeTag.isIn(Tags.Biomes.IS_SWAMP)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("swamp_hills"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("swamp"));
            }
        }

        if (biomeTag.isIn(BiomeTags.IS_OCEAN)
                || biomeTag.isIn(BiomeTags.IS_DEEP_OCEAN)
                || biomeTag.isIn(BiomeTags.IS_RIVER)
                || biomeTag.isIn(Tags.Biomes.IS_WATER)) {
            if (biomeTag.isIn(Tags.Biomes.IS_COLD) || biomeTag.isIn(Tags.Biomes.IS_SNOWY))
                return Optional.of(AntiqueAtlasMod.id("ice"));

            return Optional.of(AntiqueAtlasMod.id("water"));
        }

        if (biomeTag.isIn(BiomeTags.IS_BEACH) || biomeTag.isIn(Tags.Biomes.IS_BEACH)) {
            return Optional.of(AntiqueAtlasMod.id("shore"));
        }

        if (biomeTag.isIn(BiomeTags.IS_JUNGLE)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("jungle_hills"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("jungle"));
            }
        }

        if (biomeTag.isIn(Tags.Biomes.IS_SAVANNA)) {
            return Optional.of(AntiqueAtlasMod.id("savana"));
        }

        if (biomeTag.isIn((Tags.Biomes.IS_PLATEAU))) {
            return Optional.of(AntiqueAtlasMod.id("plateau_mesa"));
        }

        if (biomeTag.isIn(BiomeTags.IS_FOREST)
                || biomeTag.isIn(Tags.Biomes.IS_DENSE)
                || biomeTag.isIn(Tags.Biomes.IS_SPARSE)
        ) {
            if (biomeTag.isIn(Tags.Biomes.IS_COLD) || biomeTag.isIn(Tags.Biomes.IS_SNOWY)) {
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

        if (biomeTag.isIn(Tags.Biomes.IS_PLAINS)) {
            if (biomeTag.isIn(Tags.Biomes.IS_COLD)
                    || biomeTag.isIn(Tags.Biomes.IS_SNOWY)
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

        if (biomeTag.isIn(Tags.Biomes.IS_COLD)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("mountains_snow_caps"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("ice_spikes"));
            }
        }

        if (biomeTag.isIn(Tags.Biomes.IS_HOT)) {
            if (biomeTag.isIn(BiomeTags.IS_HILL)) {
                return Optional.of(AntiqueAtlasMod.id("desert_hills"));
            } else {
                return Optional.of(AntiqueAtlasMod.id("desert"));
            }
        }

//        if (biomeTag.isIn(Tags.Biomes.TAIGA)) {
//            return Optional.of(AntiqueAtlasMod.id("snow"));
//        }

//        if (biomeTag.isIn(Tags.Biomes.EXTREME_HILLS)) {
//            return Optional.of(AntiqueAtlasMod.id("hills"));
//        }

        if (biomeTag.isIn(Tags.Biomes.IS_SLOPE)) {
            return Optional.of(AntiqueAtlasMod.id("mountains"));
        }

        if (biomeTag.isIn(Tags.Biomes.IS_PEAK)) {
            return Optional.of(AntiqueAtlasMod.id("mountains_snow_caps"));
        }

        if (biomeTag.isIn(Tags.Biomes.IS_MUSHROOM)) {
            return Optional.of(AntiqueAtlasMod.id("mushroom"));
        }



        if (biomeTag.isIn(BiomeTags.IS_BADLANDS)) {
            return Optional.of(AntiqueAtlasMod.id("mesa"));
        }

        if (biomeTag.isIn(BiomeTags.IS_HILL)) {
            return Optional.of(AntiqueAtlasMod.id("hills"));
        }

        if (biomeTag.isIn(Tags.Biomes.IS_UNDERGROUND)) {
            AntiqueAtlasMod.LOG.warn("Underground biomes aren't supported yet.");
        }

        return Optional.empty();
    }
}
