package kenkron.antiqueatlasoverlay.mixin;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import kenkron.antiqueatlasoverlay.OverlayRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
	private OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();

	@Shadow private ItemStack offHand;

	@Shadow protected abstract void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm);

	@Shadow @Final private MinecraftClient client;

	@Shadow protected abstract float getMapAngle(float tickDelta);

	@Shadow private ItemStack mainHand;

	@Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private void renderAtlas(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci, boolean bl, Arm arm) {
		if (item.getItem() == RegistrarAntiqueAtlas.ATLAS && !AntiqueAtlasMod.CONFIG.appearance.enabled) {
			if (bl && this.offHand.isEmpty()) {
				renderAtlasInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
			} else {
				renderAtlasInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
			}
		}
	}

	private void renderAtlasInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
		float f = MathHelper.sqrt(swingProgress);
		float g = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
		float h = -0.4F * MathHelper.sin(f * 3.1415927F);
		matrices.translate(0.0D, -g / 2.0F, h);
		float i = getMapAngle(pitch);
		matrices.translate(0.0D, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.7200000286102295D);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i * -85.0F));

		if (!this.client.player.isInvisible()) {
			matrices.push();
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
			this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
			this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
			matrices.pop();
		}

		float j = MathHelper.sin(f * 3.1415927F);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 20.0F));
		matrices.scale(0.5F, 0.5F, 1.0F);

		this.renderFirstPersonAtlas(matrices, vertexConsumers, light, this.mainHand);
	}

	private void renderAtlasInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack item) {
	}

	private void renderFirstPersonAtlas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack mainHand) {
		matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));

		matrices.scale(0.38F, 0.38F, 0.38F);
		matrices.translate(-1.8375D, -0.5D, 0.0D);
		matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);

		atlasOverlayRenderer.drawOverlay(matrices);
	}
}
