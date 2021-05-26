package hunternif.mc.impl.atlas.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import hunternif.mc.impl.atlas.core.TileIdMap;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public class NetherFortress {
	public static Collection<ChunkPos> bridgeX(World world,JigsawPiece element, MutableBoundingBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getXSize() > 16) {
			int chunkZ = MathUtil.getCenter(box).getZ() >> 4;
			for (int x = box.minX; x < box.maxX; x += 16) {
				matches.add(new ChunkPos(x >> 4, chunkZ));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeZ(World world,JigsawPiece element, MutableBoundingBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getZSize() > 16) {
			int chunkX = MathUtil.getCenter(box).getX() >> 4;
			for (int z = box.minZ; z < box.maxZ; z += 16) {
				matches.add(new ChunkPos(chunkX, z >> 4));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeEndX(World world,JigsawPiece element, MutableBoundingBox box) {
		if (box.getXSize() > box.getZSize()) {
			return Collections.singleton(new ChunkPos(box./*getCenter*/func_215126_f().getX() >> 4, box./*getCenter*/func_215126_f().getZ() >> 4));
		} else {
			return Collections.emptySet();
		}
	}

	public static Collection<ChunkPos> bridgeEndZ(World world,JigsawPiece element, MutableBoundingBox box) {
		if (box.getZSize() > box.getXSize()) {
			return Collections.singleton(new ChunkPos(box./*getCenter*/func_215126_f().getX() >> 4, box./*getCenter*/func_215126_f().getZ() >> 4));
		} else {
			return Collections.emptySet();
		}
	}


	public static void registerPieces() {
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_THRONE, 40, TileIdMap.NETHER_FORTRESS_BRIDGE_PLATFORM);

		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_STAIRS, 50, TileIdMap.NETHER_FORTRESS_BRIDGE_STAIRS);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_NETHER_STALK_ROOM, 50, TileIdMap.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM);

		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_CROSSING, 60, TileIdMap.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING);

		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_CORRIDOR_4, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_CORRIDOR, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_CORRIDOR_5, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_CORRIDOR_2, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_START, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_ENTRANCE, 70, TileIdMap.NETHER_FORTRESS_EXIT);

		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_CROSSING_3, 80, TileIdMap.NETHER_FORTRESS_BRIDGE_CROSSING);

		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_END,  90, TileIdMap.NETHER_BRIDGE_END_X, NetherFortress::bridgeEndX);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_END,  90, TileIdMap.NETHER_BRIDGE_END_Z, NetherFortress::bridgeEndZ);

		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_STRAIGHT,  100, TileIdMap.NETHER_BRIDGE_X, NetherFortress::bridgeX);
		StructureHandler.registerTile(IStructurePieceType.NETHER_FORTRESS_STRAIGHT,  100, TileIdMap.NETHER_BRIDGE_Z, NetherFortress::bridgeZ);
	}
}
