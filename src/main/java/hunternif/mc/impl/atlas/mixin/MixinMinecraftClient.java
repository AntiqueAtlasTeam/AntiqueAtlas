package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Inject(method = "setLevel", at=@At("TAIL"))
    void AntiqueAtlas_joinWorld(ClientLevel world, CallbackInfo info)
    {
        ClientProxy.assignCustomBiomeTextures(world);
    }
}
