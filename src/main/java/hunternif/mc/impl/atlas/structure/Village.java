package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.gen.feature.StructureFeature;

public class Village {
    public static void registerMarkers() {
        if (AntiqueAtlasMod.CONFIG.autoVillageMarkers) {
            StructureHandler.registerMarker(StructureFeature.VILLAGE, AntiqueAtlasMod.id("village"), new TranslatableText("gui.antiqueatlas.marker.village"));
        }
    }
}
