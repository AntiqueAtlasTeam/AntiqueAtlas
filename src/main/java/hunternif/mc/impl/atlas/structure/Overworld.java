package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.core.TileIdMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

import java.util.Collection;
import java.util.Collections;

public class Overworld {

    public static void registerPieces() {
        StructureHandler.registerTile(IStructurePieceType.RUINED_PORTAL, 10, TileIdMap.RUINED_PORTAL, Overworld::aboveGround);
    }

    private static Collection<ChunkPos> aboveGround(World world, @SuppressWarnings("unused") JigsawPiece structurePoolElement, MutableBoundingBox blockBox) {
        BlockPos center = new BlockPos(blockBox./*getCenter*/func_215126_f());
        if (world.getSeaLevel() - 4 <= center.getY()) {
            return Collections.singleton(new ChunkPos(center));
        }

        return Collections.emptyList();
    }
}