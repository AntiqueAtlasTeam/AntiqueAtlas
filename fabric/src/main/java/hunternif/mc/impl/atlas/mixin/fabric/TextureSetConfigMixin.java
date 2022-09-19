package hunternif.mc.impl.atlas.mixin.fabric;

import hunternif.mc.impl.atlas.client.TextureSetConfig;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.List;

@Mixin(TextureSetConfig.class)
public abstract class TextureSetConfigMixin implements IdentifiableResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier("antiqueatlas", "texture_sets");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(
                new Identifier("antiqueatlas", "textures")
        );
    }
}
