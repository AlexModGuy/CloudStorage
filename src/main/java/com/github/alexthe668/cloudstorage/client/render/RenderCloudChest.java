package com.github.alexthe668.cloudstorage.client.render;

import com.github.alexthe668.cloudstorage.block.AbstractCloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.block.CloudChestBlock;
import com.github.alexthe668.cloudstorage.block.CloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.block.StaticCloudChestBlockEntity;
import com.github.alexthe668.cloudstorage.client.model.BalloonModel;
import com.github.alexthe668.cloudstorage.client.model.CloudChestModel;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RenderCloudChest<T extends AbstractCloudChestBlockEntity> implements BlockEntityRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("cloudstorage:textures/entity/cloud_chest.png");
    private static final ResourceLocation TEXTURE_LIGHTNING = new ResourceLocation("cloudstorage:textures/entity/cloud_chest_static.png");
    private static CloudChestModel CHEST_MODEL = new CloudChestModel();
    private static final BalloonModel BALLOON_MODEL = new BalloonModel();

    public RenderCloudChest(Context rendererDispatcherIn) {
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        Direction dir = tileEntityIn.getBlockState().getValue(CloudChestBlock.HORIZONTAL_FACING);
        if(dir == Direction.NORTH){
            matrixStackIn.translate(0.5, 1.5F, 0.5F);
        }else if(dir == Direction.EAST){
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        }else if(dir == Direction.SOUTH){
            matrixStackIn.translate(0.5, 1.5F, 0.5F);
        }else if(dir == Direction.WEST){
            matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        }
        if(tileEntityIn.lastValidPlayer != null && tileEntityIn.hasBalloonFor(tileEntityIn.lastValidPlayer)){
            float ageInTicks = tileEntityIn.tickCount + partialTicks;
            float emerge = tileEntityIn.getEmergence(partialTicks);
            float scale = Mth.clamp( Mth.sqrt(emerge), 0.5F, 1.0F);
            matrixStackIn.pushPose();
            Vec3 swingVec = new Vec3(Math.sin(ageInTicks * 0.1F) * 0.2F, 0, Math.cos(ageInTicks * 0.1F) * 0.2F).scale(emerge);
            int color = tileEntityIn.getBalloonFor(tileEntityIn.lastValidPlayer);
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            BALLOON_MODEL.setColor(r, g, b);
            matrixStackIn.pushPose();
            matrixStackIn.scale(scale, scale, scale);
            matrixStackIn.translate(swingVec.x, 2.8F * emerge - 1.3F, swingVec.z);
            float rotX = 0F;
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180F + rotX));
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(BalloonTextures.BALLOON));
            BALLOON_MODEL.resetToDefaultPose();
            BALLOON_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
            BALLOON_MODEL.setColor(1.0F, 1.0F, 1.0F);
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.BALLOON_SHEEN));
            BALLOON_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder2, combinedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.75F);
            VertexConsumer ivertexbuilder3 = bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.STRING_TIE));
            BALLOON_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder3, combinedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
            if(tileEntityIn.hasLootBalloon()){
                VertexConsumer ivertexbuilder4 = bufferIn.getBuffer(RenderType.entityTranslucent(BalloonTextures.LOOT));
                BALLOON_MODEL.renderToBuffer(matrixStackIn, ivertexbuilder4, combinedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);
            }
            matrixStackIn.popPose();
            matrixStackIn.pushPose();
            matrixStackIn.translate(0, -1.5F, 0);
            Vec3 to = new Vec3(swingVec.x, 1.5F * emerge + 0.2F, swingVec.z);
            StringRenderHelper.renderSting(Minecraft.getInstance().player, to, partialTicks, matrixStackIn, bufferIn, Vec3.ZERO);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
        matrixStackIn.translate(0.0F, 0.0001F, 0.0F);
        matrixStackIn.mulPose(dir.getOpposite().getRotation());
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStackIn.pushPose();
        CHEST_MODEL.renderChest(tileEntityIn, partialTicks);
        CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), combinedLightIn, OverlayTexture.NO_OVERLAY, 1, 1F, 1, 1.0F);
        if(tileEntityIn instanceof StaticCloudChestBlockEntity){
            CHEST_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.dragonExplosionAlpha(TEXTURE_LIGHTNING)), 240, OverlayTexture.NO_OVERLAY, 1, 1F, 1, 1.0F);
        }
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
