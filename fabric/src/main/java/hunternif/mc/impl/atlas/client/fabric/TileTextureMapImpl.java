package hunternif.mc.impl.atlas.client.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TileTextureMapImpl {
    public static boolean biomeIsVoid(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.VOID);
    }

    public static boolean biomeIsEnd(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.IN_THE_END) || biomeTag.isIn(ConventionalBiomeTags.END_ISLANDS);
    }

    public static boolean biomeHasVegetation(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.VEGETATION_DENSE) || biomeTag.isIn(ConventionalBiomeTags.VEGETATION_SPARSE);
    }

    public static boolean biomeIsNether(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.IN_NETHER);
    }

    public static boolean biomeIsSwamp(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.SWAMP);
    }

    public static boolean biomeIsWater(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.AQUATIC);
    }

    public static boolean biomeIsIcy(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.ICY);
    }

    public static boolean biomeIsShore(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.BEACH);
    }

    private static boolean biomeIsJungle(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.JUNGLE) || biomeTag.isIn(ConventionalBiomeTags.TREE_JUNGLE);
    }

    public static boolean biomeIsSavanna(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.SAVANNA) || biomeTag.isIn(ConventionalBiomeTags.TREE_SAVANNA);
    }

    public static boolean biomeIsBadlands(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn((ConventionalBiomeTags.BADLANDS)) || biomeTag.isIn((ConventionalBiomeTags.MESA));
    }

    private static boolean biomeIsPlateau(RegistryEntry<Biome> biomeTag) {
        return false; // None
    }

    public static boolean biomeIsForest(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.TREE_DECIDUOUS);
    }

    public static boolean biomeIsSnowy(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.SNOWY);
    }

    public static boolean biomeIsPlains(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.PLAINS) || biomeTag.isIn(ConventionalBiomeTags.SNOWY_PLAINS);
    }

    public static boolean biomeIsDesert(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.DESERT);
    }

    public static boolean biomeIsTaiga(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.TAIGA);
    }

    public static boolean biomeIsExtremeHills(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.EXTREME_HILLS);
    }

    public static boolean biomeIsPeak(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.MOUNTAIN_PEAK);
    }

    public static boolean biomeIsMountain(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.MOUNTAIN) || biomeTag.isIn(ConventionalBiomeTags.MOUNTAIN_SLOPE);
    }

    public static boolean biomeIsMushroom(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.MUSHROOM);
    }

    public static boolean biomeIsUnderground(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.UNDERGROUND);
    }
}
