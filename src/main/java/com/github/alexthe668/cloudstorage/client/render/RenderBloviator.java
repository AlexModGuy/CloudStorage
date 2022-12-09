package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.github.alexthe668.cloudstorage.client.model.BloviatorModel;
import com.github.alexthe668.cloudstorage.entity.BloviatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class RenderBloviator extends MobRenderer<BloviatorEntity, BloviatorModel> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator.png");
    public static final ResourceLocation BLOWING_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator_blowing.png");
    public static final ResourceLocation THUNDER_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator_thunder.png");
    public static final ResourceLocation STATIC_TEXTURE = new ResourceLocation("cloudstorage:textures/entity/bloviator/bloviator_static.png");
    private LightningRender lightningRender = new LightningRender();
    private static Map<Integer, BloviatorModel> CLOUDCOUNT_TO_MODEL = new HashMap<>();

    public RenderBloviator(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new BloviatorModel(5), 0.3F);
        this.addLayer(new LayerThunder(this));
    }

    protected void scale(BloviatorEntity entity, PoseStack stack, float partialTick) {
        float f = entity.getCloudScale();
        stack.scale(f, f, f);

    }

    public boolean shouldRender(BloviatorEntity entity, Frustum frustum, double x, double y, double z) {
        if (super.shouldRender(entity, frustum, x, y, z)) {
            return true;
        } else {
            Entity push = entity.getPushingEntity();
            Entity shock = entity.getShockingEntity();
            if(push != null && (frustum.isVisible(push.getBoundingBox()) || push == Minecraft.getInstance().player)){
                return true;
            }
            if(shock != null && (frustum.isVisible(shock.getBoundingBox()) || shock == Minecraft.getInstance().player)){
                return true;
            }
        }
        return false;
    }

    public void render(BloviatorEntity entityIn, float entityYaw, float f, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        int count = entityIn.getCloudCount();
        if(CLOUDCOUNT_TO_MODEL.get(count) == null){
            CLOUDCOUNT_TO_MODEL.put(count, new BloviatorModel(count));
        }
        this.model = CLOUDCOUNT_TO_MODEL.get(count);

        Entity blow = entityIn.getPushingEntity();
        Entity struck = entityIn.getShockingEntity();
        float partialTicks = Minecraft.getInstance().getFrameTime();
        double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
        double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
        double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
        if(struck != null) {
            matrixStackIn.pushPose();
            Vec3 toVec = struck.position().subtract(x, y, z);
            float alpha = (entityIn.getShockTime())/ 5F;
            int segCount = 2 + entityIn.getCloudCount() * 2;
            LightningBoltData.BoltRenderInfo lightningBoltData = new LightningBoltData.BoltRenderInfo(0.15F, 0.1F, 0.5F, 0.85F, new Vector4f(0.3F, 0.45F, 0.6F, alpha * 0.5F), 0.1F);
            LightningBoltData bolt = new LightningBoltData(lightningBoltData, Vec3.ZERO, toVec, segCount)
                    .size(0.1F + 0.2F * entityIn.getCloudScale())
                    .lifespan(5)
                    .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE);
            lightningRender.update(entityIn, bolt, partialTicks);
            lightningRender.render(partialTicks, matrixStackIn, bufferIn);
            matrixStackIn.popPose();
        }
        if(blow != null) {
            matrixStackIn.pushPose();
            Vec3 vec3 = entityIn.getMouthVec(partialTicks);
            Vec3 vec31 = vec3.subtract(x, y, z);
            double d3 = Mth.lerp(partialTicks, blow.xOld, blow.getX());
            double d4 = Mth.lerp(partialTicks, blow.yOld, blow.getY()) + blow.getEyeHeight();
            double d5 = Mth.lerp(partialTicks, blow.zOld, blow.getZ());
            float f6 = (float) (vec3.x - d3);
            float f7 = (float) (vec3.y - d4);
            float f8 = (float) (vec3.z - d5);
            matrixStackIn.translate(vec31.x, vec31.y, vec31.z);
            StringRenderHelper.renderBloviatorBeam(f6, f7, f8, partialTicks, entityIn.tickCount, matrixStackIn, bufferIn, packedLightIn, entityIn.getCloudScale(), entityIn.getPushProgress(partialTicks));
            matrixStackIn.popPose();
        }
        super.render(entityIn, entityYaw, f, matrixStackIn, bufferIn, packedLightIn);
    }

    protected float getFlipDegrees(BloviatorEntity entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(BloviatorEntity entity) {
        return entity.isPushing() ? BLOWING_TEXTURE : TEXTURE;
    }

    class LayerThunder extends RenderLayer<BloviatorEntity, BloviatorModel> {

        public LayerThunder(RenderBloviator render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, BloviatorEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer sheenBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(THUNDER_TEXTURE));
            this.getParentModel().renderToBuffer(matrixStackIn, sheenBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, entity.getTransformProgress(partialTicks));
            if(entity.isThundery()){
                VertexConsumer staticBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(STATIC_TEXTURE));
                this.getParentModel().renderToBuffer(matrixStackIn, staticBuilder, 240, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, entity.getChargeTimeLerp(partialTicks) / entity.getMaxChargeTime());
            }
        }
    }
}
