package kenkron.antiqueatlasoverlay.mixin;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import kenkron.antiqueatlasoverlay.OverlayRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public abstract class MixinInGameHud {
    @Shadow
    protected abstract void renderCrosshair(MatrixStack matrixStack);

    private final OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void dontRenderIfMapOpen(InGameHud inGameHud, MatrixStack matrixStack) {
        if (MinecraftClient.getInstance().player != null) {
            for (ItemStack stack : MinecraftClient.getInstance().player.getItemsHand()) {
                if (stack.getItem() == Items.FILLED_MAP || stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
                    return;
                }
            }
        }

        renderCrosshair(matrixStack);
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void draw(MatrixStack matrices, float partial, CallbackInfo info) {
        if (AntiqueAtlasMod.CONFIG.enabled) {
            matrices.push();
            matrices.translate(AntiqueAtlasMod.CONFIG.xPosition, AntiqueAtlasMod.CONFIG.yPosition, 0);
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
