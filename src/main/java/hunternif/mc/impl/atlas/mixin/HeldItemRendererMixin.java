package hunternif.mc.impl.atlas.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.client.OverlayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    private OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();

    @Shadow
    private ItemStack offHandItem;

    @Shadow
    protected abstract void renderMapHand(PoseStack matrices, MultiBufferSource vertexConsumers, int light, HumanoidArm arm);

    @Shadow
    protected abstract void renderPlayerArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, float swingProgress, HumanoidArm arm);

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract float calculateMapTilt(float tickDelta);

    @Shadow
    private ItemStack mainHandItem;

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void renderAtlas(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci, boolean bl, HumanoidArm arm) {
        if (item.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            if (bl && this.offHandItem.isEmpty()) {
                renderAtlasInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
            } else {
                renderAtlasInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
            }

            matrices.popPose();

            ci.cancel();
        }
    }

    private void renderAtlasInBothHands(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
        float f = Mth.sqrt(swingProgress);
        float g = -0.2F * Mth.sin(swingProgress * 3.1415927F);
        float h = -0.4F * Mth.sin(f * 3.1415927F);
        matrices.translate(0.0D, -g / 2.0F, h);
        float i = calculateMapTilt(pitch);
        matrices.translate(0.0D, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.7200000286102295D);
        matrices.mulPose(Vector3f.XP.rotationDegrees(i * -85.0F));

        if (!this.minecraft.player.isInvisible()) {
            matrices.pushPose();
            matrices.mulPose(Vector3f.YP.rotationDegrees(90.0F));
            this.renderMapHand(matrices, vertexConsumers, light, HumanoidArm.RIGHT);
            this.renderMapHand(matrices, vertexConsumers, light, HumanoidArm.LEFT);
            matrices.popPose();
        }

        float j = Mth.sin(f * 3.1415927F);
        matrices.mulPose(Vector3f.XP.rotationDegrees(j * 20.0F));
        matrices.scale(0.5F, 0.5F, 1.0F);

        this.renderFirstPersonAtlas(matrices, vertexConsumers, light, this.mainHandItem);
    }

    private void renderAtlasInOneHand(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, HumanoidArm arm, float swingProgress, ItemStack item) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        matrices.pushPose();
        matrices.translate(f * 0.125F, -0.125D, 0.0D);
        if (!this.minecraft.player.isInvisible()) {
            matrices.pushPose();
            matrices.mulPose(Vector3f.ZP.rotationDegrees(f * 10.0F));
            this.renderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.popPose();
        }


        matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75D);
        float g = Mth.sqrt(swingProgress);
        float h = Mth.sin(g * 3.1415927F);
        float i = -0.5F * h;
        float j = 0.4F * Mth.sin(g * 6.2831855F);
        float k = -0.3F * Mth.sin(swingProgress * 3.1415927F);
        matrices.translate(f * i, j - 0.3F * h, k);
        matrices.mulPose(Vector3f.XP.rotationDegrees(h * -45.0F));
        matrices.mulPose(Vector3f.YP.rotationDegrees(f * h * -30.0F));

        matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-0.75D, -0.5D, 0.0D);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);

        matrices.scale(0.4f, 0.4F, 0.4F);

        atlasOverlayRenderer.drawOverlay(matrices, vertexConsumers, light, item);
        matrices.popPose();
    }

    private void renderFirstPersonAtlas(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack item) {
        matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180.0F));

        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-1.85D, -0.5D, 0.0D);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);

        atlasOverlayRenderer.drawOverlay(matrices, vertexConsumers, light, item);
    }
}
