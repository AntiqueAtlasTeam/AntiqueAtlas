package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.core.TileIdMap;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class NetherFortress {
	public static Collection<ChunkPos> bridgeX(World world, StructurePoolElement element, BlockBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getBlockCountX() > 16) {
			int chunkZ = MathUtil.getCenter(box).getZ() >> 4;
			for (int x = box.getMinX(); x < box.getMaxX(); x += 16) {
				matches.add(new ChunkPos(x >> 4, chunkZ));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeZ(World world, StructurePoolElement element, BlockBox box) {
		HashSet<ChunkPos> matches = new HashSet<>();

		if (box.getBlockCountZ() > 16) {
			int chunkX = MathUtil.getCenter(box).getX() >> 4;
			for (int z = box.getMinZ(); z < box.getMaxZ(); z += 16) {
				matches.add(new ChunkPos(chunkX, z >> 4));
			}
		}

		return matches;
	}

	public static Collection<ChunkPos> bridgeEndX(World world, StructurePoolElement element, BlockBox box) {
		if (box.getBlockCountX() > box.getBlockCountZ()) {
			return Collections.singleton(new ChunkPos(box.getCenter().getX() >> 4, box.getCenter().getZ() >> 4));
		} else {
			return Collections.emptySet();
		}
	}

	public static Collection<ChunkPos> bridgeEndZ(World world, StructurePoolElement element, BlockBox box) {
		if (box.getBlockCountZ() > box.getBlockCountX()) {
			return Collections.singleton(new ChunkPos(box.getCenter().getX() >> 4, box.getCenter().getZ() >> 4));
		} else {
			return Collections.emptySet();
		}
	}


	public static void registerPieces() {
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, 40, TileIdMap.NETHER_FORTRESS_BRIDGE_PLATFORM);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, 50, TileIdMap.NETHER_FORTRESS_BRIDGE_STAIRS);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, 50, TileIdMap.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, 60, TileIdMap.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_START, 70, TileIdMap.NETHER_FORTRESS_WALL);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, 70, TileIdMap.NETHER_FORTRESS_EXIT);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 80, TileIdMap.NETHER_FORTRESS_BRIDGE_CROSSING);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_END,  90, TileIdMap.NETHER_BRIDGE_END_X, NetherFortress::bridgeEndX);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE_END,  90, TileIdMap.NETHER_BRIDGE_END_Z, NetherFortress::bridgeEndZ);

		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE,  100, TileIdMap.NETHER_BRIDGE_X, NetherFortress::bridgeX);
		StructureHandler.registerTile(StructurePieceType.NETHER_FORTRESS_BRIDGE,  100, TileIdMap.NETHER_BRIDGE_Z, NetherFortress::bridgeZ);
	}
}
