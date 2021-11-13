package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.core.watcher.DeathWatcher;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Player.class, ServerPlayer.class})
public class MixinPlayerEntityDeath {
    @Inject(at = @At("HEAD"), method = "die")
    public void onDeath(DamageSource source, CallbackInfo info) {
        DeathWatcher.onPlayerDeath((Player) (Object) this);
    }
}
