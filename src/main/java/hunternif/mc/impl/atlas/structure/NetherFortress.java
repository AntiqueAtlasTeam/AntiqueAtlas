package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.util.math.ChunkPos;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class NetherFortress {
	public static Collection<ChunkPos> bridgeX(MutableBoundingBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getXSize() > 16) {
			int chunkZ = MathUtil.getCenter(box).getZ() >> 4;
			for (int x = box.minX; x < box.maxX; x += 16) {
				matches.add(new ChunkPos(x >> 4, chunkZ));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeZ(MutableBoundingBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getZSize() > 16) {
			int chunkX = MathUtil.getCenter(box).getX() >> 4;
			for (int z = box.minZ; z < box.maxZ; z += 16) {
				matches.add(new ChunkPos(chunkX, z >> 4));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeEndX(MutableBoundingBox box) {
		if (box.getXSize() > box.getZSize()) {
			return Collections.singleton(new ChunkPos(box./*getCenter*/func_215126_f().getX() >> 4, box./*getCenter*/func_215126_f().getZ() >> 4));
		} else {
			return Collections.EMPTY_SET;
		}
	}

	public static Collection<ChunkPos> bridgeEndZ(MutableBoundingBox box) {
		if (box.getZSize() > box.getXSize()) {
			return Collections.singleton(new ChunkPos(box./*getCenter*/func_215126_f().getX() >> 4, box./*getCenter*/func_215126_f().getZ() >> 4));
		} else {
			return Collections.EMPTY_SET;
		}
	}


	public static void registerPieces() {
		StructureHandler.registerTile(IStructurePieceType.NEBEF,  110, ExtTileIdMap.NETHER_BRIDGE_END_X, NetherFortress::bridgeEndX);
		StructureHandler.registerTile(IStructurePieceType.NEBEF,  110, ExtTileIdMap.NETHER_BRIDGE_END_Z, NetherFortress::bridgeEndZ);

		StructureHandler.registerTile(IStructurePieceType.NEBS,  100, ExtTileIdMap.NETHER_BRIDGE_X, NetherFortress::bridgeX);
		StructureHandler.registerTile(IStructurePieceType.NEBS,  100, ExtTileIdMap.NETHER_BRIDGE_Z, NetherFortress::bridgeZ);

		StructureHandler.registerTile(IStructurePieceType.NECCS, 95, ExtTileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NESCLT, 95, ExtTileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NESC, 95, ExtTileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NESCRT, 95, ExtTileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NESCSC, 120, ExtTileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(IStructurePieceType.NESTART, 95, ExtTileIdMap.NETHER_FORTRESS_WALL);

		StructureHandler.registerTile(IStructurePieceType.NEBCR, 120, ExtTileIdMap.NETHER_FORTRESS_BRIDGE_CROSSING);
		StructureHandler.registerTile(IStructurePieceType.NESR, 120, ExtTileIdMap.NETHER_FORTRESS_BRIDGE_STAIRS);
		StructureHandler.registerTile(IStructurePieceType.NECTB, 90, ExtTileIdMap.NETHER_FORTRESS_EXIT);
		StructureHandler.registerTile(IStructurePieceType.NECSR, 90, ExtTileIdMap.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM);

		StructureHandler.registerTile(IStructurePieceType.NESCSC, 120, ExtTileIdMap.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING);
		StructureHandler.registerTile(IStructurePieceType.NECE, 120, ExtTileIdMap.NETHER_FORTRESS_BRIDGE_PLATFORM);
	}
}
