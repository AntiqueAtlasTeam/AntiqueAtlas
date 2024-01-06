package hunternif.mc.impl.atlas.client.forge;

import net.minecraft.tag.BiomeTags;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

@OnlyIn(Dist.CLIENT)
public class TileTextureMapImpl {
    public static boolean biomeIsVoid(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_VOID);
    }

    public static boolean biomeIsEnd(RegistryEntry<Biome> biomeTag) {
        return false; // Too Specific
    }

    public static boolean biomeHasVegetation(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_SPARSE) || biomeTag.isIn(Tags.Biomes.IS_DENSE); // Not 100% sold here
    }

    public static boolean biomeIsNether(RegistryEntry<Biome> biomeTag) {
        return false; // Too Specific
    }

    public static boolean biomeIsSwamp(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_SWAMP);
    }

    public static boolean biomeIsWater(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_WATER);
    }

    public static boolean biomeIsIcy(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_COLD) || biomeTag.isIn(Tags.Biomes.IS_SNOWY);
    }

    public static boolean biomeIsShore(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(BiomeTags.IS_BEACH);
    }

    public static boolean biomeIsJungle(RegistryEntry<Biome> biomeTag) {
        return false; // None
    }

    public static boolean biomeIsSavanna(RegistryEntry<Biome> biomeTag) {
        return false; // None
    }

    public static boolean biomeIsBadlands(RegistryEntry<Biome> biomeTag) {
        return false; // None
    }

    public static boolean biomeIsPlateau(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn((Tags.Biomes.IS_PLATEAU));
    }

    public static boolean biomeIsForest(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_DENSE) || biomeTag.isIn(Tags.Biomes.IS_SPARSE);
    }

    public static boolean biomeIsSnowy(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_SNOWY);
    }

    public static boolean biomeIsPlains(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_PLAINS);
    }

    public static boolean biomeIsDesert(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_HOT);
    }

    public static boolean biomeIsTaiga(RegistryEntry<Biome> biomeTag) {
        return false; // None
    }

    public static boolean biomeIsExtremeHills(RegistryEntry<Biome> biomeTag) {
        return false; // None
    }

    public static boolean biomeIsPeak(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_PEAK);
    }

    public static boolean biomeIsMountain(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_SLOPE);
    }

    public static boolean biomeIsMushroom(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_MUSHROOM);
    }

    public static boolean biomeIsUnderground(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_UNDERGROUND);
    }
}
