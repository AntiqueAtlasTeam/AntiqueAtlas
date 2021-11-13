package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.mixinhooks.NewServerConnectionCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPacketListener.class)
public class MixinClientPlayNetworkHandler {
    @Shadow
    private Minecraft minecraft;

    @Inject(at = @At("RETURN"), method = "handleLogin")
    public void afterGameJoin(ClientboundLoginPacket packet, CallbackInfo info) {
    	MinecraftForge.EVENT_BUS.post(new NewServerConnectionCallback.TheEvent(!minecraft.hasSingleplayerServer()));
    }
}