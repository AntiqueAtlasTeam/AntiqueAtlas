package hunternif.mc.impl.atlas.mixin.fabric;

import hunternif.mc.impl.atlas.client.TextureConfig;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.List;

@Mixin(TextureConfig.class)
public abstract class TextureConfigMixin implements IdentifiableResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier("antiqueatlas", "textures");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(
        );
    }
}
