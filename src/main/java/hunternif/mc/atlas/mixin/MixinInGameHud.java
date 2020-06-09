package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.client.gui.ExportProgressOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public class MixinInGameHud {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Inject(at = @At("TAIL"), method = "render")
    public void draw(MatrixStack matrices, float f, CallbackInfo ci) {
        ExportProgressOverlay.INSTANCE.draw(matrices, scaledWidth, scaledHeight);
    }
}
