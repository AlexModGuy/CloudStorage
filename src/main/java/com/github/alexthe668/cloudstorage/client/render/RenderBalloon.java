package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.block.CSBlockRegistry;
import com.github.alexthe668.cloudstorage.client.model.BalloonModel;
import com.github.alexthe668.cloudstorage.entity.BalloonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RenderBalloon extends EntityRenderer<BalloonEntity> {
    private static final BalloonModel BALLOON_MODEL = new BalloonModel();

    public RenderBalloon(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(BalloonEntity balloon) {
        return balloon.isPopped() ? BalloonTextures.POPPED : BalloonTextures.BALLOON;
    }

    public boolean shouldRender(BalloonEntity entity, Frustum frustum, double x, double y, double z) {
        if (super.shouldRender(entity, frustum, x, y, z)) {
            return true;
        } else {
            Entity tie = entity.getTieForRendering();
            return tie != null && frustum.isVisible(tie.getBoundingBox());
        }
    }

    public void render(BalloonEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        int color = entityIn.getBalloonColor();
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        BALLOON_MODEL.setColor(r, g, b);
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.translate(0F, 1.5F, 0F);
        float rotX = 0F;
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180F + rotX));
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entityIn)));
        BALLOON_MODEL.setupAnim(entityIn, 0, 0, entityIn.tickCount + partialTicks, entityYaw, 0);
        BALLOON_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
        BALLOON_MODEL.setColor(1.0F, 1.0F, 1.0F);
        if(!entityIn.isPopped()){
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.BALLOON_SHEEN));
            BALLOON_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.75F);
        }
        VertexConsumer ivertexbuilder3 = bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.STRING_TIE));
        BALLOON_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder3, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);

        matrixStackIn.popPose();
        double d0 = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
        double d1 = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
        double d2 = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
        float xRot = entityIn.xRotO + (entityIn.getXRot() - entityIn.xRotO) * partialTicks;
        float yRot = entityIn.yRotO + (entityIn.getYRot() - entityIn.yRotO) * partialTicks;
        Vec3 to = new Vec3(d0, d1 - entityIn.getStringLength(), d2);
        if(entityIn.getTieForRendering() != null){
            double d3 = Mth.lerp(partialTicks, entityIn.getTieForRendering().xOld, entityIn.getTieForRendering().getX());
            double d4 = Mth.lerp(partialTicks, entityIn.getTieForRendering().yOld, entityIn.getTieForRendering().getY());
            double d5 = Mth.lerp(partialTicks, entityIn.getTieForRendering().zOld, entityIn.getTieForRendering().getZ());
            double height = entityIn.getTieForRendering().getBbHeight() * 0.5F;
            if(entityIn.getTieForRendering() instanceof AbstractArrow){
                height = 0;
            }
            to = new Vec3(d3, d4, d5).add(0.0F, height, 0);
        }else {
            float ageInTicks = entityIn.tickCount + partialTicks;
            Vec3 swingVec = new Vec3(Math.sin(ageInTicks * 0.1F) * 0.2F, 0, Math.cos(ageInTicks * 0.1F) * 0.2F);
            to = to.add(swingVec);
        }
        matrixStackIn.pushPose();
        matrixStackIn.translate(-d0, -d1, -d2);
        Vec3 position = new Vec3(d0, d1, d2);
        Vec3 from = BALLOON_MODEL.translateToBottom(new Vec3(0, 0.2F, 0)).xRot(xRot * ((float) Math.PI / 180F)).yRot(-yRot * ((float) Math.PI / 180F));
        StringRenderHelper.renderSting(entityIn, to, partialTicks, matrixStackIn, bufferIn, position.add(from), packedLightIn);
        matrixStackIn.popPose();
        float uploadProgress = entityIn.getUploadProgress(partialTicks);
        if(uploadProgress > 0.0F){
            uploadProgress = Math.min(uploadProgress * 2.0F, 1.0F);
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0F, uploadProgress * -entityIn.getStringLength() - 1.0F, 0.0F);
            matrixStackIn.scale(1.1F, 1.1F, 1.1F);
            matrixStackIn.translate(-0.5D, 0.0F, -0.5D);
            BlockState blockstate = entityIn.isCharged() ? CSBlockRegistry.STATIC_CLOUD.get().defaultBlockState() : CSBlockRegistry.CLOUD.get().defaultBlockState();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
            matrixStackIn.popPose();
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

}
