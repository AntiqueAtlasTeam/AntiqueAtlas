//package hunternif.mc.impl.atlas.client.event;
//
//import com.mojang.blaze3d.matrix.MatrixStack;
//
//import hunternif.mc.impl.atlas.AntiqueAtlasConfig;
//import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
//import kenkron.antiqueatlasoverlay.OverlayRenderer;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.player.ClientPlayerEntity;
//import net.minecraft.client.renderer.FirstPersonRenderer;
//import net.minecraft.client.renderer.IRenderTypeBuffer;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.Hand;
//import net.minecraft.util.HandSide;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.vector.Vector3f;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.RenderHandEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
//
//@OnlyIn(Dist.CLIENT)
//@EventBusSubscriber(value = Dist.CLIENT)
//public abstract class FirstPersonRendererEvent {
//	@OnlyIn(Dist.CLIENT)
//	private static OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();
//	
//	@OnlyIn(Dist.CLIENT)
//	private static FirstPersonRenderer getFPR() {
//		return Minecraft.getInstance().getFirstPersonRenderer();
//	}
//	
//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent
//	public static void renderItemInFirstPerson(RenderHandEvent event) {
//		if (event.getItemStack().getItem() == RegistrarAntiqueAtlas.ATLAS && !AntiqueAtlasConfig.enabled.get()) {
//			boolean flag = event.getHand() == Hand.MAIN_HAND;
//			ClientPlayerEntity player = Minecraft.getInstance().player;
//			HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
//			event.setCanceled(true);
//			event.getMatrixStack().push();
//			
//			if (flag && getFPR().itemStackOffHand.isEmpty()) {
//				renderAtlasInBothHands(event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getInterpolatedPitch(), event.getEquipProgress(), event.getSwingProgress());
//			} else {
//				renderAtlasInOneHand(event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getEquipProgress(), handside, event.getSwingProgress(), event.getItemStack());
//			}
//			
//			event.getMatrixStack().pop();
//		}
//
//	}
//	@OnlyIn(Dist.CLIENT)
//	private static void renderAtlasInBothHands(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
//		float f = MathHelper.sqrt(swingProgress);
//		float g = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
//		float h = -0.4F * MathHelper.sin(f * 3.1415927F);
//		matrices.translate(0.0D, -g / 2.0F, h);
//		float i = getFPR().getMapAngleFromPitch(pitch);
//		matrices.translate(0.0D, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.7200000286102295D);
//		matrices.rotate(Vector3f.XP.rotationDegrees(i * -85.0F));
//
//		if (!Minecraft.getInstance().player.isInvisible()) {
//			matrices.push();
//			matrices.rotate(Vector3f.YP.rotationDegrees(90.0F));
//			getFPR().renderArm(matrices, vertexConsumers, light, HandSide.RIGHT);
//			getFPR().renderArm(matrices, vertexConsumers, light, HandSide.LEFT);
//			matrices.pop();
//		}
//
//		float j = MathHelper.sin(f * 3.1415927F);
//		matrices.rotate(Vector3f.XP.rotationDegrees(j * 20.0F));
//		matrices.scale(0.5F, 0.5F, 1.0F);
//
//		renderFirstPersonAtlas(matrices, vertexConsumers, light, getFPR().itemStackMainHand);
//	}
//
//	@OnlyIn(Dist.CLIENT)
//	private static void renderAtlasInOneHand(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, float equipProgress, HandSide arm, float swingProgress, ItemStack item) {
////		float f = arm == HandSide.RIGHT ? 1.0F : -1.0F;
////	      matrices.translate((double)(f * 0.125F), -0.125D, 0.0D);
////	      if (!Minecraft.getInstance().player.isInvisible()) {
////	         matrices.push();
////	         matrices.rotate(Vector3f.ZP.rotationDegrees(f * 10.0F));
////	         getFPR().renderArmFirstPerson(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
////	         matrices.pop();
////	      }
////
////	      matrices.push();
////	      matrices.translate((double)(f * 0.51F), (double)(-0.08F + equipProgress * -1.2F), -0.75D);
////	      float f1 = MathHelper.sqrt(swingProgress);
////	      float f2 = MathHelper.sin(f1 * (float)Math.PI);
////	      float f3 = -0.5F * f2;
////	      float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
////	      float f5 = -0.3F * MathHelper.sin(swingProgress * (float)Math.PI);
////	      matrices.translate((double)(f * f3), (double)(f4 - 0.3F * f2), (double)f5);
////	      matrices.rotate(Vector3f.XP.rotationDegrees(f2 * -45.0F));
////	      matrices.rotate(Vector3f.YP.rotationDegrees(f * f2 * -30.0F));
////	      renderFirstPersonAtlas(matrices, vertexConsumers, light, item);
////	      matrices.pop();
//	}
//
//	@OnlyIn(Dist.CLIENT)
//	private static void renderFirstPersonAtlas(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, ItemStack mainHand) {
//		matrices.rotate(Vector3f.YP.rotationDegrees(180.0F));
//		matrices.rotate(Vector3f.ZP.rotationDegrees(180.0F));
//
//		matrices.scale(0.38F, 0.38F, 0.38F);
//		matrices.translate(-1.85D, -0.5D, 0.0D);
//		matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
//
//		atlasOverlayRenderer.drawOverlay(matrices);
//	}
//}
