package hunternif.mc.impl.atlas.structure;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.HashMultimap;
import com.mojang.datafixers.util.Pair;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.registry.MarkerType;
import hunternif.mc.impl.atlas.util.MathUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

public class StructureHandler {
	private static final HashMultimap<ResourceLocation, Pair<ResourceLocation, Setter>> STRUCTURE_PIECE_TO_TILE_MAP = HashMultimap.create();
    private static final Map<String, Pair<ResourceLocation, Setter>> JIGSAW_TO_TILE_MAP = new HashMap<>();
    private static final Map<ResourceLocation, Pair<ResourceLocation, ITextComponent>> STRUCTURE_PIECE_TO_MARKER_MAP = new HashMap<>();
    private static final Map<ResourceLocation, Integer> STRUCTURE_PIECE_TILE_PRIORITY = new HashMap<>();
    private static final Setter ALWAYS = (world, element, box) -> Collections.singleton(new ChunkPos(MathUtil.getCenter(box).getX() >> 4, MathUtil.getCenter(box).getZ() >> 4));

    private static final Set<Triple<Integer, Integer, ResourceLocation>> VISITED_STRUCTURES = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void registerTile(IStructurePieceType structurePieceType, int priority, ResourceLocation textureId, Setter setter) {
        ResourceLocation id = Registry.STRUCTURE_PIECE.getKey(structurePieceType);
        STRUCTURE_PIECE_TO_TILE_MAP.put(id, new Pair<>(textureId, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(textureId, priority);
    }

    public static void registerTile(IStructurePieceType structurePieceType, int priority, ResourceLocation textureId) {
        registerTile(structurePieceType, priority, textureId, ALWAYS);
    }

    public static void registerJigsawTile(String jigsawPattern, int priority, ResourceLocation tileID, Setter setter) {
        JIGSAW_TO_TILE_MAP.put(jigsawPattern, new Pair<>(tileID, setter));
        STRUCTURE_PIECE_TILE_PRIORITY.put(tileID, priority);
    }

    public static void registerJigsawTile(String jigsawPattern, int priority, ResourceLocation tileID) {
        registerJigsawTile(jigsawPattern, priority, tileID, ALWAYS);
    }

    public static void registerMarker(Structure<?> structureFeature, ResourceLocation markerType, ITextComponent name) {
        STRUCTURE_PIECE_TO_MARKER_MAP.put(Registry.STRUCTURE_FEATURE.getKey(structureFeature), new Pair<>(markerType, name));
    }

    private static int getPriority(ResourceLocation structurePieceId) {
        return STRUCTURE_PIECE_TILE_PRIORITY.getOrDefault(structurePieceId, Integer.MAX_VALUE);
    }

    private static void put(World world, int chunkX, int chunkZ, ResourceLocation textureId) {
        ResourceLocation existingTile = AtlasAPI.getTileAPI().getGlobalTile(world, chunkX, chunkZ);

        if (getPriority(textureId) < getPriority(existingTile)) {
            AtlasAPI.getTileAPI().putGlobalTile(world, textureId, chunkX, chunkZ);
        }
    }

    private static void resolveJigsaw(StructurePiece jigsawPiece, ServerWorld world) {
        if (jigsawPiece instanceof AbstractVillagePiece) {
        	AbstractVillagePiece pool = (AbstractVillagePiece) jigsawPiece;

            if (pool.getJigsawPiece() instanceof SingleJigsawPiece) {
            	SingleJigsawPiece singlePoolElement = (SingleJigsawPiece) pool.getJigsawPiece();

                Optional<ResourceLocation> left = singlePoolElement.field_236839_c_.left();
                if (left.isPresent()) {
                    String path = left.get().getPath();

                    for (Map.Entry<String, Pair<ResourceLocation, Setter>> entry : JIGSAW_TO_TILE_MAP.entrySet()) {
                        ResourceLocation tile = entry.getValue().getFirst();
                        Setter setter = entry.getValue().getSecond();
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
        if (structurePiece.getStructurePieceType() == IStructurePieceType./*JIGSAW*/field_242786_ad) {
            resolveJigsaw(structurePiece, world);

            return;
        }

        ResourceLocation structurePieceId = Registry.STRUCTURE_PIECE.getKey(structurePiece.getStructurePieceType());
        if (STRUCTURE_PIECE_TO_TILE_MAP.containsKey(structurePieceId)) {
            for (Pair<ResourceLocation, Setter> entry : STRUCTURE_PIECE_TO_TILE_MAP.get(structurePieceId)) {
                Collection<ChunkPos> matches;
                if (structurePiece instanceof AbstractVillagePiece) {
                	AbstractVillagePiece pool = (AbstractVillagePiece) structurePiece;
                    matches = entry.getSecond().matches(world, pool.getJigsawPiece(), pool.getBoundingBox());
                } else {
                    matches = entry.getSecond().matches(world, null, structurePiece.getBoundingBox());
                }

                for (ChunkPos pos : matches) {
                    put(world, pos.x, pos.z, entry.getFirst());
                }
            }
        }
    }

    public static void resolve(StructureStart<?> structureStart, ServerWorld world) {
        ResourceLocation structureId = Registry.STRUCTURE_FEATURE.getKey(structureStart.getStructure());
        if (STRUCTURE_PIECE_TO_MARKER_MAP.containsKey(structureId)) {
            Triple<Integer, Integer, ResourceLocation> key = Triple.of(
                    structureStart.getBoundingBox()./*getCenter*/func_215126_f().getX(),
                    structureStart.getBoundingBox()./*getCenter*/func_215126_f().getY(),
                    structureId);

            if (VISITED_STRUCTURES.contains(key)) return;
            VISITED_STRUCTURES.add(key);

            AtlasAPI.getMarkerAPI().putGlobalMarker(
                    world,
                    false,
                    STRUCTURE_PIECE_TO_MARKER_MAP.get(structureId).getFirst(),
                    STRUCTURE_PIECE_TO_MARKER_MAP.get(structureId).getSecond(),
                    structureStart.getBoundingBox()./*getCenter*/func_215126_f().getX(),
                    structureStart.getBoundingBox()./*getCenter*/func_215126_f().getZ()
            );
        }
    }

    interface Setter {
        Collection<ChunkPos> matches(World world, JigsawPiece element, MutableBoundingBox box);
    }
}
