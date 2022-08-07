package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.text.LiteralText;
import net.minecraft.world.gen.feature.StructureFeature;

public class EndCity {

    public static void registerMarkers() {
        StructureHandler.registerMarker(StructureFeature.ENDCITY, AntiqueAtlasMod.id("end_city"), new LiteralText(""));
    }

}
