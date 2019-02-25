package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.mixinhooks.ServerWorldLoadCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
    @Inject(at = @At("RETURN"), method = "<init>")
    public void afterInit(MinecraftServer minecraftServer_1, Executor executor_1, WorldSaveHandler worldSaveHandler_1, LevelProperties levelProperties_1, DimensionType dimensionType_1, Profiler profiler_1, WorldGenerationProgressListener worldGenerationProgressListener_1, CallbackInfo info) {
        ServerWorldLoadCallback.EVENT.invoker().onWorldLoaded((ServerWorld) (Object) this);
    }
}