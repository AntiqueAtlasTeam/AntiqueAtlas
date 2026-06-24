package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.text.Text;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.List;

public class EndCity {

    public static void registerMarkers() {
        StructureHandler.registerMarker(List.of(StructureKeys.END_CITY), AntiqueAtlasMod.id("end_city"), Text.literal(""));
    }

}
