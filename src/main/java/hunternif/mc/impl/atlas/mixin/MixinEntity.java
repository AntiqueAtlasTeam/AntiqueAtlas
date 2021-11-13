package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.mixinhooks.EntityHooksAA;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class MixinEntity implements EntityHooksAA {
    @Shadow
    protected boolean isInsidePortal;

    @Override
    public boolean antiqueAtlas_isInPortal() {
        return isInsidePortal;
    }
}
