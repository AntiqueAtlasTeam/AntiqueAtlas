package hunternif.mc.impl.atlas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import hunternif.mc.impl.atlas.mixinhooks.EntityHooksAA;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class MixinEntity implements EntityHooksAA {
	@Shadow
	protected boolean inPortal;

	@Override
	public boolean antiqueAtlas_isInPortal() {
		return inPortal;
	}
}
