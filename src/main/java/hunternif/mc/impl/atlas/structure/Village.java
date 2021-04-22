package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;

public class Village {
    public static void registerMarkers() {
        if (AntiqueAtlasConfig.autoVillageMarkers.get()) {
            StructureHandler.registerMarker(Structure.VILLAGE, AntiqueAtlasMod.id("village"), new TranslationTextComponent("gui.antiqueatlas.marker.village"));
        }
    }
}