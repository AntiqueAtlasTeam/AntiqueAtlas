package hunternif.mc.impl.atlas.structure;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.tag.StructureTags;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StructureHandler {
    private static final HashMultimap<Identifier, Pair<Identifier, Setter>> STRUCTURE_PIECE_TO_TILE_MAP = HashMultimap.create();
    private static final Multimap<Identifier, Pair<Identifier, Setter>> JIGSAW_TO_TILE_MAP = HashMultimap.create();
    private static final Map<Identifier, Pair<Identifier, Text>> STRUCTURE_PIECE_TO_MARKER_MAP = new HashMap<>();
    private static final Map<TagKey<Structure>, Pair<Identifier, Text>> STRUCTURE_TAG_TO_MARKER_MAP = new HashMap<>();
    private static final Map<Identifier, Integer> STRUCTURE_PIECE_TILE_PRIORITY = new HashMap<>();
    public static final Setter ALWAYS = (world, element, box, rotation) -> Collections.singleton(new ChunkPos(MathUtil.getCenter(box).getX() >> 4, MathUtil.getCenter(box).getZ() >> 4));


    public static Collection<ChunkPos> IF_X_DIRECTION(World ignoredWorld, StructurePoolElement ignoredElement, BlockBox box, StructurePiece piece) {
        if (piece instanceof PoolStructurePiece poolPiece) {
            List<JigsawJunction> junctions = poolPiece.getJunctions();
            if (junctions.size() == 2) {
                if (junctions.get(0).getSourceX() == junctions.get(1).getSourceX() || junctions.get(0).getSourceZ() != junctions.get(1).getSourceZ()) {
                    return Collections.singleton(new ChunkPos(MathUtil.getCenter(box)));
                }
            } else {
                return Collections.singleton(new ChunkPos(MathUtil.getCenter(box)));
            }
        }
        return Collections.emptyList();
    }

    public static Collection<ChunkPos> IF_Z_DIRECTION(World ignoredWorld, StructurePoolElement ignoredElement, BlockBox box, StructurePiece piece) {
        if (piece instanceof PoolStructurePiece poolPiece) {
            List<JigsawJunction> junctions = poolPiece.getJunctions();
            if (junctions.size() == 2) {
                if (junctions.get(0).getSourceZ() == junctions.get(1).getSourceZ() || junctions.get(0).getSourceX() != junctions.get(1).getSourceX()) {
                    return Collections.singleton(new ChunkPos(MathUtil.getCenter(box)));
                }
            } else {
                return Collections.singleton(new ChunkPos(MathUtil.getCenter(box)));
            }
        }
        return Collections.emptyList();
    }

    private static final Set<Triple<Integer, Integer, Identifier>> VISITED_STRUCTURES = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void registerTile(StructurePieceType structurePieceType, int priority, Identifier textureId, Setter setter) {
        Identifier id = Registry.STRUCTURE_PIECE.getId(structurePieceType);
        STRUCTURE_PIECE_TO_TILE_MAP.put(id, new Pair<>(textureId, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(textureId, priority);
    }

    public static void registerTile(StructurePieceType structurePieceType, int priority, Identifier textureId) {
        registerTile(structurePieceType, priority, textureId, ALWAYS);
    }

    public static void registerJigsawTile(Identifier jigsawPattern, int priority, Identifier tileID, Setter setter) {
        JIGSAW_TO_TILE_MAP.put(jigsawPattern, new Pair<>(tileID, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(tileID, priority);
    }

    public static void registerJigsawTile(Identifier jigsawPattern, int priority, Identifier tileID) {
        registerJigsawTile(jigsawPattern, priority, tileID, ALWAYS);
    }

    public static void registerMarker(StructureType<?> structureFeature, Identifier markerType, Text name) {
        STRUCTURE_PIECE_TO_MARKER_MAP.put(Registry.STRUCTURE_TYPE.getId(structureFeature), new Pair<>(markerType, name));
    }

    public static void registerMarker(TagKey<Structure> structureTag, Identifier markerType, Text name) {
        STRUCTURE_TAG_TO_MARKER_MAP.put(structureTag, new Pair<>(markerType, name));
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
        if (jigsawPiece instanceof PoolStructurePiece pool) {
            if (pool.getPoolElement() instanceof SinglePoolElement singlePoolElement) {
                Optional<Identifier> left = singlePoolElement.location.left();

                if (left.isPresent()) {
                    for (Pair<Identifier, Setter> entry : JIGSAW_TO_TILE_MAP.get(left.get())) {
                        Identifier tile = entry.getLeft();
                        Setter setter = entry.getRight();
                        for (ChunkPos pos : setter.matches(world, singlePoolElement, pool.getBoundingBox(), jigsawPiece)) {
                            put(world, pos.x, pos.z, tile);
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
                if (structurePiece instanceof PoolStructurePiece pool) {
                    matches = entry.getRight().matches(world, pool.getPoolElement(), pool.getBoundingBox(), structurePiece);
                } else {
                    matches = entry.getRight().matches(world, null, structurePiece.getBoundingBox(), structurePiece);
                }

                for (ChunkPos pos : matches) {
                    put(world, pos.x, pos.z, entry.getLeft());
                }
            }
        }
    }

    public static void resolve(StructureStart structureStart, ServerWorld world) {
        Identifier structureId = Registry.STRUCTURE_TYPE.getId(structureStart.getStructure().getType());


        Pair<Identifier, Text> foundMarker = null;

        if (STRUCTURE_PIECE_TO_MARKER_MAP.containsKey(structureId)) {
            foundMarker =  STRUCTURE_PIECE_TO_MARKER_MAP.get(structureId);
        } else {
            Registry<Structure> structureRegistry = world.getRegistryManager().get(Registry.STRUCTURE_KEY);
            RegistryEntry<Structure> structureTag = structureRegistry.entryOf(structureRegistry.getKey(structureStart.getStructure()).orElse(null));
            for (Map.Entry<TagKey<Structure>, Pair<Identifier, Text>> entry : STRUCTURE_TAG_TO_MARKER_MAP.entrySet()) {
                if (structureTag.isIn(entry.getKey())) {
                    foundMarker = entry.getValue();
                    break;
                }
            }
        }

        if (foundMarker != null) {
            Triple<Integer, Integer, Identifier> key = Triple.of(
                    structureStart.getBoundingBox().getCenter().getX(),
                    structureStart.getBoundingBox().getCenter().getY(),
                    structureId);

            if (VISITED_STRUCTURES.contains(key)) return;
            VISITED_STRUCTURES.add(key);

            AtlasAPI.getMarkerAPI().putGlobalMarker(
                    world,
                    false,
                    foundMarker.getLeft(),
                    foundMarker.getRight(),
                    structureStart.getBoundingBox().getCenter().getX(),
                    structureStart.getBoundingBox().getCenter().getZ()
            );
        }
    }

    interface Setter {
        Collection<ChunkPos> matches(World world, StructurePoolElement element, BlockBox box, StructurePiece rotation);
    }
}
