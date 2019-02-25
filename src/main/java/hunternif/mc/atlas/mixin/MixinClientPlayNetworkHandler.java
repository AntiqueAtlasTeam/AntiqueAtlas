package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.mixinhooks.NewServerConnectionCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Shadow
    private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "onGameJoin")
    public void afterGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        NewServerConnectionCallback.EVENT.invoker().onNewConnection(!client.isIntegratedServerRunning());
    }
}