package kenkron.antiqueatlasoverlay.mixin;

import kenkron.antiqueatlasoverlay.OverlayRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameGui.class)
public class MixinInGameHud {
    @Shadow
    protected int scaledWidth;
    @Shadow
    protected int scaledHeight;

    @Inject(at = @At("TAIL"), method = "renderGameOverlay")
    public void draw(float partial, CallbackInfo info) {
        OverlayRenderer.drawOverlay(scaledWidth, scaledHeight);
    }
}
