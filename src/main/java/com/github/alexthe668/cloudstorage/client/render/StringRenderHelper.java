package com.github.alexthe668.cloudstorage.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class StringRenderHelper {

    private static final RenderType BEAM = RenderType.entityTranslucentCull(new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator_beam.png"));
    private static final float STRING_COLOR_R = 238F / 255F;
    private static final float STRING_COLOR_G = 231F / 255F;
    private static final float STRING_COLOR_B = 225F / 255F;
    private static final float STRING_COLOR_R2 = 1F;
    private static final float STRING_COLOR_G2 = 1F;
    private static final float STRING_COLOR_B2 = 1F;

    private static void addVertexPairAlex(VertexConsumer p_174308_, Matrix4f p_174309_, float p_174310_, float p_174311_, float p_174312_, int packedLight, float p_174317_, float p_174318_, float p_174319_, float p_174320_, int p_174321_, boolean p_174322_) {
        float f = (float) p_174321_ / 24.0F;
        float f2 = STRING_COLOR_R;
        float f3 = STRING_COLOR_G;
        float f4 = STRING_COLOR_B;
        if (p_174321_ % 2 == (p_174322_ ? 1 : 0)) {
            f2 = STRING_COLOR_R2;
            f3 = STRING_COLOR_G2;
            f4 = STRING_COLOR_B2;
        }
        float f5 = p_174310_ * f;
        float f6 = p_174311_ > 0.0F ? p_174311_ * f * f : p_174311_ - p_174311_ * (1.0F - f) * (1.0F - f);
        float f7 = p_174312_ * f;
        p_174308_.vertex(p_174309_, f5 - p_174319_, f6 + p_174318_, f7 + p_174320_).color(f2, f3, f4, 1.0F).uv2(packedLight).endVertex();
        p_174308_.vertex(p_174309_, f5 + p_174319_, f6 + p_174317_ - p_174318_, f7 - p_174320_).color(f2, f3, f4, 1.0F).uv2(packedLight).endVertex();
    }

    public static <E extends Entity> void renderSting(Entity from, Vec3 fromVec, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 to, int lightIn) {
        poseStack.pushPose();
        double d3 = fromVec.x;
        double d4 = fromVec.y;
        double d5 = fromVec.z;
        poseStack.translate(d3, d4, d5);
        float f = (float) (to.x - d3);
        float f1 = (float) (to.y - d4);
        float f2 = (float) (to.z - d5);
        float f3 = 0.025F;
        VertexConsumer vertexconsumer = bufferSource.getBuffer(CSRenderTypes.BALLOON_STRING);
        Matrix4f matrix4f = poseStack.last().pose();
        float width = 0.025F;
        BlockPos blockpos = new BlockPos(fromVec);
        BlockPos blockpos1 = new BlockPos(to);
        for (int i1 = 0; i1 <= 24; ++i1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, lightIn, width, width, width, width, i1, false);
        }
        for (int j1 = 24; j1 >= 0; --j1) {
            addVertexPairAlex(vertexconsumer, matrix4f, f, f1, f2, lightIn, width, width, width, width, j1, true);
        }
        poseStack.popPose();
    }


    public static void renderBloviatorBeam(float p_114188_, float p_114189_, float p_114190_, float p_114191_, int p_114192_, PoseStack p_114193_, MultiBufferSource p_114194_, int p_114195_, float intensity, float alpha) {
        float f = Mth.sqrt(p_114188_ * p_114188_ + p_114190_ * p_114190_);
        float f1 = Mth.sqrt(p_114188_ * p_114188_ + p_114189_ * p_114189_ + p_114190_ * p_114190_);
        p_114193_.pushPose();
        p_114193_.mulPose(Vector3f.YP.rotation((float)(-Math.atan2((double)p_114190_, (double)p_114188_)) + ((float)Math.PI / 2F)));
        p_114193_.mulPose(Vector3f.XN.rotation((float)(-Math.atan2((double)f, (double)p_114189_)) - ((float)Math.PI / 2F)));
        VertexConsumer vertexconsumer = p_114194_.getBuffer(BEAM);
        float f2 = ((float)p_114192_ + p_114191_) * -0.04F * intensity;
        float f3 = Mth.sqrt(p_114188_ * p_114188_ + p_114189_ * p_114189_ + p_114190_ * p_114190_) / 16.0F + ((float)p_114192_ + p_114191_) * -0.04F * intensity;
        float f4 = 0F;
        float f5 = 0.2F;
        float f6 = 0.0F;
        PoseStack.Pose posestack$pose = p_114193_.last();
        Matrix4f matrix4f = posestack$pose.pose();
        for(int j = 1; j <= 8; ++j) {
            Matrix3f matrix3f = posestack$pose.normal();
            float f7 = Mth.cos((float)Math.PI + (float)j * ((float)Math.PI * 2F) / 8.0F) * 0.75F;
            float f8 = Mth.sin((float)Math.PI + (float)j * ((float)Math.PI * 2F) / 8.0F) * 0.75F;
            float f9 = (float)j / 4.0F;
            vertexconsumer.vertex(matrix4f, f4 * 0.2F, f5 * 0.2F, 0.0F).color(255, 255, 255, (int)(alpha * 255)).uv(f6, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f4, f5 * intensity, f1 - 0.5F).color(255, 255, 255, 0).uv(f6, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal(matrix3f, 0.0F, -1F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f7, f8 * intensity, f1 - 0.5F).color(255, 255, 255, 0).uv(f9, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal(matrix3f, 0.0F, -1F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f7 * 0.2F, f8 * 0.2F, 0.0F).color(255, 255, 255, (int)(alpha * 255)).uv(f9, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114195_).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }

        p_114193_.popPose();
    }
}
