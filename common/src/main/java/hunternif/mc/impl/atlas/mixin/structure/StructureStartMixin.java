package hunternif.mc.impl.atlas.mixin.structure;

import hunternif.mc.impl.atlas.structure.StructureAddedCallback;
import hunternif.mc.impl.atlas.structure.StructurePieceAddedCallback;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StructureStart.class)
public class StructureStartMixin {
    @Redirect(method = "generateStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructurePiece;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean structurePieceGenerated(StructurePiece structurePiece, StructureWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        ServerWorld world;

        if (serverWorldAccess instanceof ServerWorld) {
            world = (ServerWorld) serverWorldAccess;
        } else {
            world = ((ChunkRegion) serverWorldAccess).world;
        }

        StructurePieceAddedCallback.EVENT.invoker().onStructurePieceAdded(structurePiece, world);
        return structurePiece.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
    }

    @Inject(method = "generateStructure", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructureStart;setBoundingBoxFromChildren()V", shift = At.Shift.AFTER))
    private void structureGenerated(StructureWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos, CallbackInfo ci) {
        ServerWorld world;

        if (serverWorldAccess instanceof ServerWorld) {
            world = (ServerWorld) serverWorldAccess;
        } else {
            world = ((ChunkRegion) serverWorldAccess).world;
        }

        StructureAddedCallback.EVENT.invoker().onStructureAdded((StructureStart<?>) (Object) this, world);
    }
}
