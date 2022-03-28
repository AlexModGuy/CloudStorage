package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe668.cloudstorage.client.model.BalloonModel;
import com.github.alexthe668.cloudstorage.entity.BadloonEntity;
import com.github.alexthe668.cloudstorage.entity.BadloonHandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class RenderBadloon extends MobRenderer<BadloonEntity, BalloonModel<BadloonEntity>> {

    public RenderBadloon(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BalloonModel(), 0.3F);
        this.addLayer(new LayerFace(this));
    }

    protected float getFlipDegrees(BadloonEntity entity) {
        return 0.0F;
    }

    public void render(BadloonEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        int color = entityIn.getBalloonColor();
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        this.model.setColor(r, g, b);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        this.model.setColor(1.0F, 1.0F, 1.0F);
        if (entityIn.getHandForRendering() instanceof BadloonHandEntity hand) {
            double d0 = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
            double d1 = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
            double d2 = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
            float xRot = entityIn.xRotO + (entityIn.getXRot() - entityIn.xRotO) * partialTicks;
            float yRot = entityIn.yRotO + (entityIn.getYRot() - entityIn.yRotO) * partialTicks;
            float zRot = entityIn.prevRotZ + (entityIn.getRotZ() - entityIn.prevRotZ) * partialTicks;
            double d3 = Mth.lerp(partialTicks, hand.xOld, hand.getX());
            double d4 = Mth.lerp(partialTicks, hand.yOld, hand.getY());
            double d5 = Mth.lerp(partialTicks, hand.zOld, hand.getZ());
            matrixStackIn.pushPose();
            matrixStackIn.translate(-d0, -d1, -d2);
            Vec3 position = new Vec3(d0, d1, d2);
            Vec3 handPosition = new Vec3(d3, d4, d5);
            Vec3 from = new Vec3(0, 0.2F, 0).xRot(xRot * ((float) Math.PI / 180F)).yRot(-yRot * ((float) Math.PI / 180F)).zRot(-zRot * ((float) Math.PI / 180F));
            Vec3 to = handPosition.add(0.0F, hand.getBbHeight() * 0.8F, 0);
            StringRenderHelper.renderSting(entityIn, position.add(from), partialTicks, matrixStackIn, bufferIn, to);
            matrixStackIn.popPose();
        }
    }

    protected void setupRotations(BadloonEntity entity, PoseStack poseStack, float unused, float yRotIn, float partialTicks) {
        if (this.isShaking(entity)) {
            yRotIn += (float) (Math.cos((double) entity.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }

        Pose pose = entity.getPose();
        if (pose != Pose.SLEEPING) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yRotIn));
        }

        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.isAutoSpinAttack()) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entity.getXRot()));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(((float) entity.tickCount + partialTicks) * -75.0F));
        } else if (isEntityUpsideDown(entity)) {
            poseStack.translate(0.0D, entity.getBbHeight() + 0.1F, 0.0D);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        }

    }

    public ResourceLocation getTextureLocation(BadloonEntity entity) {
        return entity.isAlive() ? BalloonTextures.BALLOON : BalloonTextures.POPPED;
    }

    class LayerFace extends RenderLayer<BadloonEntity, BalloonModel<BadloonEntity>> {

        public LayerFace(RenderBadloon render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, BadloonEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            this.getParentModel().setColor(1.0F, 1.0F, 1.0F);
            VertexConsumer stringBuilder = bufferIn.getBuffer(RenderType.entityCutout(BalloonTextures.STRING_TIE));
            this.getParentModel().renderToBuffer(matrixStackIn, stringBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1);
            VertexConsumer sheenBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.BALLOON_SHEEN));
            this.getParentModel().renderToBuffer(matrixStackIn, sheenBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 0.75F);
            ResourceLocation face;
            switch (entity.getFace()) {
                case ANGRY:
                    face = BalloonTextures.FACE_ANGRY;
                    break;
                case SCARED:
                    face = BalloonTextures.FACE_SCARED;
                    break;
                default:
                    face = BalloonTextures.FACE_NEUTRAL;
                    break;
            }
            int color = entity.getBalloonColor();
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            this.getParentModel().setColor(r, g, b);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(face));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1, 1, 1, 1);
        }
    }
}
