package hunternif.mc.impl.atlas.client;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface IResourceReloadListener<T> extends ResourceReloadListener
{

    CompletableFuture<T> load(ResourceManager manager, Profiler profiler, Executor executor);

    CompletableFuture<Void> apply(T data,
                                  ResourceManager manager,
                                  Profiler profiler,
                                  Executor executor);


    default CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer,
                                           ResourceManager manager,
                                           Profiler prepareProfiler,
                                           Profiler applyProfiler,
                                           Executor prepareExecutor,
                                           Executor applyExecutor)
    {
        CompletableFuture<T> load = load(manager, prepareProfiler, prepareExecutor);

        return load.thenCompose(synchronizer::whenPrepared)
                .thenCompose(t -> apply(t, manager, applyProfiler, applyExecutor));
    }

}
