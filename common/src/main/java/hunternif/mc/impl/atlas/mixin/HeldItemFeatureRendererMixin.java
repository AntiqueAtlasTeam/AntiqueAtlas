/**
 * Modified from https://github.com/tr7zw/AntiqueAtlas/blob/1.17-fabric-3rd-person-render/src/main/java/hunternif/mc/impl/atlas/mixin/HeldItemFeatureRendererMixin.java
 */
package hunternif.mc.impl.atlas.mixin;

import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.client.OverlayRenderer;
import hunternif.mc.impl.atlas.client.Textures;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import hunternif.mc.impl.atlas.item.AntiqueAtlasItems;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends FeatureRenderer<T, M> {

    public HeldItemFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    private final OverlayRenderer atlasOverlayRenderer = new OverlayRenderer();

    @Inject(at = @At("HEAD"), method = "renderItem", cancellable = true)
    private void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode transformationMode, Arm arm,
                            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        Item atlas= AntiqueAtlasItems.ATLAS.getOrNull();
        if (atlas == null) {
            return;
        }
        if (!AntiqueAtlasMod.CONFIG.showOthersAtlas && entity instanceof OtherClientPlayerEntity) {
            return;
        }
        boolean mainHand = arm == entity.getMainArm() && entity.getMainHandStack().isOf(atlas);
        boolean offHand = arm != entity.getMainArm() && entity.getOffHandStack().isOf(atlas);
        if (mainHand || offHand) {
            matrices.push();
            ((ModelWithArms) getContextModel()).setArmAngle(arm, matrices);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0f));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(200.0f));
            renderThirdPersonAtlas(matrices, vertexConsumers, light, stack, !entity.getOffHandStack().isEmpty() || offHand, arm.equals(Arm.LEFT));
            matrices.pop();
            info.cancel();
        }
    }

    private void renderThirdPersonAtlas(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack item, boolean smallMap, boolean leftHand) {
        matrices.translate((leftHand ? -1 : 1) / 16.0, 0.125, -0.625);
        float positiveZ = 210.0f;
        double translateX = -3.6;
        double translateY = -4.8;
        float scale = 0.012f;
        if (smallMap) {
            positiveZ = 180.0f;
            translateX = -0.8;
            translateY = -3.2;
            scale = 0.0098125f;
        } else if (leftHand) {
            positiveZ = 150.0f;
            translateX = 1.2;
            translateY = -3.0;
        }
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(160.0f));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(positiveZ));
        matrices.scale(0.38f/3, 0.38f/3, 0.38f/3);

        matrices.translate(translateX, translateY, 0.0);
        matrices.scale(scale, scale, scale);

        // Render cover
        Textures.BOOK_COVER.drawWithLight(vertexConsumers, matrices, 0, 0, (int) (GuiAtlas.WIDTH * 1.5), (int) (GuiAtlas.HEIGHT * 1.5), light);

        atlasOverlayRenderer.drawOverlay(matrices, vertexConsumers, light, item);
    }

}
