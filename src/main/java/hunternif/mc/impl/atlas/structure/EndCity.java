package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class EndCity {

    public static void registerMarkers() {
        StructureHandler.registerMarker(StructureFeature.END_CITY, AntiqueAtlasMod.id("end_city"), new TextComponent(""));
    }

}
