package stereowalker.forge;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

public interface IResourceReloadListener<T> extends ISelectiveResourceReloadListener {

	@Override
	default void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		
	}
	
	@Override
	default CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return load(resourceManager, preparationsProfiler, backgroundExecutor)
				.thenCompose(stage::markCompleteAwaitingOthers)
				.thenAccept((value) -> {
						apply(value, resourceManager, reloadProfiler, gameExecutor);
				});

	}

	CompletableFuture<T> load(IResourceManager resourceManager, IProfiler profiler, Executor executor);

	CompletableFuture<Void> apply (T value, IResourceManager resourceManager, IProfiler profiler, Executor executor);
}
