package hunternif.mc.impl.atlas.structure;

import com.google.common.collect.HashMultimap;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StructureHandler {
    private static final HashMultimap<Identifier, Pair<Identifier, Setter>> STRUCTURE_PIECE_TO_TILE_MAP = HashMultimap.create();
    private static final Map<String, Pair<Identifier, Setter>> JIGSAW_TO_TILE_MAP = new HashMap<>();
    private static final Map<Identifier, Pair<Identifier, Text>> STRUCTURE_PIECE_TO_MARKER_MAP = new HashMap<>();
    private static final Map<Identifier, Integer> STRUCTURE_PIECE_TILE_PRIORITY = new HashMap<>();
    private static final Setter ALWAYS = (world, element, box) -> Collections.singleton(new ChunkPos(MathUtil.getCenter(box).getX() >> 4, MathUtil.getCenter(box).getZ() >> 4));

    private static final Set<Triple<Integer, Integer, Identifier>> VISITED_STRUCTURES = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void registerTile(StructurePieceType structurePieceType, int priority, Identifier textureId, Setter setter) {
        Identifier id = Registry.STRUCTURE_PIECE.getId(structurePieceType);
        STRUCTURE_PIECE_TO_TILE_MAP.put(id, new Pair<>(textureId, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(textureId, priority);
    }

    public static void registerTile(StructurePieceType structurePieceType, int priority, Identifier textureId) {
        registerTile(structurePieceType, priority, textureId, ALWAYS);
    }

    public static void registerJigsawTile(String jigsawPattern, int priority, Identifier tileID, Setter setter) {
        JIGSAW_TO_TILE_MAP.put(jigsawPattern, new Pair<>(tileID, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(tileID, priority);
    }

    public static void registerJigsawTile(String jigsawPattern, int priority, Identifier tileID) {
        registerJigsawTile(jigsawPattern, priority, tileID, ALWAYS);
    }

    public static void registerMarker(StructureFeature<?> structureFeature, Identifier markerType, Text name) {
        STRUCTURE_PIECE_TO_MARKER_MAP.put(Registry.STRUCTURE_FEATURE.getId(structureFeature), new Pair<>(markerType, name));
    }

    private static int getPriority(Identifier structurePieceId) {
        return STRUCTURE_PIECE_TILE_PRIORITY.getOrDefault(structurePieceId, Integer.MAX_VALUE);
    }

    private static void put(World world, int chunkX, int chunkZ, Identifier textureId) {
        Identifier existingTile = AtlasAPI.getTileAPI().getGlobalTile(world, chunkX, chunkZ);

        if (getPriority(textureId) < getPriority(existingTile)) {
            AtlasAPI.getTileAPI().putGlobalTile(world, textureId, chunkX, chunkZ);
        }
    }

    private static void resolveJigsaw(StructurePiece jigsawPiece, ServerWorld world) {
        if (jigsawPiece instanceof PoolStructurePiece) {
            PoolStructurePiece pool = (PoolStructurePiece) jigsawPiece;

            if (pool.getPoolElement() instanceof SinglePoolElement) {
                SinglePoolElement singlePoolElement = (SinglePoolElement) pool.getPoolElement();

                Optional<Identifier> left = singlePoolElement.field_24015.left();
                if (left.isPresent()) {
                    String path = left.get().getPath();

                    for (Map.Entry<String, Pair<Identifier, Setter>> entry : JIGSAW_TO_TILE_MAP.entrySet()) {
                        Identifier tile = entry.getValue().getLeft();
                        Setter setter = entry.getValue().getRight();
                        if (path.contains(entry.getKey())) {
                            for(ChunkPos pos : setter.matches(world, singlePoolElement, pool.getBoundingBox())) {
                                put(world, pos.x, pos.z, tile);
                            }
                        }
                    }
                }
            }

        }
    }

    public static void resolve(StructurePiece structurePiece, ServerWorld world) {
        if (structurePiece.getType() == StructurePieceType.JIGSAW) {
            resolveJigsaw(structurePiece, world);

            return;
        }

        Identifier structurePieceId = Registry.STRUCTURE_PIECE.getId(structurePiece.getType());
        if (STRUCTURE_PIECE_TO_TILE_MAP.containsKey(structurePieceId)) {
            for (Pair<Identifier, Setter> entry : STRUCTURE_PIECE_TO_TILE_MAP.get(structurePieceId)) {
                Collection<ChunkPos> matches;
                if (structurePiece instanceof PoolStructurePiece) {
                    PoolStructurePiece pool = (PoolStructurePiece) structurePiece;
                    matches = entry.getRight().matches(world, pool.getPoolElement(), pool.getBoundingBox());
                } else {
                    matches = entry.getRight().matches(world, null, structurePiece.getBoundingBox());
                }

                for (ChunkPos pos : matches) {
                    put(world, pos.x, pos.z, entry.getLeft());
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
        Collection<ChunkPos> matches(World world, StructurePoolElement element, BlockBox box);
    }
}
