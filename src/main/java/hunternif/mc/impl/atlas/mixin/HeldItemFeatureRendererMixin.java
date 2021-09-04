package hunternif.mc.impl.atlas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.impl.atlas.client.OverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3f;


@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends FeatureRenderer<T, M> {

    public HeldItemFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }
    
    private OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();

    @Inject(at = @At("HEAD"), method = "renderItem", cancellable = true)
    private void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (arm == entity.getMainArm() && entity.getMainHandStack().getItem().equals(RegistrarAntiqueAtlas.ATLAS)) { // Mainhand with or without the offhand
            matrices.push();
            ((ModelWithArms) getContextModel()).setArmAngle(arm, matrices);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(200.0f));
            boolean bl = arm.equals(Arm.LEFT);
            matrices.translate((double) ((float) (bl ? -1 : 1) / 16.0f), 0.125, -0.625);
            renderThirdPersonAtlas(matrices, vertexConsumers, light, stack, !entity.getOffHandStack().isEmpty(), bl);
            matrices.pop();
            info.cancel();
            return;
        }
        if (arm != entity.getMainArm() && entity.getOffHandStack().getItem().equals(RegistrarAntiqueAtlas.ATLAS)) { // Only offhand
            matrices.push();
            ((ModelWithArms) getContextModel()).setArmAngle(arm, matrices);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(200.0f));
            boolean bl = arm.equals(Arm.LEFT);
            matrices.translate((double) ((float) (bl ? -1 : 1) / 16.0f), 0.125, -0.625);
            renderThirdPersonAtlas(matrices, vertexConsumers, light, stack, true, bl);
            matrices.pop();
            info.cancel();
            return;
        }
    }
    
    private void renderThirdPersonAtlas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack item, boolean smallMap, boolean leftHanded) {
        if (smallMap) {
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(160.0f));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
            matrices.scale(0.38f/3, 0.38f/3, 0.38f/3);
            
            matrices.translate(-0.8, -3.2, 0.0);
            matrices.scale(0.0098125f, 0.0098125f, 0.0098125f);
        } else {
//            if(leftHanded) {
//                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(160.0f));
//                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(150.0f));
//                matrices.scale(0.38f/3, 0.38f/3, 0.38f/3);
//                
//                matrices.translate(+0.5, -1.3, 0.0);
//            } else {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(160.0f));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
                matrices.scale(0.38f/3, 0.38f/3, 0.38f/3);
                
                matrices.translate(-4.5, -4.2, 0.0);
//            }

            matrices.scale(0.0138125f, 0.0138125f, 0.0138125f);
        }

        atlasOverlayRenderer.drawOverlay(matrices, vertexConsumers, light, item);
    }

}