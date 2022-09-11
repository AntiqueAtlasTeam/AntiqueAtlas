package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.ClientProxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "joinWorld", at=@At("TAIL"))
    void AntiqueAtlas_joinWorld(ClientWorld world, CallbackInfo info)
    {
        ClientProxy.assignCustomBiomeTextures(world);
    }
}
