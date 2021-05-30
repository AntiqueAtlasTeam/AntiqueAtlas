package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;

public class Village {
	public static void registerMarkers() {
        if (AntiqueAtlasConfig.autoVillageMarkers.get()) {
            StructureHandler.registerMarker(Structure.VILLAGE, AntiqueAtlasMod.id("village"), new TranslationTextComponent("gui.antiqueatlas.marker.village"));
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