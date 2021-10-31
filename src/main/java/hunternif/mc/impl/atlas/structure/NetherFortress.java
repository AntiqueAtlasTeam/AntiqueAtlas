package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.core.TileIdMap;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class NetherFortress {
	public static Collection<ChunkPos> bridgeX(Level world, StructurePoolElement element, BoundingBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getXSpan() > 16) {
			int chunkZ = MathUtil.getCenter(box).getZ() >> 4;
			for (int x = box.minX(); x < box.maxX(); x += 16) {
				matches.add(new ChunkPos(x >> 4, chunkZ));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeZ(Level world, StructurePoolElement element, BoundingBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getZSpan() > 16) {
			int chunkX = MathUtil.getCenter(box).getX() >> 4;
			for (int z = box.minZ(); z < box.maxZ(); z += 16) {
				matches.add(new ChunkPos(chunkX, z >> 4));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeEndX(Level world, StructurePoolElement element, BoundingBox box) {
		if (box.getXSpan() > box.getZSpan()) {
			return Collections.singleton(new ChunkPos(box.getCenter().getX() >> 4, box.getCenter().getZ() >> 4));
		} else {
			return Collections.emptySet();
		}
	}

	public static Collection<ChunkPos> bridgeEndZ(Level world, StructurePoolElement element, BoundingBox box) {
		if (box.getZSpan() > box.getXSpan()) {
			return Collections.singleton(new ChunkPos(box.getCenter().getX() >> 4, box.getCenter().getZ() >> 4));
		} else {
			return Collections.emptySet();
		}
	}


	public static void registerPieces() {
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, 40, TileIdMap.NETHER_FORTRESS_BRIDGE_PLATFORM);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, 50, TileIdMap.NETHER_FORTRESS_BRIDGE_STAIRS);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, 50, TileIdMap.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, 60, TileIdMap.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_START, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, 70, TileIdMap.NETHER_FORTRESS_EXIT);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 80, TileIdMap.NETHER_FORTRESS_BRIDGE_CROSSING);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER,  90, TileIdMap.NETHER_BRIDGE_END_X, NetherFortress::bridgeEndX);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER,  90, TileIdMap.NETHER_BRIDGE_END_Z, NetherFortress::bridgeEndZ);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT,  100, TileIdMap.NETHER_BRIDGE_X, NetherFortress::bridgeX);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT,  100, TileIdMap.NETHER_BRIDGE_Z, NetherFortress::bridgeZ);
	}
}
