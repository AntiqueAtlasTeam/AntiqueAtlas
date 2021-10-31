package hunternif.mc.impl.atlas.mixin.structure;

import hunternif.mc.impl.atlas.structure.StructureAddedCallback;
import hunternif.mc.impl.atlas.structure.StructurePieceAddedCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(StructureStart.class)
public class StructureStartMixin {

    @Shadow
    protected List<StructurePiece> pieces;


    @Redirect(method = "placeInChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;postProcess(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureFeatureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean structurePieceGenerated(StructurePiece structurePiece, WorldGenLevel serverWorldAccess, StructureFeatureManager structureAccessor, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        ServerLevel world;

        if (serverWorldAccess instanceof ServerLevel) {
            world = (ServerLevel) serverWorldAccess;
        } else {
            world = ((WorldGenRegion) serverWorldAccess).level;
        }

        MinecraftForge.EVENT_BUS.post(new StructurePieceAddedCallback.TheEvent(structurePiece, world));
        return structurePiece.postProcess(serverWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
    }

    @Inject(method = "placeInChunk", at = @At("RETURN"))
    private void structureGenerated(WorldGenLevel serverWorldAccess, StructureFeatureManager structureAccessor, ChunkGenerator chunkGenerator, Random random, BoundingBox blockBox, ChunkPos chunkPos, CallbackInfo ci) {
        ServerLevel world;

        if (serverWorldAccess instanceof ServerLevel) {
            world = (ServerLevel) serverWorldAccess;
        } else {
            world = ((WorldGenRegion) serverWorldAccess).level;
        }

        synchronized (this.pieces) {
            if(this.pieces.isEmpty()) return;
            MinecraftForge.EVENT_BUS.post(new StructureAddedCallback.TheEvent((StructureStart<?>) (Object) this, world));
        }
    }
}
