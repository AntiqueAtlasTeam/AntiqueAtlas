package kenkron.antiqueatlasoverlay.mixin;

import hunternif.mc.atlas.AntiqueAtlasMod;
import kenkron.antiqueatlasoverlay.OverlayRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
    private OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Inject(at = @At("TAIL"), method = "render")
    public void draw(MatrixStack matrices, float partial, CallbackInfo info) {
        if (AntiqueAtlasMod.CONFIG.appearance.enabled) {
            matrices.push();
            matrices.translate(AntiqueAtlasMod.CONFIG.position.xPosition, AntiqueAtlasMod.CONFIG.position.yPosition, 0);
            matrices.scale(
            0.3F,
            0.3F,
            1F
            );
            atlasOverlayRenderer.drawOverlay(matrices);
            matrices.pop();
        }
    }
}
