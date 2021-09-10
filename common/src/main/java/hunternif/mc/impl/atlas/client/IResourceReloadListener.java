package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.client.texture.ITexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public interface IResourceReloadListener<T> extends ResourceReloadListener {

    CompletableFuture<T> load(ResourceManager manager, Profiler profiler, Executor executor);

    CompletableFuture<Void> apply(T data, ResourceManager manager, Profiler profiler, Executor executor);


    default CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor)
    {
        CompletableFuture<T> load = load(manager, prepareProfiler, prepareExecutor);

        try {
            CompletableFuture<Void> applyFuture = apply(load.get(), manager, applyProfiler, applyExecutor);

            // TODO actually allow parallel loading of resources.
            applyFuture.join();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }

}
