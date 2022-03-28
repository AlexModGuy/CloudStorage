package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.client.model.BadloonHandModel;
import com.github.alexthe668.cloudstorage.entity.BadloonHandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderBadloonHand extends EntityRenderer<BadloonHandEntity> {
    private static final ResourceLocation GLOVE_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/balloon/glove.png");
    private static final BadloonHandModel GLOVE_MODEL = new BadloonHandModel();

    public RenderBadloonHand(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getTextureLocation(BadloonHandEntity p_114482_) {
        return GLOVE_TEXTURE;
    }

    public void render(BadloonHandEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.translate(0F, 0.4F, 0F);
        float rotX = 0F;
        if(entityIn.getGesture().appliesXRot()){
            rotX = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        }
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180F + rotX));
        matrixStackIn.translate(0F, -0.1F, 0F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entityIn)));
        GLOVE_MODEL.setupAnim(entityIn, 0, 0, entityIn.tickCount + partialTicks, entityYaw, 0);
        GLOVE_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

}
