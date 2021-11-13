package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.mixinhooks.NewPlayerConnectionCallback;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerManager {
    @Inject(at = @At("TAIL"), method = "placeNewPlayer")
    private void afterPlayerConnect(Connection connection, ServerPlayer playerEntity, CallbackInfo info) {
    	MinecraftForge.EVENT_BUS.post(new NewPlayerConnectionCallback.TheEvent(playerEntity));
    }
}
