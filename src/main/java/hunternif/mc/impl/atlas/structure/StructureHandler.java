package hunternif.mc.impl.atlas.structure;

import com.google.common.collect.HashMultimap;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StructureHandler {
    private static final HashMultimap<ResourceLocation, Tuple<ResourceLocation, Setter>> STRUCTURE_PIECE_TO_TILE_MAP = HashMultimap.create();
    private static final Map<String, Tuple<ResourceLocation, Setter>> JIGSAW_TO_TILE_MAP = new HashMap<>();
    private static final Map<ResourceLocation, Tuple<ResourceLocation, Component>> STRUCTURE_PIECE_TO_MARKER_MAP = new HashMap<>();
    private static final Map<ResourceLocation, Integer> STRUCTURE_PIECE_TILE_PRIORITY = new HashMap<>();
    private static final Setter ALWAYS = (world, element, box) -> Collections.singleton(new ChunkPos(MathUtil.getCenter(box).getX() >> 4, MathUtil.getCenter(box).getZ() >> 4));

    private static final Set<Triple<Integer, Integer, ResourceLocation>> VISITED_STRUCTURES = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void registerTile(StructurePieceType structurePieceType, int priority, ResourceLocation textureId, Setter setter) {
        ResourceLocation id = Registry.STRUCTURE_PIECE.getKey(structurePieceType);
        STRUCTURE_PIECE_TO_TILE_MAP.put(id, new Tuple<>(textureId, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(textureId, priority);
    }

    public static void registerTile(StructurePieceType structurePieceType, int priority, ResourceLocation textureId) {
        registerTile(structurePieceType, priority, textureId, ALWAYS);
    }

    public static void registerJigsawTile(String jigsawPattern, int priority, ResourceLocation tileID, Setter setter) {
        JIGSAW_TO_TILE_MAP.put(jigsawPattern, new Tuple<>(tileID, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(tileID, priority);
    }

    public static void registerJigsawTile(String jigsawPattern, int priority, ResourceLocation tileID) {
        registerJigsawTile(jigsawPattern, priority, tileID, ALWAYS);
    }

    public static void registerMarker(StructureFeature<?> structureFeature, ResourceLocation markerType, Component name) {
        STRUCTURE_PIECE_TO_MARKER_MAP.put(Registry.STRUCTURE_FEATURE.getKey(structureFeature), new Tuple<>(markerType, name));
    }

    private static int getPriority(ResourceLocation structurePieceId) {
        return STRUCTURE_PIECE_TILE_PRIORITY.getOrDefault(structurePieceId, Integer.MAX_VALUE);
    }

    private static void put(Level world, int chunkX, int chunkZ, ResourceLocation textureId) {
        ResourceLocation existingTile = AtlasAPI.getTileAPI().getGlobalTile(world, chunkX, chunkZ);

        if (getPriority(textureId) < getPriority(existingTile)) {
            AtlasAPI.getTileAPI().putGlobalTile(world, textureId, chunkX, chunkZ);
        }
    }

    private static void resolveJigsaw(StructurePiece jigsawPiece, ServerLevel world) {
        if (jigsawPiece instanceof PoolElementStructurePiece) {
            PoolElementStructurePiece pool = (PoolElementStructurePiece) jigsawPiece;

            if (pool.getElement() instanceof SinglePoolElement) {
                SinglePoolElement singlePoolElement = (SinglePoolElement) pool.getElement();

                Optional<ResourceLocation> left = singlePoolElement.template.left();
                if (left.isPresent()) {
                    String path = left.get().getPath();

                    for (Map.Entry<String, Tuple<ResourceLocation, Setter>> entry : JIGSAW_TO_TILE_MAP.entrySet()) {
                        ResourceLocation tile = entry.getValue().getA();
                        Setter setter = entry.getValue().getB();
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

    public static void resolve(StructurePiece structurePiece, ServerLevel world) {
        if (structurePiece.getType() == StructurePieceType.JIGSAW) {
            resolveJigsaw(structurePiece, world);

            return;
        }

        ResourceLocation structurePieceId = Registry.STRUCTURE_PIECE.getKey(structurePiece.getType());
        if (STRUCTURE_PIECE_TO_TILE_MAP.containsKey(structurePieceId)) {
            for (Tuple<ResourceLocation, Setter> entry : STRUCTURE_PIECE_TO_TILE_MAP.get(structurePieceId)) {
                Collection<ChunkPos> matches;
                if (structurePiece instanceof PoolElementStructurePiece) {
                    PoolElementStructurePiece pool = (PoolElementStructurePiece) structurePiece;
                    matches = entry.getB().matches(world, pool.getElement(), pool.getBoundingBox());
                } else {
                    matches = entry.getB().matches(world, null, structurePiece.getBoundingBox());
                }

                for (ChunkPos pos : matches) {
                    put(world, pos.x, pos.z, entry.getA());
                }
            }
        }
    }

    public static void resolve(StructureStart<?> structureStart, ServerLevel world) {
        ResourceLocation structureId = Registry.STRUCTURE_FEATURE.getKey(structureStart.getFeature());
        if (STRUCTURE_PIECE_TO_MARKER_MAP.containsKey(structureId)) {
            Triple<Integer, Integer, ResourceLocation> key = Triple.of(
                    structureStart.getBoundingBox().getCenter().getX(),
                    structureStart.getBoundingBox().getCenter().getY(),
                    structureId);

            if (VISITED_STRUCTURES.contains(key)) return;
            VISITED_STRUCTURES.add(key);

            AtlasAPI.getMarkerAPI().putGlobalMarker(
                    world,
                    false,
                    STRUCTURE_PIECE_TO_MARKER_MAP.get(structureId).getA(),
                    STRUCTURE_PIECE_TO_MARKER_MAP.get(structureId).getB(),
                    structureStart.getBoundingBox().getCenter().getX(),
                    structureStart.getBoundingBox().getCenter().getZ()
            );
        }
    }

    interface Setter {
        Collection<ChunkPos> matches(Level world, StructurePoolElement element, BoundingBox box);
    }
}
