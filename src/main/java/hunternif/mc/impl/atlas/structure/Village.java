package hunternif.mc.impl.atlas.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;

public class Village {
	public static Collection<ChunkPos> matches(JigsawPiece element, MutableBoundingBox box) {
        if (element instanceof SingleJigsawPiece) {
        	SingleJigsawPiece singlePoolElement = (SingleJigsawPiece) element;

            Optional<ResourceLocation> left = singlePoolElement.field_236839_c_.left();
            if (left.isPresent()) {
                String path = left.get().getPath();

                if (path.startsWith("village")) {
                    return matchesVillage(path.substring(path.indexOf("/", 8) + 1), box);
                }
            }
        }


        return Collections.EMPTY_SET;
    }

    private static Collection<ChunkPos> matchesVillage(String path, MutableBoundingBox box) {
        int delimiter = path.indexOf("/");
        if (delimiter == -1) return Collections.EMPTY_SET;

        String base = path.substring(0, delimiter);
        String id = path.substring(delimiter + 1);

        if (base == "streets") {
//            return matchesStreets(id, box);
        }

        if (base == "houses") {
            return matchesHouses(id, box);
        }

        return Collections.EMPTY_SET;
    }

    private static Collection<ChunkPos> boxToChunkPos(MutableBoundingBox box) {
        return Collections.singleton(new ChunkPos(MathUtil.getCenter(box)));
    }

    private static Collection<ChunkPos> matchesHouses(String id, MutableBoundingBox box) {
        if (id.contains("temple")) {
            return boxToChunkPos(box);
        }

        return Collections.EMPTY_SET;
    }
	
    public static void registerMarkers() {
        if (AntiqueAtlasConfig.autoVillageMarkers.get()) {
            StructureHandler.registerMarker(Structure.VILLAGE, AntiqueAtlasMod.id("village"), new TranslationTextComponent("gui.antiqueatlas.marker.village"));
        }
    }
    
    public static void registerPieces() {
        StructureHandler.registerTile(IStructurePieceType./*JIGSAW*/field_242786_ad, 1, ExtTileIdMap.TILE_VILLAGE_WELL, Village::matches);
    }
}