package hunternif.mc.impl.atlas.mixin.fabric;

import hunternif.mc.impl.atlas.ClientProxy;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiConsumer;

@Mixin(ClientProxy.class)
public class ClientProxyMixin {
    // This doesn't REALLY need to be a mixin, but it's tidy and ensures it runs at the right time for some reason.
    @Shadow
    public static BiConsumer<ResourceType, ResourceReloader> SORTED_REGISTER_LISTENER = (t, r) -> ResourceManagerHelper.get(t).registerReloadListener((IdentifiableResourceReloadListener) r);
}
