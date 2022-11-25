package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.text.Text;
import net.minecraft.world.gen.structure.Structures;

public class Village {
    public static void registerMarkers() {
        if (AntiqueAtlasMod.CONFIG.autoVillageMarkers) {
            StructureHandler.registerMarker(Structures.VILLAGE_DESERT.value().getType(), AntiqueAtlasMod.id("village"), Text.translatable("gui.antiqueatlas.marker.village"));
            StructureHandler.registerMarker(Structures.VILLAGE_PLAINS.value().getType(), AntiqueAtlasMod.id("village"), Text.translatable("gui.antiqueatlas.marker.village"));
            StructureHandler.registerMarker(Structures.VILLAGE_SAVANNA.value().getType(), AntiqueAtlasMod.id("village"), Text.translatable("gui.antiqueatlas.marker.village"));
            StructureHandler.registerMarker(Structures.VILLAGE_SNOWY.value().getType(), AntiqueAtlasMod.id("village"), Text.translatable("gui.antiqueatlas.marker.village"));
            StructureHandler.registerMarker(Structures.VILLAGE_TAIGA.value().getType(), AntiqueAtlasMod.id("village"), Text.translatable("gui.antiqueatlas.marker.village"));
        }
    }

    public static void registerPieces() {
        StructureHandler.registerJigsawTile("well", 70, TileIdMap.TILE_VILLAGE_WELL);
        StructureHandler.registerJigsawTile("town_center", 70, TileIdMap.TILE_VILLAGE_WELL);

        StructureHandler.registerJigsawTile("temple", 90, TileIdMap.TILE_VILLAGE_CHURCH);
        StructureHandler.registerJigsawTile("butcher", 90, TileIdMap.TILE_VILLAGE_BUTCHERS_SHOP);
        StructureHandler.registerJigsawTile("smith", 90, TileIdMap.TILE_VILLAGE_SMITHY);
        StructureHandler.registerJigsawTile("library", 90, TileIdMap.TILE_VILLAGE_LIBRARY);


        StructureHandler.registerJigsawTile("large_farm", 89, TileIdMap.TILE_VILLAGE_FARMLAND_LARGE);
        StructureHandler.registerJigsawTile("farm", 90, TileIdMap.TILE_VILLAGE_FARMLAND_SMALL);
        StructureHandler.registerJigsawTile("animal_pen", 90, TileIdMap.TILE_VILLAGE_FARMLAND_SMALL);

        StructureHandler.registerJigsawTile("big_house", 100, TileIdMap.TILE_VILLAGE_L_HOUSE);
        StructureHandler.registerJigsawTile("medium_house", 100, TileIdMap.TILE_VILLAGE_SMALL_HOUSE);
        StructureHandler.registerJigsawTile("small_house", 100, TileIdMap.TILE_VILLAGE_HUT);

        StructureHandler.registerJigsawTile("house", 105, TileIdMap.TILE_VILLAGE_LIBRARY);

        StructureHandler.registerJigsawTile("streets", 110, TileIdMap.TILE_VILLAGE_PATH_X);
        StructureHandler.registerJigsawTile("streets", 111, TileIdMap.TILE_VILLAGE_PATH_Z);

        StructureHandler.registerJigsawTile("lamp", 120, TileIdMap.TILE_VILLAGE_TORCH);

    }
}
