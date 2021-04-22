package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;

public class EndCity {

    public static void registerMarkers() {
        StructureHandler.registerMarker(Structure.END_CITY, AntiqueAtlasMod.id("end_city"), new StringTextComponent(""));
    }

}