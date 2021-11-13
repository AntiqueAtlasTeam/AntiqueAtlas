package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Collection;
import java.util.Collections;

public class Overworld {

    public static void registerPieces() {
        StructureHandler.registerTile(StructurePieceType.RUINED_PORTAL, 10, TileIdMap.RUINED_PORTAL, Overworld::aboveGround);
    }

    private static Collection<ChunkPos> aboveGround(Level world, @SuppressWarnings("unused") StructurePoolElement structurePoolElement, BoundingBox blockBox) {
        BlockPos center = new BlockPos(blockBox.getCenter());
        if (world.getSeaLevel() - 4 <= center.getY()) {
            return Collections.singleton(new ChunkPos(center));
        }

        return Collections.emptyList();
    }
}
