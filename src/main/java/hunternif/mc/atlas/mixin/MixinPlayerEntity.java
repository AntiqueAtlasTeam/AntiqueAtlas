package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.core.PlayerEventHandler;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo info) {
        PlayerEventHandler.onPlayerTick((PlayerEntity) (Object) this);
    }
}