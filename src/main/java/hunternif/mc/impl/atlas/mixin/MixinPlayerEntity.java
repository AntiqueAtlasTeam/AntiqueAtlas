package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.core.PlayerEventHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class MixinPlayerEntity {

    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo info) {
        PlayerEventHandler.onPlayerTick((Player) (Object) this);
    }
}