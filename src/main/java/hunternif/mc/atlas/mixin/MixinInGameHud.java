package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.client.gui.ExportProgressOverlay;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Inject(at = @At("TAIL"), method = "render")
    public void draw(MatrixStack matrix, float partial, CallbackInfo info) {
        ExportProgressOverlay.INSTANCE.draw(matrix, scaledWidth, scaledHeight, partial);
    }
}
