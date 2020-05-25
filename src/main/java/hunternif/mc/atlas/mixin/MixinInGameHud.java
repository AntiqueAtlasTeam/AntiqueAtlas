package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.client.gui.ExportProgressOverlay;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameGui.class)
public class MixinInGameHud extends AbstractGui {
    @Shadow
    protected int scaledWidth;
    @Shadow
    protected int scaledHeight;

    @Inject(at = @At("TAIL"), method = "renderGameOverlay(F)V")
    public void draw(float partial, CallbackInfo info) {
        ExportProgressOverlay.INSTANCE.draw(scaledWidth, scaledHeight, partial);
    }
}
