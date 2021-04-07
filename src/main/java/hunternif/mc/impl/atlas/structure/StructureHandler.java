package hunternif.mc.impl.atlas.structure;

import com.google.common.collect.HashMultimap;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StructureHandler {
	private static final HashMultimap<Identifier, Pair<Identifier, Setter>> STRUCTURE_PIECE_TO_TILE_MAP = HashMultimap.create();
	private static final HashMap<Identifier, Pair<Identifier, Text>> STRUCTURE_PIECE_TO_MARKER_MAP = new HashMap<>();
	private static final HashMap<Identifier, Integer> STRUCTURE_PIECE_TILE_PRIORITY = new HashMap<>();
	private static final Setter ALWAYS = (box) -> Collections.singleton(new ChunkPos(MathUtil.getCenter(box).getX() >> 4, MathUtil.getCenter(box).getZ() >> 4));

	private static final Set<Triple<Integer, Integer, Identifier>> VISITED_STRUCTURES = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public static void registerTile(StructurePieceType structurePieceType, int priority, Identifier textureId, Setter setter) {
		Identifier id = Registry.STRUCTURE_PIECE.getId(structurePieceType);
		STRUCTURE_PIECE_TO_TILE_MAP.put(id, new Pair<>(textureId, setter));
		STRUCTURE_PIECE_TILE_PRIORITY.put(id, priority);
	}

	public static void registerTile(StructurePieceType structurePieceType, int priority, Identifier textureId) {
		registerTile(structurePieceType, priority, textureId, ALWAYS);
	}

	public static void registerMarker(StructureFeature<?> structureFeature, Identifier markerType, Text name) {
		STRUCTURE_PIECE_TO_MARKER_MAP.put(Registry.STRUCTURE_FEATURE.getId(structureFeature), new Pair<>(markerType, name));
	}

	private static int getPriority(Identifier structurePieceId) {
		return STRUCTURE_PIECE_TILE_PRIORITY.getOrDefault(structurePieceId, Integer.MAX_VALUE);
	}

	private static void put(Identifier structurePieceId, World world, int chunkX, int chunkZ, Identifier textureId) {
		Identifier existingTile = AtlasAPI.getTileAPI().getGlobalTile(world, chunkX, chunkZ);

		if (getPriority(structurePieceId) < getPriority(existingTile)) {
			AtlasAPI.getTileAPI().putGlobalTile(world, textureId, chunkX, chunkZ);
		}
	}

	public static void resolve(StructurePiece structurePiece, ServerWorld world) {
		Identifier structurePieceId = Registry.STRUCTURE_PIECE.getId(structurePiece.getType());
		if (STRUCTURE_PIECE_TO_TILE_MAP.containsKey(structurePieceId)) {
			for (Pair<Identifier, Setter> entry : STRUCTURE_PIECE_TO_TILE_MAP.get(structurePieceId)) {
				Collection<ChunkPos> matches = entry.getRight().matches(structurePiece.getBoundingBox());

				for (ChunkPos pos : matches) {
					put(structurePieceId, world, pos.x, pos.z, entry.getLeft());
				}
			}
		}
	}

	public static void resolve(StructureStart<?> structureStart, ServerWorld world) {
		Identifier structureId = Registry.STRUCTURE_FEATURE.getId(structureStart.getFeature());
		if (STRUCTURE_PIECE_TO_MARKER_MAP.containsKey(structureId)) {
			Triple<Integer, Integer, Identifier> key = Triple.of(
					structureStart.getBoundingBox().getCenter().getX(),
					structureStart.getBoundingBox().getCenter().getY(),
					structureId);

			if (VISITED_STRUCTURES.contains(key)) return;
			VISITED_STRUCTURES.add(key);

			AtlasAPI.getMarkerAPI().putGlobalMarker(
					world,
					false,
					STRUCTURE_PIECE_TO_MARKER_MAP.get(structureId).getLeft(),
					STRUCTURE_PIECE_TO_MARKER_MAP.get(structureId).getRight(),
					structureStart.getBoundingBox().getCenter().getX(),
					structureStart.getBoundingBox().getCenter().getZ()
			);
		}
	}

	interface Setter {
		Collection<ChunkPos> matches(BlockBox box);
	}
}
