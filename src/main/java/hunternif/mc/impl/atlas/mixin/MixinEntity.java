package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.mixinhooks.EntityHooksAA;

//@Mixin(Entity.class)
public class MixinEntity implements EntityHooksAA {
//	@Shadow
	protected boolean inNetherPortal;

	@Override
	public boolean antiqueAtlas_isInPortal() {
		return inNetherPortal;
	}
}
