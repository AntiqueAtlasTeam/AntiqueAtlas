package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class Village {
    public static Collection<ChunkPos> matches(StructurePoolElement element, BlockBox box) {
        if (element instanceof SinglePoolElement) {
            SinglePoolElement singlePoolElement = (SinglePoolElement) element;

            Optional<Identifier> left = singlePoolElement.field_24015.left();
            if (left.isPresent()) {
                String path = left.get().getPath();

                if (path.startsWith("village")) {
                    return matchesVillage(path.substring(path.indexOf("/", 8) + 1), box);
                }
            }
        }


        return Collections.EMPTY_SET;
    }

    private static Collection<ChunkPos> matchesVillage(String path, BlockBox box) {
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

    private static Collection<ChunkPos> boxToChunkPos(BlockBox box) {
        return Collections.singleton(new ChunkPos(MathUtil.getCenter(box)));
    }

    private static Collection<ChunkPos> matchesHouses(String id, BlockBox box) {
        if (id.contains("temple")) {
            return boxToChunkPos(box);
        }

        return Collections.EMPTY_SET;
    }

    public static void registerMarkers() {
        if (AntiqueAtlasMod.CONFIG.autoVillageMarkers) {
            StructureHandler.registerMarker(StructureFeature.VILLAGE, AntiqueAtlasMod.id("village"), new TranslatableText("gui.antiqueatlas.marker.village"));
        }
    }

    public static void registerPieces() {
        StructureHandler.registerTile(StructurePieceType.JIGSAW, 1, ExtTileIdMap.TILE_VILLAGE_WELL, Village::matches);
    }
}
