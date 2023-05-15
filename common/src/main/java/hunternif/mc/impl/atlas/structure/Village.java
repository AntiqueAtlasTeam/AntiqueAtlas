package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.text.Text;
import net.minecraft.world.gen.structure.StructureType;

public class Village {
    public static void registerMarkers() {
        if (AntiqueAtlasMod.CONFIG.autoVillageMarkers) {
            StructureHandler.registerMarker(StructureType.JIGSAW, AntiqueAtlasMod.id("village"), Text.translatable("gui.antiqueatlas.marker.village"));
        }
    }
}
