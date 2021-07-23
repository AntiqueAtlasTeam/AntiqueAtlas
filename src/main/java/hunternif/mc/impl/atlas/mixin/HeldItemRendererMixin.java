package hunternif.mc.impl.atlas.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.matrix.MatrixStack;

import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.client.OverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(FirstPersonRenderer.class)
public abstract class HeldItemRendererMixin {
    private OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();

    @Shadow
    private ItemStack itemStackOffHand;

    @Shadow
    protected abstract void renderArm(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, HandSide arm);

    @Shadow
    protected abstract void renderArmFirstPerson(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, float equipProgress, float swingProgress, HandSide arm);

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    protected abstract float getMapAngleFromPitch(float tickDelta);

    @Shadow
    private ItemStack itemStackMainHand;

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void renderAtlas(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand handIn, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, CallbackInfo ci, boolean flag, HandSide handside) {
//    private void renderAtlas(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, CallbackInfo ci, boolean bl, HandSide arm) {
        if (stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            if (flag && this.itemStackOffHand.isEmpty()) {
                renderAtlasInBothHands(matrixStackIn, bufferIn, combinedLightIn, pitch, equipProgress, swingProgress);
            } else {
                renderAtlasInOneHand(matrixStackIn, bufferIn, combinedLightIn, equipProgress, handside, swingProgress, stack);
            }

            matrixStackIn.pop();

            ci.cancel();
        }
    }

    private void renderAtlasInBothHands(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
        float f = MathHelper.sqrt(swingProgress);
        float g = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
        float h = -0.4F * MathHelper.sin(f * 3.1415927F);
        matrices.translate(0.0D, -g / 2.0F, h);
        float i = getMapAngleFromPitch(pitch);
        matrices.translate(0.0D, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.7200000286102295D);
        matrices.rotate(Vector3f.XP.rotationDegrees(i * -85.0F));

        if (!this.mc.player.isInvisible()) {
            matrices.push();
            matrices.rotate(Vector3f.YP.rotationDegrees(90.0F));
            this.renderArm(matrices, vertexConsumers, light, HandSide.RIGHT);
            this.renderArm(matrices, vertexConsumers, light, HandSide.LEFT);
            matrices.pop();
        }

        float j = MathHelper.sin(f * 3.1415927F);
        matrices.rotate(Vector3f.XP.rotationDegrees(j * 20.0F));
        matrices.scale(0.5F, 0.5F, 1.0F);

        this.renderFirstPersonAtlas(matrices, vertexConsumers, light, this.itemStackMainHand);
    }

    private void renderAtlasInOneHand(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, float equipProgress, HandSide arm, float swingProgress, ItemStack item) {
        float f = arm == HandSide.RIGHT ? 1.0F : -1.0F;
        matrices.push();
        matrices.translate(f * 0.125F, -0.125D, 0.0D);
        if (!this.mc.player.isInvisible()) {
            matrices.push();
            matrices.rotate(Vector3f.ZP.rotationDegrees(f * 10.0F));
            this.renderArmFirstPerson(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.pop();
        }


        matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75D);
        float g = MathHelper.sqrt(swingProgress);
        float h = MathHelper.sin(g * 3.1415927F);
        float i = -0.5F * h;
        float j = 0.4F * MathHelper.sin(g * 6.2831855F);
        float k = -0.3F * MathHelper.sin(swingProgress * 3.1415927F);
        matrices.translate(f * i, j - 0.3F * h, k);
        matrices.rotate(Vector3f.YP.rotationDegrees(h * -45.0F));
        matrices.rotate(Vector3f.YP.rotationDegrees(f * h * -30.0F));

        matrices.rotate(Vector3f.YP.rotationDegrees(180.0F));
        matrices.rotate(Vector3f.ZP.rotationDegrees(180.0F));
        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-0.75D, -0.5D, 0.0D);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);

        matrices.scale(0.4f, 0.4F, 0.4F);

        atlasOverlayRenderer.drawOverlay(matrices, vertexConsumers, light);
        matrices.pop();
    }

    private void renderFirstPersonAtlas(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, ItemStack itemStackMainHand) {
        matrices.rotate(Vector3f.YP.rotationDegrees(180.0F));
        matrices.rotate(Vector3f.ZP.rotationDegrees(180.0F));

        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-1.85D, -0.5D, 0.0D);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);

        atlasOverlayRenderer.drawOverlay(matrices, vertexConsumers, light);
    }
}
