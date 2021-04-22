package hunternif.mc.impl.atlas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hunternif.mc.impl.atlas.core.watcher.DeathWatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;

@Mixin({PlayerEntity.class, ServerPlayerEntity.class})
public class MixinPlayerEntityDeath {
	
    @Inject(at = @At("HEAD"), method = "onDeath")
    public void onDeath(DamageSource source, CallbackInfo info) {
        DeathWatcher.onPlayerDeath((PlayerEntity) (Object) this);
    }
}