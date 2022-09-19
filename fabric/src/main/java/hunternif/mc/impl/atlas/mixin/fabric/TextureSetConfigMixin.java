package hunternif.mc.impl.atlas.mixin.fabric;

import hunternif.mc.impl.atlas.client.TextureSetConfig;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TextureSetConfig.class)
public class TextureSetConfigMixin implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return new Identifier("antiqueatlas", "texture_sets");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return null;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(
                new Identifier("antiqueatlas", "textures")
        );
    }
}
