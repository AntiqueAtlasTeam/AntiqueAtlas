package hunternif.mc.impl.atlas.core.scanning.fabric;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

public class TileDetectorBaseImpl {
    public static boolean hasSwampWater(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(ConventionalBiomeTags.SWAMP);
    }
}
