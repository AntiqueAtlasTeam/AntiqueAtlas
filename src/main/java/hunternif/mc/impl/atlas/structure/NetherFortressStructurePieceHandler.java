package hunternif.mc.impl.atlas.structure;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.api.AtlasAPI;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class NetherFortressStructurePieceHandler implements StructurePieceAddedCallback {
	protected static boolean hasTile(World world, int chunkX, int chunkZ) {
		return AntiqueAtlasMod.extBiomeData.getData().getBiomeAt(world.getRegistryKey(), chunkX, chunkZ) != -1;
	}

	@Override
	public void onStructurePieceAdded(World world, StructurePiece structurePiece) {
		StructurePieceType type = structurePiece.getType();
		BlockBox box = structurePiece.getBoundingBox();

		if (type == StructurePieceType.NETHER_FORTRESS_BRIDGE) {
			if (box.getBlockCountX() > 16) {
				int chunkZ = MathUtil.getCenter(box).getZ() >> 4;
				for (int x = box.minX; x < box.maxX; x += 16) {
					ChunkPos pos = new ChunkPos(x >> 4, chunkZ);
					if (!hasTile(world, x >> 4, chunkZ)) {
						AtlasAPI.tiles.putCustomGlobalTile(world, ExtTileIdMap.TILE_NETHER_BRIDGE_X, pos.x, pos.z);
					}
				}
			}
		}
	}
}
