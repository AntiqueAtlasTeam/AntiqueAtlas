package hunternif.mc.impl.atlas.mixin.fabric;

import hunternif.mc.impl.atlas.client.TileTextureConfig;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collection;
import java.util.List;

@Mixin(TileTextureConfig.class)
public abstract class TileTextureConfigMixin implements IdentifiableResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier("antiqueatlas", "tiles");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(
                new Identifier("antiqueatlas", "texture_sets"),
                new Identifier("antiqueatlas", "textures")
                );
    }
}
