package hunternif.mc.impl.atlas.core.scanning.forge;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.Tags;

public class TileDetectorBaseImpl {
    public static boolean hasSwampWater(RegistryEntry<Biome> biomeTag) {
        return biomeTag.isIn(Tags.Biomes.IS_SWAMP);
    }
}
