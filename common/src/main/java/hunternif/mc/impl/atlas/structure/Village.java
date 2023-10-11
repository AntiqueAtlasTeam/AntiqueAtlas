package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.text.Text;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.List;

public class Village {
    public static void registerMarkers() {
        if (AntiqueAtlasMod.CONFIG.autoVillageMarkers) {
            StructureHandler.registerMarker(
                    List.of(
                            StructureKeys.VILLAGE_DESERT,
                            StructureKeys.VILLAGE_PLAINS,
                            StructureKeys.VILLAGE_SAVANNA,
                            StructureKeys.VILLAGE_SNOWY,
                            StructureKeys.VILLAGE_TAIGA
                    ), AntiqueAtlasMod.id("village"), Text.translatable("gui.antiqueatlas.marker.village"));
        }
    }
}
