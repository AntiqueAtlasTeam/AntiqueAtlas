package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.client.gui.ExportProgressOverlay;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(Gui.class)
@OnlyIn(Dist.CLIENT)
public class MixinInGameHud {
    @Shadow
    private int screenWidth;
    @Shadow
    private int screenHeight;

    @Inject(at = @At("TAIL"), method = "render")
    public void draw(PoseStack matrix, float partial, CallbackInfo info) {
        ExportProgressOverlay.INSTANCE.draw(matrix, screenWidth, screenHeight);
    }
}
