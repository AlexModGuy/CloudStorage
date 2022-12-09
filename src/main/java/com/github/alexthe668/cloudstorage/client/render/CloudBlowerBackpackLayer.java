package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.client.model.CloudBlowerBackpackModel;
import com.github.alexthe668.cloudstorage.item.CSItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class CloudBlowerBackpackLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {

    private static final CloudBlowerBackpackModel CLOUD_BLOWER_BACKPACK_MODEL = new CloudBlowerBackpackModel();
    public static final ResourceLocation CLOUD_BLOWER_BACKPACK_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/cloud_blower_backpack.png");

    public CloudBlowerBackpackLayer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity, float f1, float f2, float f3, float f4, float f5, float f6) {
        if(entity instanceof Player player && (player.getMainHandItem().is(CSItemRegistry.CLOUD_BLOWER.get()) || player.getOffhandItem().is(CSItemRegistry.CLOUD_BLOWER.get()))){
            ItemStack itemStack = player.getMainHandItem().is(CSItemRegistry.CLOUD_BLOWER.get()) ? player.getMainHandItem() : player.getOffhandItem();
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(CLOUD_BLOWER_BACKPACK_TEXTURE), false, itemStack.hasFoil());
            Vec3 body = Vec3.ZERO;
            poseStack.pushPose();
            if(getParentModel() instanceof HumanoidModel humanoidModel) {
                humanoidModel.body.translateAndRotate(poseStack);
            }
            poseStack.translate(0, -0.75F, 0.45F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            CLOUD_BLOWER_BACKPACK_MODEL.resetToDefaultPose();
            CLOUD_BLOWER_BACKPACK_MODEL.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();

            if(isRightHand(entity)){
                poseStack.pushPose();
                Vec3 rightHandPos = Vec3.ZERO;
                if(getParentModel() instanceof HumanoidModel humanoidModel) {
                    PoseStack armStack = new PoseStack();
                    armStack.pushPose();
                    humanoidModel.rightArm.translateAndRotate(armStack);
                    Vector4f armOffsetVec = new Vector4f(-0.05F, 0.6F, 0F, 1.0F);
                    armOffsetVec.mul(armStack.last().pose());
                    rightHandPos = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
                    armStack.popPose();

                    PoseStack bodyStack = new PoseStack();
                    humanoidModel.body.translateAndRotate(bodyStack);
                    Vector4f bodyOffsetVec = new Vector4f(-0.2F, 0.6F, 0.4F, 1.0F);
                    bodyOffsetVec.mul(bodyStack.last().pose());
                    body = new Vec3(bodyOffsetVec.x(), bodyOffsetVec.y(), bodyOffsetVec.z());
                    bodyStack.popPose();
                }
                poseStack.popPose();
                StringRenderHelper.renderWire(player, body, f3, poseStack, multiBufferSource, rightHandPos, 3F, packedLight);
            }
            if(isLeftHand(entity)){
                poseStack.pushPose();
                Vec3 leftHandPos = Vec3.ZERO;
                if(getParentModel() instanceof HumanoidModel humanoidModel) {
                    PoseStack armStack = new PoseStack();
                    armStack.pushPose();
                    humanoidModel.leftArm.translateAndRotate(armStack);
                    Vector4f armOffsetVec = new Vector4f(0.05F, 0.6F, 0F, 1.0F);
                    armOffsetVec.mul(armStack.last().pose());
                    leftHandPos = new Vec3(armOffsetVec.x(), armOffsetVec.y(), armOffsetVec.z());
                    armStack.popPose();

                    PoseStack bodyStack = new PoseStack();
                    humanoidModel.body.translateAndRotate(bodyStack);
                    Vector4f bodyOffsetVec = new Vector4f(0.2F, 0.6F, 0.4F, 1.0F);
                    bodyOffsetVec.mul(bodyStack.last().pose());
                    body = new Vec3(bodyOffsetVec.x(), bodyOffsetVec.y(), bodyOffsetVec.z());
                    bodyStack.popPose();
                }
                poseStack.popPose();
                StringRenderHelper.renderWire(player, leftHandPos, f3, poseStack, multiBufferSource, body, 1F, packedLight);
            }
        }
    }

    public boolean isLeftHand(LivingEntity entity){
        boolean leftHand = false;
        if (entity.getItemInHand(InteractionHand.MAIN_HAND).is(CSItemRegistry.CLOUD_BLOWER.get())) {
            leftHand = leftHand || entity.getMainArm() == HumanoidArm.LEFT;
        }
        if (entity.getItemInHand(InteractionHand.OFF_HAND).is(CSItemRegistry.CLOUD_BLOWER.get())) {
            leftHand = leftHand || entity.getMainArm() != HumanoidArm.LEFT;
        }
        return leftHand;
    }

    public boolean isRightHand(LivingEntity entity){
        boolean rightHand = false;
        if (entity.getItemInHand(InteractionHand.MAIN_HAND).is(CSItemRegistry.CLOUD_BLOWER.get())) {
            rightHand = rightHand|| entity.getMainArm() == HumanoidArm.RIGHT;
        }
        if (entity.getItemInHand(InteractionHand.OFF_HAND).is(CSItemRegistry.CLOUD_BLOWER.get())) {
            rightHand = rightHand || entity.getMainArm() != HumanoidArm.RIGHT;
        }
        return rightHand;
    }
}