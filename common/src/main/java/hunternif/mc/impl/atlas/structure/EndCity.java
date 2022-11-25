package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.text.Text;
import net.minecraft.world.gen.structure.StructureType;

public class EndCity {

    public static void registerMarkers() {
        StructureHandler.registerMarker(StructureType.END_CITY, AntiqueAtlasMod.id("end_city"), Text.of(""));
    }

}
