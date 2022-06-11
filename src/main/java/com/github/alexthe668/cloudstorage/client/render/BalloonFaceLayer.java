package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.client.model.BalloonModel;
import com.github.alexthe668.cloudstorage.entity.LivingBalloon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BalloonFaceLayer<T extends LivingEntity & LivingBalloon> extends RenderLayer<T, BalloonModel<T>> {

    public BalloonFaceLayer(RenderLayerParent<T, BalloonModel<T>> render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getParentModel().setColor(1.0F, 1.0F, 1.0F);
        VertexConsumer stringBuilder = bufferIn.getBuffer(RenderType.entityCutout(BalloonTextures.STRING_TIE));
        this.getParentModel().renderToBuffer(matrixStackIn, stringBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1);
        this.getParentModel().setAlpha(entity.getAlpha(partialTicks));
        VertexConsumer sheenBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.BALLOON_SHEEN));
        this.getParentModel().renderToBuffer(matrixStackIn, sheenBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 0.75F);
        ResourceLocation face = BalloonTextures.getTextureForFace(entity.getFace());
        int color = entity.getBalloonColor();
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        this.getParentModel().setColor(r, g, b);
        this.getParentModel().setAlpha(entity.getAlpha(partialTicks));
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(face));
        this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1, 1, 1, 1);
    }
}